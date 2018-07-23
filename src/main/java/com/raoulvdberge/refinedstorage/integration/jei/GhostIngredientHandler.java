package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterItemOrFluid;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterLegacy;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.network.MessageFilterSlot;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

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
                if (slot instanceof SlotFilterItemOrFluid && ((SlotFilterItemOrFluid) slot).getType().getType() == IType.FLUIDS) {
                    continue;
                }

                if (slot instanceof SlotFilterLegacy || (slot instanceof SlotFilter && !(slot instanceof SlotFilterFluid))) {
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            slot.putStack((ItemStack) ingredient);

                            RS.INSTANCE.network.sendToServer(new MessageFilterSlot(slot.slotNumber, (ItemStack) ingredient));
                        }
                    });
                }
            } else if (ingredient instanceof FluidStack) {
                if (slot instanceof SlotFilterFluid || (slot instanceof SlotFilterItemOrFluid && ((SlotFilterItemOrFluid) slot).getType().getType() == IType.FLUIDS)) {
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            ItemStack filledContainer = new ItemStack(Items.BUCKET);

                            IFluidHandlerItem fluidHandler = filledContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

                            fluidHandler.fill(StackUtils.copy((FluidStack) ingredient, Fluid.BUCKET_VOLUME), true);

                            filledContainer = fluidHandler.getContainer();
                            filledContainer.setCount(((FluidStack) ingredient).amount);

                            slot.putStack(filledContainer);

                            RS.INSTANCE.network.sendToServer(new MessageFilterSlot(slot.slotNumber, filledContainer));
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
