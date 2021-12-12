package com.refinedmods.refinedstorage.integration.jei;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import com.refinedmods.refinedstorage.network.SetFilterSlotMessage;
import com.refinedmods.refinedstorage.network.SetFluidFilterSlotMessage;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.util.StackUtils;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler.Target;

public class GhostIngredientHandler implements IGhostIngredientHandler<BaseScreen> {
    @Override
    public <I> List<Target<I>> getTargets(BaseScreen gui, I ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();

        for (Slot slot : gui.getMenu().slots) {
            if (!slot.isActive()) {
                continue;
            }

            Rectangle2d bounds = new Rectangle2d(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 17, 17);

            if (ingredient instanceof ItemStack && (slot instanceof LegacyFilterSlot || slot instanceof FilterSlot)) {
                targets.add(new Target<I>() {
                    @Override
                    public Rectangle2d getArea() {
                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient) {
                        slot.set((ItemStack) ingredient);

                        RS.NETWORK_HANDLER.sendToServer(new SetFilterSlotMessage(slot.index, (ItemStack) ingredient));
                    }
                });
            } else if (ingredient instanceof FluidStack && slot instanceof FluidFilterSlot) {
                targets.add(new Target<I>() {
                    @Override
                    public Rectangle2d getArea() {
                        return bounds;
                    }

                    @Override
                    public void accept(I ingredient) {
                        RS.NETWORK_HANDLER.sendToServer(new SetFluidFilterSlotMessage(slot.index, StackUtils.copy((FluidStack) ingredient, FluidAttributes.BUCKET_VOLUME)));
                    }
                });
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {
        // NO OP
    }
}
