package com.raoulvdberge.refinedstorage;

public class RSOldConfig {
    //region Autocrafting
    public int calculationTimeoutMs;
    //endregion

    //region Categories
    private static final String AUTOCRAFTING = "autocrafting";
    //endregion

    /*private void loadConfig() {
        //region Autocrafting
        calculationTimeoutMs = config.getInt("calculationTimeoutMs", AUTOCRAFTING, 5000, 5000, Integer.MAX_VALUE, "The autocrafting calculation timeout in milliseconds, tasks taking longer than this to calculate (NOT execute) are cancelled to avoid server strain");
        //endregion
    }*/
}
