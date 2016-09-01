package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactoryNormal;
import refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactoryProcessing;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileCrafter;

public class CraftingPattern implements ICraftingPattern {
    public static final String NBT_CRAFTER_X = "CrafterX";
    public static final String NBT_CRAFTER_Y = "CrafterY";
    public static final String NBT_CRAFTER_Z = "CrafterZ";

    private ItemStack stack;
    private BlockPos crafterPos;
    private TileCrafter crafter;
    private boolean processing;
    private ItemStack[] inputs;
    private ItemStack[] outputs;
    private ItemStack[] byproducts;

    public CraftingPattern(ItemStack stack, BlockPos crafterPos, boolean processing, ItemStack[] inputs, ItemStack[] outputs, ItemStack[] byproducts) {
        this.stack = stack;
        this.crafterPos = crafterPos;
        this.processing = processing;
        this.inputs = inputs;
        this.outputs = outputs;
        this.byproducts = byproducts;
    }

    @Override
    public ICraftingPatternContainer getContainer(World world) {
        if (crafter == null) {
            crafter = (TileCrafter) world.getTileEntity(crafterPos);
        }

        return crafter;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStack[] getInputs() {
        return inputs;
    }

    @Override
    public ItemStack[] getOutputs() {
        return outputs;
    }

    @Override
    public ItemStack[] getByproducts() {
        return byproducts;
    }

    @Override
    public String getId() {
        return processing ? CraftingTaskFactoryProcessing.ID : CraftingTaskFactoryNormal.ID;
    }

    @Override
    public int getQuantityPerRequest(ItemStack requested) {
        int quantity = 0;

        for (ItemStack output : outputs) {
            if (CompareUtils.compareStackNoQuantity(requested, output)) {
                quantity += output.stackSize;

                if (!processing) {
                    break;
                }
            }
        }

        return quantity;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setBoolean(ItemPattern.NBT_PROCESSING, processing);

        NBTTagList inputsTag = new NBTTagList();

        for (ItemStack input : inputs) {
            inputsTag.appendTag(input.serializeNBT());
        }

        tag.setTag(ItemPattern.NBT_INPUTS, inputsTag);

        NBTTagList outputsTag = new NBTTagList();

        for (ItemStack output : outputs) {
            outputsTag.appendTag(output.serializeNBT());
        }

        tag.setTag(ItemPattern.NBT_OUTPUTS, outputsTag);

        if (byproducts != null) {
            NBTTagList byproductsTag = new NBTTagList();

            for (ItemStack byproduct : byproducts) {
                byproductsTag.appendTag(byproduct.serializeNBT());
            }

            tag.setTag(ItemPattern.NBT_BYPRODUCTS, byproductsTag);
        }

        tag.setInteger(NBT_CRAFTER_X, crafterPos.getX());
        tag.setInteger(NBT_CRAFTER_Y, crafterPos.getY());
        tag.setInteger(NBT_CRAFTER_Z, crafterPos.getZ());

        return tag;
    }
}
