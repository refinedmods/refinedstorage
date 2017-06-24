/*package com.raoulvdberge.refinedstorage.integration.oc;

import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import li.cil.oc.api.driver.Converter;

import java.util.Map;

public class ConverterCraftingTask implements Converter {
    @Override
    public void convert(Object value, Map<Object, Object> output) {
        if (value instanceof ICraftingTask) {
            ICraftingTask task = (ICraftingTask) value;

            output.put("stack", task.getRequested());
            output.put("missing", task.getMissing().getStacks());
            output.put("pattern", task.getPattern());
            output.put("quantity", task.getQuantity());
        }
    }
}*/
