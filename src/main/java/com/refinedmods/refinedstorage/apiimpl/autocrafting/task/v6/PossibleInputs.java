package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PossibleInputs {
    private final List<ItemStack> possibilities;
    private int pos;

    public PossibleInputs(List<ItemStack> possibilities) {
        this.possibilities = new ArrayList<>(possibilities);
    }

    public ItemStack get() {
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

    public void sort(IStackList<ItemStack> mutatedStorage, IStackList<ItemStack> results) {
        possibilities.sort((a, b) -> {
            ItemStack ar = mutatedStorage.get(a);
            ItemStack br = mutatedStorage.get(b);

            return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
        });

        possibilities.sort((a, b) -> {
            ItemStack ar = results.get(a);
            ItemStack br = results.get(b);

            return (br == null ? 0 : br.getCount()) - (ar == null ? 0 : ar.getCount());
        });
    }
}
