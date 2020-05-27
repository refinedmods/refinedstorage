package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import java.util.function.Predicate;

public class TagGridFilter implements Predicate<IGridStack> {
    private String tagName;

    public TagGridFilter(String tagName) {
        this.tagName = tagName.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        return stack.getTags().stream().anyMatch(name -> name.toLowerCase().contains(this.tagName));
    }
}