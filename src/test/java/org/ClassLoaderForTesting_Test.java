package org;

import org.definilizer.ClassLoaderForTesting;
import org.junit.Test;

/**
 * User: dima
 * Date: Sep 9, 2008
 * Time: 10:22:03 PM
 */
public class ClassLoaderForTesting_Test {
    @Test
    public void shouldLoadClassesFromClasspath() throws ClassNotFoundException {
        final ClassLoaderForTesting classLoader = new ClassLoaderForTesting(ClassLoader.getSystemClassLoader());

        classLoader.loadClass("org.lib.Data");
        classLoader.loadClass("org.junit.Test");
        classLoader.loadClass("org.lib.FinalDataProcessor");
        classLoader.loadClass("org.lib.FinalStaticDataProcessor");
    }
}
