package com.refinedmods.refinedstorage.inventory.player;

import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public class PlayerSlot {
    int slot;
    String curioSlot;

    public PlayerSlot(int slot, String curioSlot) {
        this.slot = slot;
        this.curioSlot = curioSlot;
    }

    public PlayerSlot(int slot) {
        this.slot = slot;
    }

    public PlayerSlot(PacketBuffer buffer) {
        slot = buffer.readInt();

        if (buffer.readBoolean()) {
            curioSlot = PacketBufferUtils.readString(buffer);
        }
    }

    public ItemStack getStackFromSlot(PlayerEntity player) {
        if (curioSlot == null || !CuriosIntegration.isLoaded()) {
            return player.inventory.getItem(slot);
        }

        LazyOptional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player);

        Optional<ICurioStacksHandler> stacksHandler = curiosHandler.resolve().flatMap((handler ->
            handler.getStacksHandler(curioSlot)
        ));

        Optional<ItemStack> stack = stacksHandler.map(handler -> handler.getStacks().getStackInSlot(slot));

        return stack.orElse(ItemStack.EMPTY);
    }

    public void writePlayerSlot(PacketBuffer buffer) {
        buffer.writeInt(slot);
        buffer.writeBoolean(curioSlot != null);
        if (curioSlot != null) {
            buffer.writeUtf(curioSlot);
        }
    }

    public int getSlotIdInPlayerInventory() {
        if (curioSlot != null) {
            return -1;
        }
        return slot;
    }

    public static PlayerSlot getSlotForHand(PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return new PlayerSlot(player.inventory.selected);
        }

        //@Volatile Offhand Slot, could use -1 as we aren't using this anywhere.
        return new PlayerSlot(40);
    }
}
