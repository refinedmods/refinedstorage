package com.refinedmods.refinedstorage.inventory.player;

import com.refinedmods.refinedstorage.integration.curios.CuriosIntegration;
import com.refinedmods.refinedstorage.util.PacketBufferUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public PlayerSlot(FriendlyByteBuf buffer) {
        slot = buffer.readInt();

        if (buffer.readBoolean()) {
            curioSlot = PacketBufferUtils.readString(buffer);
        }
    }

    public static PlayerSlot getSlotForHand(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            return new PlayerSlot(player.getInventory().selected);
        }
        return new PlayerSlot(Inventory.SLOT_OFFHAND);
    }

    public ItemStack getStackFromSlot(Player player) {
        if (curioSlot == null || !CuriosIntegration.isLoaded()) {
            return player.getInventory().getItem(slot);
        }

        LazyOptional<ICuriosItemHandler> curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(player);

        Optional<ICurioStacksHandler> stacksHandler = curiosHandler.resolve().flatMap((handler ->
            handler.getStacksHandler(curioSlot)
        ));

        Optional<ItemStack> stack = stacksHandler.map(handler -> handler.getStacks().getStackInSlot(slot));

        return stack.orElse(ItemStack.EMPTY);
    }

    public void writePlayerSlot(FriendlyByteBuf buffer) {
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
}
