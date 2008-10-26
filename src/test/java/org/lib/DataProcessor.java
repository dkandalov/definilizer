package org.lib;

/**
 * User: dima
 * Date: Aug 30, 2008
 * Time: 12:30:16 PM
 */
public class DataProcessor {
    private static DataProcessor singleInstance;

    public static DataProcessor getInstance() {
        if (singleInstance == null) {
            singleInstance = new DataProcessor();
        }
        return singleInstance;
    }

    private DataProcessor() {
    }

    public String retrieveUpdate() {
        return "update from far away";
    }

    public Data applyUpdate(final Data data, final String update) {
        return null;
    }
}
