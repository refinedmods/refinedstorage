package refinedstorage.api;

import java.lang.reflect.Field;

public final class RefinedStorageAPI {
    private static final String API_IMPL_CLASS = "refinedstorage.apiimpl.API";
    private static final String API_IMPL_FIELD = "INSTANCE";

    private static final IAPI API;

    static {
        try {
            Class<?> apiClass = Class.forName(API_IMPL_CLASS);
            Field apiField = apiClass.getField(API_IMPL_FIELD);

            API = (IAPI) apiField.get(apiClass);
        } catch (Exception e) {
            throw new Error("The Refined Storage API implementation is unavailable, make sure Refined Storage is installed");
        }
    }

    /**
     * @return The Refined Storage API
     */
    public static IAPI instance() {
        return API;
    }
}
