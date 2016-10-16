package refinedstorage.apiimpl.autocrafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.apiimpl.API;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;
import refinedstorage.item.ItemPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CraftingPattern implements ICraftingPattern {
    private World world;
    private ICraftingPatternContainer container;
    private ItemStack stack;
    private List<ItemStack> inputs = new ArrayList<>();
    private List<ItemStack> outputs = new ArrayList<>();
    private List<ItemStack> byproducts = new ArrayList<>();

    public CraftingPattern(World world, ICraftingPatternContainer container, ItemStack stack) {
        this.world = world;
        this.container = container;
        this.stack = stack;

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            ItemStack slot = ItemPattern.getSlot(stack, i);
            inputs.add(slot);
            inv.setInventorySlotContents(i, slot);
        }

        if (!ItemPattern.isProcessing(stack)) {
            ItemStack output = CraftingManager.getInstance().findMatchingRecipe(inv, world);

            if (output != null) {
                outputs.add(output.copy());

                for (ItemStack remaining : CraftingManager.getInstance().getRemainingItems(inv, world)) {
                    if (remaining != null) {
                        byproducts.add(remaining.copy());
                    }
                }
            }
        } else {
            outputs = ItemPattern.getOutputs(stack);
        }
    }

    @Override
    public ICraftingPatternContainer getContainer() {
        return container;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isValid() {
        return inputs.stream().filter(Objects::nonNull).count() > 0 && !outputs.isEmpty();
    }

    @Override
    public boolean isProcessing() {
        return ItemPattern.isProcessing(stack);
    }

    @Override
    public boolean isOredict() {
        return true;
    }

    @Override
    public List<ItemStack> getInputs() {
        return inputs;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Override
    public List<ItemStack> getByproducts(ItemStack[] took) {
        List<ItemStack> byproducts = new ArrayList<>();

        InventoryCrafting inv = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            inv.setInventorySlotContents(i, took[i]);
        }

        for (ItemStack remaining : CraftingManager.getInstance().getRemainingItems(inv, world)) {
            if (remaining != null) {
                byproducts.add(remaining.copy());
            }
        }

        return byproducts;
    }

    @Override
    public List<ItemStack> getByproducts() {
        return byproducts;
    }

    @Override
    public String getId() {
        return CraftingTaskFactory.ID;
    }

    @Override
    public int getQuantityPerRequest(ItemStack requested) {
        int quantity = 0;

        for (ItemStack output : outputs) {
            if (API.instance().getComparer().isEqualNoQuantity(requested, output)) {
                quantity += output.stackSize;

                if (!ItemPattern.isProcessing(stack)) {
                    break;
                }
            }
        }

        return quantity;
    }

    @Override
    public String toString() {
        return "CraftingPattern{" +
            "container=" + container +
            ", inputs=" + inputs +
            ", outputs=" + outputs +
            ", byproducts=" + byproducts +
            '}';
    }
}
