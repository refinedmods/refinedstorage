package refinedstorage.api.autocrafting.craftingmonitor;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import refinedstorage.api.render.IElementDrawer;

public interface ICraftingMonitorElementDrawers {
    IElementDrawer<ItemStack> getItemDrawer();

    IElementDrawer<FluidStack> getFluidDrawer();

    IElementDrawer<String> getStringDrawer();

    IElementDrawer<?> getRedOverlayDrawer();
}
