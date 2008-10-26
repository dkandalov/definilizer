package org.definilizer;

import org.definilizer.asm.FinalRemover;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * User: dima
 * Date: Sep 9, 2008
 * Time: 10:13:18 PM
 */
public class ClassLoaderForTesting extends ClassLoader {
    private static List<String> CLASSES_TO_FIX = Arrays.asList(
            "home.lib.FinalDataProcessor",
            "home.lib.FinalStaticDataProcessor"
    );

    /**
     * @param classLoader parent classloader. If this (it will be the "original" system classloader)
     */
    public ClassLoaderForTesting(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected synchronized Class<?> loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {
        if (isSystemClass(name)) {
            return super.loadClass(name, resolve);
        }
        return loadClassManually(name);
    }

    private Class<?> loadClassManually(final String className) throws ClassNotFoundException {
        byte[] classAsBytes = getClassAsBytes(className);
        if (CLASSES_TO_FIX.contains(className)) {
            classAsBytes = fixClassStructure(classAsBytes);
        }
        return defineClass(null, classAsBytes, 0, classAsBytes.length);
    }

    private static byte[] fixClassStructure(byte[] classAsBytes) {
        classAsBytes = new FinalRemover().process(classAsBytes);
//        classAsBytes = new StaticInitRemover().process(classAsBytes);
        return classAsBytes;
    }

    private static boolean isSystemClass(final String name) {
//        return !name.startsWith("home.");
        return name.startsWith("java.") || name.startsWith("javax.") ||
                name.startsWith("sun.") || name.startsWith("sunw.");
    }

    private static byte[] getClassAsBytes(final String className) throws ClassNotFoundException {
        try {
            byte[] bytes = new byte[100000]; // FIXME don't allocate constant amount
            final String fileName = classNameToFileName(className);
            final InputStream stream = getSystemResourceAsStream(fileName);
            if (stream == null) throw new IOException("Cannot find " + fileName);

            int i;
            int counter = 0;
            while ((i = stream.read()) != -1) {
                bytes[counter] = (byte) i;
                counter++;
            }

            byte[] result = new byte[counter];
            System.arraycopy(bytes, 0, result, 0, counter);
            return result;
        } catch (IOException e) {
            throw new ClassNotFoundException("", e);
        }
    }

    private static String classNameToFileName(final String className) {
        return className.replace(".", "/") + ".class";
    }
}
