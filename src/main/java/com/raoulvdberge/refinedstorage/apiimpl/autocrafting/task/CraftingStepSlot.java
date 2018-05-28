package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

public class CraftingStepSlot {
    private ICraftingStep step;
    private boolean fulfilled;
    private int ticks = -1;

    public CraftingStepSlot(ICraftingStep step) {
        this.step = step;
    }

    public boolean canExecute() {
        ticks++;

        switch (step.getPattern().getContainer().getSpeedUpdateCount()) {
            case 1:
                return ticks % 5 == 0;
            case 2:
                return ticks % 4 == 0;
            case 3:
                return ticks % 3 == 0;
            case 4:
                return ticks % 2 == 0;
            default:
            case 0:
                return ticks % 10 == 0;
        }
    }

    public ICraftingStep getStep() {
        return step;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled() {
        this.fulfilled = true;
    }
}
