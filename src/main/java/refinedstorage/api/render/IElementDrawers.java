package refinedstorage.api.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.render.IElementDrawer;

public interface IElementDrawers {
    default IElementDrawer<ItemStack> getItemDrawer() {
        return getNullDrawer();
    }

    default IElementDrawer<FluidStack> getFluidDrawer() {
        return getNullDrawer();
    }

    default IElementDrawer<String> getStringDrawer() {
        return getNullDrawer();
    }

    default IElementDrawer<?> getRedOverlayDrawer() {
        return getNullDrawer();
    }

    /**
     * DO NOT OVERRIDE
     *
     * @param <T> any type of drawer
     * @return A drawer that does nothing
     */
    default <T> IElementDrawer<T> getNullDrawer() {
        return (x, y, element) -> {};
    }
}
