package org.definilizer;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.definilizer.lib.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

/**
 * User: dima
 * Date: Sep 8, 2008
 * Time: 11:31:31 PM
 */
public class SingletonRemoving_Test {
    private static final Data DONT_CARE_WHAT = null;

    @Test
    public void shouldSubstituteInterfaceBasedSingleton() throws Exception {
        // setup
        DataMunger dataMunger = new DataMunger() {
            public String munge(final Data data) {
                return "I'm a simple implementation";
            }
        };
        setPrivateField(DefaultDataMunger.class, "singleInstance", dataMunger);

        // exercise / verify
        assertEquals("I'm a simple implementation", DefaultDataMunger.getInstance().munge(DONT_CARE_WHAT));
    }

    private static void setPrivateField(final Class aClass, final String fieldName, final Object object)
            throws NoSuchFieldException, IllegalAccessException {
        final Field field = aClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, object);
    }

    @Test
    public void shouldSubstituteClassBasedSingleton() throws Exception {
        // setup
        DataProcessor dummyProcessor = mock(DataProcessor.class);
        stub(dummyProcessor.retrieveUpdate()).toReturn("dummy update");
        setPrivateField(DataProcessor.class, "singleInstance", dummyProcessor);

        // exercise / verify
        assertEquals("dummy update", DataProcessor.getInstance().retrieveUpdate());
    }

    /**
     * Won't work without {@link org.definilizer.ClassLoaderForTesting}
     */
    @Test
    public void shouldSubstituteFinalClassBasedSingleton() throws Exception {
        // setup
        FinalDataProcessor dummyProcessor = mock(FinalDataProcessor.class);
        stub(dummyProcessor.retrieveUpdate()).toReturn("dummy update");
        setPrivateField(FinalDataProcessor.class, "singleInstance", dummyProcessor);

        // exercise / verify
        assertEquals("dummy update", FinalDataProcessor.getInstance().retrieveUpdate());
    }

    /**
     * Won't work without {@link org.definilizer.ClassLoaderForTesting}
     */
    @Test
    @Ignore()
    // TODO figure out why it stopped working
    public void shouldSubstituteFinalStaticClassBasedSingleton() throws Exception {
        // setup
        FinalStaticDataProcessor dummyProcessor = mock(FinalStaticDataProcessor.class);
        stub(dummyProcessor.retrieveUpdate()).toReturn("dummy update");
        setPrivateField(FinalStaticDataProcessor.class, "singleInstance", dummyProcessor);

        // exercise / verify
        assertEquals("dummy update", FinalStaticDataProcessor.getInstance().retrieveUpdate());
    }
}
