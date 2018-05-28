package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task;

class CraftingStepWrapper {
    private ICraftingStep step;
    private boolean completed;
    private int ticks = -1;

    CraftingStepWrapper(ICraftingStep step) {
        this.step = step;
    }

    boolean canExecute() {
        if (!step.canExecute()) {
            return false;
        }

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

    ICraftingStep getStep() {
        return step;
    }

    boolean isCompleted() {
        return completed;
    }

    void setCompleted() {
        this.completed = true;
    }
}
