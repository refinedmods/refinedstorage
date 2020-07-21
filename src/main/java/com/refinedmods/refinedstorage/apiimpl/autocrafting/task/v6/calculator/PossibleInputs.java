package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator;

import com.refinedmods.refinedstorage.api.util.IStackList;

import java.util.ArrayList;
import java.util.List;

public class PossibleInputs<T> {
    private final List<T> possibilities;
    private int pos;

    public PossibleInputs(List<T> possibilities) {
        this.possibilities = new ArrayList<>(possibilities);
    }

    public T get() {
        return possibilities.get(pos);
    }

    // Return false if we're exhausted.
    public boolean cycle() {
        if (pos + 1 >= possibilities.size()) {
            pos = 0;

            return false;
        }

        pos++;

        return true;
    }

    public void sort(IStackList<T> mutatedStorage, IStackList<T> results) {
        possibilities.sort((a, b) -> {
            int ar = mutatedStorage.getCount(a);
            int br = mutatedStorage.getCount(b);

            return br - ar;
        });

        possibilities.sort((a, b) -> {
            int ar = results.getCount(a);
            int br = results.getCount(b);

            return br - ar;
        });
    }
}
