package org.definilizer.lib;

/**
 * User: dima
 * Date: Aug 30, 2008
 * Time: 12:30:16 PM
 */
public final class FinalDataProcessor {
    private static FinalDataProcessor singleInstance;

    public static FinalDataProcessor getInstance() {
        if (singleInstance == null) {
            singleInstance = new FinalDataProcessor();
        }
        return singleInstance;
    }

    private FinalDataProcessor() {
    }

    public String retrieveUpdate() {
        return "update from far away";
    }
}