package refinedstorage.tile.autocrafting.task;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import refinedstorage.tile.TileController;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.autocrafting.TileCrafter;
import refinedstorage.util.InventoryUtils;

public class ProcessingCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;
    private boolean inserted;
    private boolean satisfied[];

    public ProcessingCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
        this.satisfied = new boolean[pattern.getOutputs().length];
    }

    @Override
    public CraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(TileController controller) {
        if (!inserted) {
            for (ItemStack input : pattern.getInputs()) {
                ItemStack take = controller.take(input);

                if (take != null) {
                    TileCrafter crafter = pattern.getCrafter();

                    TileEntityHopper.putStackInInventoryAllSlots((IInventory) crafter.getWorld().getTileEntity(crafter.getPos().offset(crafter.getDirection())), take, crafter.getDirection().getOpposite());
                }
            }

            inserted = true;
        }

        for (int i = 0; i < satisfied.length; ++i) {
            if (!satisfied[i]) {
                return false;
            }
        }

        return true;
    }

    public void onInserted(ItemStack in) {
        for (int i = 0; i < pattern.getOutputs().length; ++i) {
            if (!satisfied[i] && InventoryUtils.compareStack(in, pattern.getOutputs()[i])) {
                satisfied[i] = true;
            }
        }
    }

    @Override
    public void onDone(TileController controller) {
        // NO OP
    }
}
