package refinedstorage.apiimpl.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.item.ItemPattern;
import refinedstorage.tile.TileCrafter;

public class CraftingPattern implements ICraftingPattern {
    public static final String NBT = "Pattern";
    private static final String NBT_CRAFTER_X = "CrafterX";
    private static final String NBT_CRAFTER_Y = "CrafterY";
    private static final String NBT_CRAFTER_Z = "CrafterZ";

    private BlockPos crafterPos;
    private TileCrafter crafter;
    private boolean processing;
    private ItemStack[] inputs;
    private ItemStack[] outputs;
    private ItemStack[] byproducts;

    public CraftingPattern(BlockPos crafterPos, boolean processing, ItemStack[] inputs, ItemStack[] outputs, ItemStack[] byproducts) {
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
    public BlockPos getContainerPosition() {
        return crafterPos;
    }

    @Override
    public boolean isProcessing() {
        return processing;
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

    public static CraftingPattern readFromNBT(NBTTagCompound tag) {
        BlockPos crafterPos = new BlockPos(tag.getInteger(NBT_CRAFTER_X), tag.getInteger(NBT_CRAFTER_Y), tag.getInteger(NBT_CRAFTER_Z));

        boolean processing = tag.getBoolean(ItemPattern.NBT_PROCESSING);

        NBTTagList inputsTag = tag.getTagList(ItemPattern.NBT_INPUTS, Constants.NBT.TAG_COMPOUND);
        ItemStack inputs[] = new ItemStack[inputsTag.tagCount()];

        for (int i = 0; i < inputsTag.tagCount(); ++i) {
            inputs[i] = ItemStack.loadItemStackFromNBT(inputsTag.getCompoundTagAt(i));

            if (inputs[i] == null) {
                return null;
            }
        }

        NBTTagList outputsTag = tag.getTagList(ItemPattern.NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);
        ItemStack outputs[] = new ItemStack[outputsTag.tagCount()];

        for (int i = 0; i < outputsTag.tagCount(); ++i) {
            outputs[i] = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));

            if (outputs[i] == null) {
                return null;
            }
        }

        ItemStack byproducts[] = new ItemStack[0];

        if (tag.hasKey(ItemPattern.NBT_BYPRODUCTS)) {
            NBTTagList byproductsTag = tag.getTagList(ItemPattern.NBT_BYPRODUCTS, Constants.NBT.TAG_COMPOUND);
            byproducts = new ItemStack[byproductsTag.tagCount()];

            for (int i = 0; i < byproductsTag.tagCount(); ++i) {
                byproducts[i] = ItemStack.loadItemStackFromNBT(byproductsTag.getCompoundTagAt(i));

                if (byproducts[i] == null) {
                    return null;
                }
            }
        }

        return new CraftingPattern(crafterPos, processing, inputs, outputs, byproducts);
    }
}
