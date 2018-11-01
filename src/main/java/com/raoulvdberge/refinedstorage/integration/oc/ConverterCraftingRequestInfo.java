package com.raoulvdberge.refinedstorage.integration.oc;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import li.cil.oc.api.driver.Converter;

import java.util.Map;

public class ConverterCraftingRequestInfo implements Converter {
    @Override
    public void convert(Object value, Map<Object, Object> map) {
        if (value instanceof ICraftingRequestInfo) {
            map.put("item", ((ICraftingRequestInfo) value).getItem());
            map.put("fluid", ((ICraftingRequestInfo) value).getFluid());
        }
    }
}
