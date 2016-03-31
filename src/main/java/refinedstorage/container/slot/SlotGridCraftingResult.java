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
    private IInventory craftingMatrix;
    private TileGrid grid;

    public SlotGridCraftingResult(ContainerGrid container, EntityPlayer player, InventoryCrafting craftingMatrix, IInventory craftingResult, TileGrid grid, int id, int x, int y) {
        super(player, craftingMatrix, craftingResult, id, x, y);

        this.container = container;
        this.craftingMatrix = craftingMatrix;
        this.grid = grid;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftingMatrix);

        onCrafting(stack);

        grid.onCrafted(container);
    }

    public void onShiftClick(EntityPlayer player) {
        grid.onCraftedShift(container, player);
    }
}
