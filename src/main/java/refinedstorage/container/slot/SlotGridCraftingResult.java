package refinedstorage.container.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.grid.TileGrid;

public class SlotGridCraftingResult extends SlotCrafting {
    private ContainerGrid container;
    private IInventory matrix;
    private TileGrid grid;

    public SlotGridCraftingResult(ContainerGrid container, EntityPlayer player, InventoryCrafting matrix, IInventory craftingResult, TileGrid grid, int id, int x, int y) {
        super(player, matrix, craftingResult, id, x, y);

        this.container = container;
        this.matrix = matrix;
        this.grid = grid;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, matrix);

        onCrafting(stack);

        grid.onCrafted(container);
    }
}
