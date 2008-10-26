package org.definilizer.lib;

/**
 * User: dima
 * Date: Sep 8, 2008
 * Time: 11:24:42 PM
 */
public class DefaultDataMunger implements DataMunger {
    private static DataMunger singleInstance;

    public static DataMunger getInstance() {
        if (singleInstance == null) {
            singleInstance = new DefaultDataMunger();
        }
        return singleInstance;
    }

    private DefaultDataMunger() {}

    public String munge(final Data data) {
        return "I access external resource...";
    }
}
