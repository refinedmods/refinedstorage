package com.refinedmods.refinedstorage.container.transfer;

import net.minecraft.world.item.ItemStack;

class InsertionResult {
    private final InsertionResultType type;
    private ItemStack value;

    InsertionResult(ItemStack value) {
        this.type = InsertionResultType.CONTINUE_IF_POSSIBLE;
        this.value = value;
    }

    InsertionResult(InsertionResultType type) {
        this.type = type;
    }

    public InsertionResultType getType() {
        return type;
    }

    public ItemStack getValue() {
        return value;
    }
}
