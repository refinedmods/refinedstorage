package refinedstorage.tile.autocrafting.task;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import refinedstorage.tile.TileController;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.autocrafting.TileCrafter;
import refinedstorage.util.InventoryUtils;

public class ProcessingCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;
    private boolean inserted[];
    private boolean satisfied[];

    public ProcessingCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.inserted = new boolean[pattern.getInputs().length];
        this.satisfied = new boolean[pattern.getOutputs().length];
    }

    @Override
    public CraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(TileController controller) {
        for (int i = 0; i < inserted.length; ++i) {
            if (!inserted[i]) {
                ItemStack input = pattern.getInputs()[i];
                ItemStack took = controller.take(input);

                if (took != null) {
                    TileCrafter crafter = pattern.getCrafter();
                    TileEntity crafterFacing = crafter.getWorld().getTileEntity(crafter.getPos().offset(crafter.getDirection()));

                    if (crafterFacing instanceof IInventory) {
                        ItemStack remaining = TileEntityHopper.putStackInInventoryAllSlots((IInventory) crafterFacing, took, crafter.getDirection().getOpposite());

                        if (remaining == null) {
                            inserted[i] = true;
                        } else {
                            controller.push(input);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < satisfied.length; ++i) {
            if (!satisfied[i]) {
                return false;
            }
        }

        return true;
    }

    public boolean onInserted(ItemStack inserted) {
        for (int i = 0; i < pattern.getOutputs().length; ++i) {
            if (!satisfied[i] && InventoryUtils.compareStack(inserted, pattern.getOutputs()[i])) {
                satisfied[i] = true;

                return true;
            }
        }
        return false;
    }

    @Override
    public void onDone(TileController controller) {
        // NO OP
    }
}
