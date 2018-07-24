package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.container.slot.legacy.SlotLegacyFilter;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.network.MessageSlotFilterSet;
import com.raoulvdberge.refinedstorage.network.MessageSlotFilterSetFluid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GhostIngredientHandler implements IGhostIngredientHandler<GuiBase> {
    @Override
    public <I> List<Target<I>> getTargets(GuiBase gui, I ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();

        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (!slot.isEnabled()) {
                continue;
            }

            Rectangle bounds = new Rectangle(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, 17, 17);

            if (ingredient instanceof ItemStack) {
                if (slot instanceof SlotLegacyFilter || slot instanceof SlotFilter) {
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            slot.putStack((ItemStack) ingredient);

                            RS.INSTANCE.network.sendToServer(new MessageSlotFilterSet(slot.slotNumber, (ItemStack) ingredient));
                        }
                    });
                }
            } else if (ingredient instanceof FluidStack) {
                if (slot instanceof SlotFilterFluid) {
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            RS.INSTANCE.network.sendToServer(new MessageSlotFilterSetFluid(slot.slotNumber, StackUtils.copy((FluidStack) ingredient, Fluid.BUCKET_VOLUME)));
                        }
                    });
                }
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {
        // NO OP
    }
}
