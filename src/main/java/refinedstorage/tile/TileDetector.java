package refinedstorage.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import refinedstorage.RS;
import refinedstorage.RSBlocks;
import refinedstorage.api.RSAPI;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.util.IComparer;
import refinedstorage.gui.GuiDetector;
import refinedstorage.inventory.ItemHandlerBasic;
import refinedstorage.inventory.ItemHandlerFluid;
import refinedstorage.tile.config.IComparable;
import refinedstorage.tile.config.IType;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataParameter;

public class TileDetector extends TileNode implements IComparable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    public static final TileDataParameter<Integer> MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileDetector>() {
        @Override
        public Integer getValue(TileDetector tile) {
            return tile.mode;
        }
    }, new ITileDataConsumer<Integer, TileDetector>() {
        @Override
        public void setValue(TileDetector tile, Integer value) {
            if (value == MODE_UNDER || value == MODE_EQUAL || value == MODE_ABOVE || value == MODE_AUTOCRAFTING) {
                tile.mode = value;

                tile.markDirty();
            }
        }
    });

    public static final TileDataParameter<Integer> AMOUNT = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileDetector>() {
        @Override
        public Integer getValue(TileDetector tile) {
            return tile.amount;
        }
    }, new ITileDataConsumer<Integer, TileDetector>() {
        @Override
        public void setValue(TileDetector tile, Integer value) {
            tile.amount = value;

            tile.markDirty();
        }
    }, parameter -> {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;

            if (gui instanceof GuiDetector) {
                ((GuiDetector) gui).AMOUNT.setText(String.valueOf(parameter.getValue()));
            }
        }
    });

    private static final int SPEED = 5;

    public static final int MODE_UNDER = 0;
    public static final int MODE_EQUAL = 1;
    public static final int MODE_ABOVE = 2;
    public static final int MODE_AUTOCRAFTING = 3;

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_AMOUNT = "Amount";
    private static final String NBT_POWERED = "Powered";
    private static final String NBT_TYPE = "Type";

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(1, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(1, this);

    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int type = IType.ITEMS;
    private int mode = MODE_EQUAL;
    private int amount = 0;

    private boolean powered = false;
    private boolean wasPowered;

    public TileDetector() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(AMOUNT);
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.detectorUsage;
    }

    @Override
    public void updateNode() {
        if (ticks % SPEED == 0) {
            if (type == IType.ITEMS) {
                ItemStack slot = itemFilters.getStackInSlot(0);

                if (slot != null) {
                    if (mode == MODE_AUTOCRAFTING) {
                        boolean found = false;

                        for (ICraftingTask task : network.getCraftingTasks()) {
                            for (ItemStack output : task.getPattern().getOutputs()) {
                                if (RSAPI.instance().getComparer().isEqualNoQuantity(slot, output)) {
                                    found = true;

                                    break;
                                }
                            }

                            if (found) {
                                break;
                            }
                        }

                        powered = found;
                    } else {
                        ItemStack stack = network.getItemStorage().getList().get(slot, compare);

                        powered = isPowered(stack == null ? null : stack.stackSize);
                    }
                } else {
                    powered = mode == MODE_AUTOCRAFTING && !network.getCraftingTasks().isEmpty();
                }
            } else if (type == IType.FLUIDS) {
                FluidStack slot = fluidFilters.getFluidStackInSlot(0);

                if (slot != null) {
                    FluidStack stack = network.getFluidStorage().get(slot, compare);

                    powered = isPowered(stack == null ? null : stack.amount);
                } else {
                    powered = false;
                }
            }
        }
    }

    @Override
    public void update() {
        if (powered != wasPowered) {
            wasPowered = powered;

            worldObj.notifyNeighborsOfStateChange(pos, RSBlocks.DETECTOR);

            updateBlock();
        }

        super.update();
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        if (!state) {
            powered = false;
        }
    }

    public boolean isPowered() {
        return powered;
    }

    public boolean isPowered(Integer size) {
        if (size != null) {
            switch (mode) {
                case MODE_UNDER:
                    return size < amount;
                case MODE_EQUAL:
                    return size == amount;
                case MODE_ABOVE:
                    return size > amount;
            }
        } else {
            if (mode == MODE_UNDER && amount != 0) {
                return true;
            } else if (mode == MODE_EQUAL && amount == 0) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_AMOUNT)) {
            amount = tag.getInteger(NBT_AMOUNT);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        readItems(itemFilters, 0, tag);
        readItems(fluidFilters, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_AMOUNT, amount);
        tag.setInteger(NBT_TYPE, type);

        writeItems(itemFilters, 0, tag);
        writeItems(fluidFilters, 1, tag);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        powered = tag.getBoolean(NBT_POWERED);

        super.readUpdate(tag);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_POWERED, powered);

        return tag;
    }

    public IItemHandler getInventory() {
        return itemFilters;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }

    @Override
    public int getType() {
        return worldObj.isRemote ? TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }
}
