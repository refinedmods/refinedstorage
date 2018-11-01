package com.raoulvdberge.refinedstorage.integration.oc;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import li.cil.oc.api.driver.Converter;

import java.util.Map;

public class ConverterCraftingPattern implements Converter {
    @Override
    public void convert(Object value, final Map<Object, Object> output) {
        if (value instanceof ICraftingPattern) {
            ICraftingPattern pattern = (ICraftingPattern) value;

            output.put("outputs", pattern.getOutputs());
            output.put("inputs", pattern.getInputs());

            if (!pattern.isProcessing()) {
                output.put("byproducts", pattern.getByproducts());
            }

            output.put("processing", pattern.isProcessing());
            output.put("oredict", pattern.isOredict());
        }
    }
}
