package refinedstorage.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.SlotItemHandler;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.slot.SlotSpecimen;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileProcessingPatternEncoder;

public class ContainerProcessingPatternEncoder extends ContainerBase {
	private SlotItemHandler patternItemResultSlot;
	
    public ContainerProcessingPatternEncoder(TileProcessingPatternEncoder processingPatternEncoder, EntityPlayer player) {
        super(processingPatternEncoder, player);

        addSlotToContainer(new SlotItemHandler(processingPatternEncoder.getPatterns(), 0, 152, 18));
        addSlotToContainer(patternItemResultSlot = new SlotItemHandler(processingPatternEncoder.getPatterns(), 1, 152, 58));

        int ox = 8;
        int x = ox;
        int y = 20;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlotToContainer(new SlotSpecimen(processingPatternEncoder.getConfiguration(), i, x, y, SlotSpecimen.SPECIMEN_SIZE));

            x += 18;

            if ((i + 1) % 3 == 0) {
                if (i == 8) {
                    ox = 90;
                    x = ox;
                    y = 20;
                } else {
                    x = ox;
                    y += 18;
                }
            }
        }

        addPlayerInventory(8, 90);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && !(slot instanceof SlotSpecimen) && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 2) {
                if (!mergeItemStack(stack, 2 + 18, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 1, false)) {
                return null;
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
    
    private List<ItemStack> combineItems(List<ItemStack> stacks) {
    	List<ItemStack> out = new ArrayList<>(); 
        Set<Integer> combinedIndices = new HashSet<>();

        for (int i = 0; i < stacks.size(); ++i) {
            if (stacks.get(i) != null && !combinedIndices.contains(i)) {
                //String data = stacks[i].getDisplayName();

                int amount = stacks.get(i).stackSize;

                for (int j = i + 1; j < stacks.size(); ++j) {
                    if (CompareUtils.compareStack(stacks.get(i), stacks.get(j))) {
                        amount += stacks.get(j).stackSize;

                        combinedIndices.add(j);
                    }
                }

                while(amount>0) {
                    ItemStack tmp = stacks.get(i).copy();
                	tmp.stackSize = Math.min(amount ,tmp.getMaxStackSize());
                	amount -= tmp.stackSize;
                	out.add( tmp );
                }
                
            }
        }
        return out;
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
		        	if(ItemPattern.isProcessing(pattern)) {
		        	
			        	for (int i = 0; i < 9; ++i) {
			        		Slot craftslot = inventorySlots.get(2+i);
			        		craftslot.putStack( ItemPattern.getSlot(pattern, i) );
			        	}
			        	
			        	for (int i = 0; i < 9; ++i) {
			        		Slot craftslot = inventorySlots.get(11+i);
			        		craftslot.putStack( null );
			        	}

			        	// ToDo : compile the list of output to stack's
			        	List<ItemStack> outputs = combineItems(ItemPattern.getOutputs(pattern));
			        	for (int i = 0; i < Math.min(outputs.size(),9); ++i) {
			        		Slot craftslot = inventorySlots.get(11+i);
			        		
			        		craftslot.putStack( outputs.get(i) );
			        	}
			        	
			        	
		        	}
		        	
		        	detectAndSendChanges();
				}
	    	}
        }
    	
    	return slotItem;
    }
}