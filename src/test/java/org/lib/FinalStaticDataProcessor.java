package org.lib;

/**
 * User: dima
 * Date: Aug 30, 2008
 * Time: 12:30:16 PM
 */
public final class FinalStaticDataProcessor {
    private final static FinalStaticDataProcessor singleInstance = new FinalStaticDataProcessor();

    public static FinalStaticDataProcessor getInstance() {
        return singleInstance;
    }

    private FinalStaticDataProcessor() {
        throw new IllegalStateException("long initialization process happened");
    }

    public String retrieveUpdate() {
        return "update from far away";
    }
}