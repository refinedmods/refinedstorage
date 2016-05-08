package refinedstorage.tile.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.item.ItemPattern;

public class CraftingPattern {
    public static final String NBT = "Pattern";
    public static final String NBT_CRAFTER_X = "CrafterX";
    public static final String NBT_CRAFTER_Y = "CrafterY";
    public static final String NBT_CRAFTER_Z = "CrafterZ";

    private int crafterX;
    private int crafterY;
    private int crafterZ;
    private TileCrafter crafter;
    private boolean processing;
    private ItemStack[] inputs;
    private ItemStack[] outputs;

    public CraftingPattern(int crafterX, int crafterY, int crafterZ, boolean processing, ItemStack[] inputs, ItemStack[] outputs) {
        this.crafterX = crafterX;
        this.crafterY = crafterY;
        this.crafterZ = crafterZ;
        this.processing = processing;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public TileCrafter getCrafter(World world) {
        if (crafter == null) {
            crafter = (TileCrafter) world.getTileEntity(new BlockPos(crafterX, crafterY, crafterZ));
        }

        return crafter;
    }

    public boolean isProcessing() {
        return processing;
    }

    public ItemStack[] getInputs() {
        return inputs;
    }

    public ItemStack[] getOutputs() {
        return outputs;
    }

    public boolean comparePattern(World world, CraftingPattern otherPattern, int flags) {
        if (otherPattern == this) {
            return true;
        }

        if (otherPattern.getInputs().length != inputs.length ||
            otherPattern.getOutputs().length != outputs.length ||
            otherPattern.isProcessing() != processing ||
            !otherPattern.getCrafter(world).getPos().equals(getCrafter(world).getPos())) {
            return false;
        }

        for (int i = 0; i < inputs.length; ++i) {
            if (!RefinedStorageUtils.compareStack(inputs[i], otherPattern.getInputs()[i], flags)) {
                return false;
            }
        }

        for (int i = 0; i < outputs.length; ++i) {
            if (!RefinedStorageUtils.compareStack(outputs[i], otherPattern.getOutputs()[i], flags)) {
                return false;
            }
        }

        return true;
    }

    public void writeToNBT(NBTTagCompound tag) {
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

        tag.setInteger(NBT_CRAFTER_X, crafter.getPos().getX());
        tag.setInteger(NBT_CRAFTER_Y, crafter.getPos().getY());
        tag.setInteger(NBT_CRAFTER_Z, crafter.getPos().getZ());
    }

    public static CraftingPattern readFromNBT(NBTTagCompound tag) {
        int cx = tag.getInteger(NBT_CRAFTER_X);
        int cy = tag.getInteger(NBT_CRAFTER_Y);
        int cz = tag.getInteger(NBT_CRAFTER_Z);

        boolean processing = tag.getBoolean(ItemPattern.NBT_PROCESSING);

        NBTTagList inputsTag = tag.getTagList(ItemPattern.NBT_INPUTS, Constants.NBT.TAG_COMPOUND);
        ItemStack inputs[] = new ItemStack[inputsTag.tagCount()];
        for (int i = 0; i < inputsTag.tagCount(); ++i) {
            inputs[i] = ItemStack.loadItemStackFromNBT(inputsTag.getCompoundTagAt(i));
        }

        NBTTagList outputsTag = tag.getTagList(ItemPattern.NBT_OUTPUTS, Constants.NBT.TAG_COMPOUND);
        ItemStack outputs[] = new ItemStack[outputsTag.tagCount()];
        for (int i = 0; i < outputsTag.tagCount(); ++i) {
            outputs[i] = ItemStack.loadItemStackFromNBT(outputsTag.getCompoundTagAt(i));
        }

        return new CraftingPattern(cx, cy, cz, processing, inputs, outputs);
    }
}
