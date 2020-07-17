package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6;

import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class PossibleFluidInputs {
    private final List<FluidStack> possibilities;
    private int pos;

    public PossibleFluidInputs(List<FluidStack> possibilities) {
        this.possibilities = possibilities;
    }

    public FluidStack get() {
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

    public void sort(IStackList<FluidStack> mutatedStorage, IStackList<FluidStack> results) {
        possibilities.sort((a, b) -> {
            FluidStack ar = mutatedStorage.get(a);
            FluidStack br = mutatedStorage.get(b);

            return (br == null ? 0 : br.getAmount()) - (ar == null ? 0 : ar.getAmount());
        });

        possibilities.sort((a, b) -> {
            FluidStack ar = results.get(a);
            FluidStack br = results.get(b);

            return (br == null ? 0 : br.getAmount()) - (ar == null ? 0 : ar.getAmount());
        });
    }
}
