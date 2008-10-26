package org.definilizer;

import org.definilizer.asm.FinalRemover;
import org.definilizer.asm.StaticInitRemover;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Classloader which instruments loaded classes for example to remove final modifiers.
 * To use it as a system classloader pass the following parameter to JVM:
 * <pre>
 *      -Djava.system.class.loader=org.definilizer.ClassLoaderForTesting
 * </pre>
 * <p/>
 * User: dima
 * Date: Sep 9, 2008
 * Time: 10:13:18 PM
 *
 * @see #CLASSES_TO_INSTRUMENT
 */
public class ClassLoaderForTesting extends ClassLoader {
    /**
     * List of classes or packages which will be instrumented.
     * If you want some class to be instrumented, you need to add it to this list.
     */
    private static final List<String> CLASSES_TO_INSTRUMENT = Arrays.asList(
            "org.definilizer.lib.FinalDataProcessor",
            "org.definilizer.lib.FinalStaticDataProcessor"
    );
    private static final String[] PACKAGES_NOT_TO_LOAD = {
            "java.", "javax.", "sun."
    };

    /**
     * @param classLoader parent classloader.
     *                    If this classloader is used as a system class loader,
     *                    {@code classLoader} parameter will be the original system classloader.
     */
    public ClassLoaderForTesting(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected synchronized Class<?> loadClass(final String className, final boolean resolve)
            throws ClassNotFoundException {
        // We cannot load just those classes we want to instrument, because junit will load them
        // using classloader with which it was loaded itself. Therefore, we need to load junit classes
        // and everything it depends on; in case of using IDE GUI runner should also be loaded.
        // Since junit uses classes with various package names and IDE plugins are different,
        // it is be simpler to load as much classes as possible.
        //
        // On the other hand, if all classes including java.lang.* are loaded, then JVM fails.
        // Because of it after some experimentation, I added separation between classes we should load
        // and those which should be loaded by original system classloader.

        if (isClassWeShouldNotLoad(className)) {
            return loadClassWithOriginalClassLoader(className, resolve);
        } else {
            return loadClassSkippingParentClassloader(className);
        }
    }

    private Class<?> loadClassWithOriginalClassLoader(final String className, final boolean resolve) throws ClassNotFoundException {
        // use super implementation which will try parent classloader
        // which in this case is the original system classloader
        return super.loadClass(className, resolve);
    }

    private Class<?> loadClassSkippingParentClassloader(final String className) throws ClassNotFoundException {
        byte[] classAsBytes = readClassAsBytes(className);
        if (shouldBeInstrumented(className)) {
            classAsBytes = instrumentClassStructure(classAsBytes);
        }
        return defineClass(null, classAsBytes, 0, classAsBytes.length);
    }

    private static boolean shouldBeInstrumented(final String className) {
        if (CLASSES_TO_INSTRUMENT.contains(className)) return true;

        for (String packageName : CLASSES_TO_INSTRUMENT) {
            if (className.startsWith(packageName)) return true;
        }
        return false;
    }

    private static byte[] instrumentClassStructure(byte[] classAsBytes) {
        classAsBytes = new FinalRemover().process(classAsBytes);
        classAsBytes = new StaticInitRemover().process(classAsBytes);
        return classAsBytes;
    }

    private static boolean isClassWeShouldNotLoad(final String className) {
        for (String packageName : PACKAGES_NOT_TO_LOAD) {
            if (className.startsWith(packageName)) return true;
        }
        return false;
    }

    private byte[] readClassAsBytes(final String className) throws ClassNotFoundException {
        try {
            // use original system classloader with getParent() to read classes since it's aware of classpath
            URL resource = getParent().getResource(convertClassNameToFileName(className));
            BufferedInputStream stream = new BufferedInputStream(resource.openStream());
            byte[] result = new byte[resource.openConnection().getContentLength()];

            int i;
            int counter = 0;
            while ((i = stream.read()) != -1) {
                result[counter] = (byte) i;
                counter++;
            }

            return result;
        } catch (IOException e) {
            throw new ClassNotFoundException("", e);
        }
    }

    private static String convertClassNameToFileName(final String className) {
        return className.replace(".", "/") + ".class";
    }
}
