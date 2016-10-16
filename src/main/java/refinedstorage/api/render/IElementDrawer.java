package refinedstorage.api.render;

import refinedstorage.api.autocrafting.preview.ICraftingPreviewElement;

/**
 * This {@link FunctionalInterface} is used to define a draw/render function
 * This function use x and y coords and the element to be draw
 * Used in {@link ICraftingPreviewElement#draw(int, int, IElementDrawer, IElementDrawer, IElementDrawer)} and
 * {@link refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement#draw(int, int, IElementDrawer, IElementDrawer, IElementDrawer)}
 *
 * @param <T> The element to draw, usually {@link String}, {@link net.minecraft.item.ItemStack} or {@link net.minecraftforge.fluids.FluidStack}
 */
@FunctionalInterface
public interface IElementDrawer<T> {
    void draw(int x, int y, T element);
}
