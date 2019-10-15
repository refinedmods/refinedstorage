package com.raoulvdberge.refinedstorage.integration.jei;

import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import com.raoulvdberge.refinedstorage.screen.BaseScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class GhostIngredientHandler implements IGhostIngredientHandler<BaseScreen> {
    @Override
    public <I> List<Target<I>> getTargets(BaseScreen gui, I ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();

        for (Slot slot : gui.getContainer().inventorySlots) {
            if (!slot.isEnabled()) {
                continue;
            }

            Rectangle2d bounds = new Rectangle2d(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, 17, 17);

            if (ingredient instanceof ItemStack) {
                if (slot instanceof LegacyFilterSlot || slot instanceof FilterSlot) {
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle2d getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            slot.putStack((ItemStack) ingredient);

                            // TODO RS.INSTANCE.network.sendToServer(new MessageSlotFilterSet(slot.slotNumber, (ItemStack) ingredient));
                        }
                    });
                }
            } else if (ingredient instanceof FluidStack) {
                if (slot instanceof FluidFilterSlot) {
                    targets.add(new Target<I>() {
                        @Override
                        public Rectangle2d getArea() {
                            return bounds;
                        }

                        @Override
                        public void accept(I ingredient) {
                            // TODO RS.INSTANCE.network.sendToServer(new MessageSlotFilterSetFluid(slot.slotNumber, StackUtils.copy((FluidStack) ingredient, Fluid.BUCKET_VOLUME)));
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
