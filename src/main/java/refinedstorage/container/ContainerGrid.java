package refinedstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.slot.*;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;
import refinedstorage.item.ItemPattern;

public class ContainerGrid extends ContainerBase {
    private IGrid grid;

    private SlotGridCraftingResult craftingResultSlot;
    private SlotDisabled patternResultSlot;
    private SlotItemHandler patternItemResultSlot;

    public ContainerGrid(IGrid grid, EntityPlayer player) {
        super(grid instanceof TileBase ? (TileBase) grid : null, player);

        this.grid = grid;

        addPlayerInventory(8, (grid.getType() == EnumGridType.CRAFTING || grid.getType() == EnumGridType.PATTERN) ? 165 : 126);

        if (grid.getType() == EnumGridType.CRAFTING) {
            int x = 26;
            int y = 96;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotGridCrafting(((TileGrid) grid).getMatrix(), i, x, y));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 26;
                }
            }

            addSlotToContainer(craftingResultSlot = new SlotGridCraftingResult(this, player, (TileGrid) grid, 0, 130 + 4, 110 + 4));
        } else if (grid.getType() == EnumGridType.PATTERN) {
            int x = 8;
            int y = 96;

            for (int i = 0; i < 9; ++i) {
                addSlotToContainer(new SlotSpecimenLegacy(((TileGrid) grid).getMatrix(), i, x, y, false));

                x += 18;

                if ((i + 1) % 3 == 0) {
                    y += 18;
                    x = 8;
                }
            }

            addSlotToContainer(patternResultSlot = new SlotDisabled(((TileGrid) grid).getResult(), 0, 112 + 4, 110 + 4));

            addSlotToContainer(new SlotItemHandler(((TileGrid) grid).getPatterns(), 0, 152, 96));
            addSlotToContainer(patternItemResultSlot = new SlotItemHandler(((TileGrid) grid).getPatterns(), 1, 152, 132));
        }

        if (grid.getType() != EnumGridType.FLUID) {
            for (int i = 0; i < 4; ++i) {
                addSlotToContainer(new SlotItemHandler(grid.getFilter(), i, 204, 6 + (18 * i)));
            }
        }
    }

    public IGrid getGrid() {
        return grid;
    }

    public void sendCraftingSlots() {
        for (int i = 0; i < inventorySlots.size(); ++i) {
            Slot slot = inventorySlots.get(i);

            if (slot instanceof SlotGridCrafting || slot == craftingResultSlot) {
                for (int j = 0; j < listeners.size(); ++j) {
                    listeners.get(j).sendSlotContents(this, i, slot.getStack());
                }
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);

        if (!player.worldObj.isRemote && grid instanceof WirelessGrid) {
            ((WirelessGrid) grid).onClose(player);
        }
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return (slot == craftingResultSlot || slot == patternResultSlot) ? false : super.canMergeSlot(stack, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (!player.worldObj.isRemote) {
            Slot slot = inventorySlots.get(slotIndex);
 
            if (slot.getHasStack()) {
                if (slot == craftingResultSlot) {
                    ((TileGrid) grid).onCraftedShift(this, player);
                } else if (slot != patternResultSlot && !(slot instanceof SlotSpecimenLegacy)) {
                    if (grid.getType() != EnumGridType.FLUID && grid.getItemHandler() != null) {
                        slot.putStack(grid.getItemHandler().onInsert((EntityPlayerMP) player, slot.getStack()));
                    } else if (grid.getType() == EnumGridType.FLUID && grid.getFluidHandler() != null) {
                        slot.putStack(grid.getFluidHandler().onInsert(slot.getStack()));
                    }

                    detectAndSendChanges();
                } 
            }
        }

        return null;
    }
    
    @Override
    public ItemStack slotClick(int id, int clickedButton, ClickType clickType, EntityPlayer player) {
    	ItemStack slotItem = super.slotClick(id, clickedButton, clickType, player);

        if(id>=0) {    	
	    	Slot slot = inventorySlots.get(id);
	    	
	    	if (slot.getHasStack()) {
		    	if (slot == patternItemResultSlot) {
		        	// fill the crafting grid with slot info
		        
		        	ItemStack pattern = slot.getStack();
		        	if(!ItemPattern.isProcessing(pattern)) {
			        	for (int i = 0; i < 9; ++i) {
			        		Slot craftslot = inventorySlots.get(36+i);
			        		craftslot.putStack( ItemPattern.getSlot(pattern, i) );
			        	}
		        	}
		        	
		        	detectAndSendChanges();
				}
	    	}
        }
    	
    	return slotItem;
    }
}
