package refinedstorage.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.solderer.ISoldererRecipe;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.container.ContainerSolderer;
import refinedstorage.inventory.BasicItemHandler;
import refinedstorage.inventory.SoldererItemHandler;
import refinedstorage.inventory.UpgradeItemHandler;
import refinedstorage.item.ItemUpgrade;

public class TileSolderer extends TileNode {
    private static final String NBT_WORKING = "Working";
    private static final String NBT_PROGRESS = "Progress";

    private BasicItemHandler items = new BasicItemHandler(4, this);
    private UpgradeItemHandler upgrades = new UpgradeItemHandler(4, this, ItemUpgrade.TYPE_SPEED);
    private SoldererItemHandler[] itemsFacade = new SoldererItemHandler[EnumFacing.values().length];

    private ISoldererRecipe recipe;

    private boolean working = false;
    private int progress = 0;
    private int duration;

    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.soldererUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        boolean wasWorking = working;

        if (items.getStackInSlot(1) == null && items.getStackInSlot(2) == null && items.getStackInSlot(3) == null) {
            stop();
        } else {
            ISoldererRecipe newRecipe = RefinedStorageAPI.SOLDERER_REGISTRY.getRecipe(items);

            if (newRecipe == null) {
                stop();
            } else if (newRecipe != recipe) {
                boolean sameItem = items.getStackInSlot(3) != null ? CompareUtils.compareStackNoQuantity(items.getStackInSlot(3), newRecipe.getResult()) : false;

                if (items.getStackInSlot(3) == null || (sameItem && ((items.getStackInSlot(3).stackSize + newRecipe.getResult().stackSize) <= items.getStackInSlot(3).getMaxStackSize()))) {
                    recipe = newRecipe;
                    progress = 0;
                    working = true;

                    markDirty();
                }
            } else if (working) {
                progress += 1 + upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);

                if (progress >= recipe.getDuration()) {
                    if (items.getStackInSlot(3) != null) {
                        items.getStackInSlot(3).stackSize += recipe.getResult().stackSize;
                    } else {
                        items.setStackInSlot(3, recipe.getResult().copy());
                    }

                    for (int i = 0; i < 3; ++i) {
                        if (recipe.getRow(i) != null) {
                            items.extractItem(i, recipe.getRow(i).stackSize, false);
                        }
                    }

                    recipe = null;
                    progress = 0;
                    // Don't set working to false yet, wait till the next update because we may have another stack waiting.

                    markDirty();
                }
            }
        }

        if (wasWorking != working) {
            updateBlock();
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        if (!state) {
            stop();
        }
    }

    public void stop() {
        progress = 0;
        working = false;
        recipe = null;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        RefinedStorageUtils.readItems(items, 0, nbt);
        RefinedStorageUtils.readItems(upgrades, 1, nbt);

        recipe = RefinedStorageAPI.SOLDERER_REGISTRY.getRecipe(items);

        if (nbt.hasKey(NBT_WORKING)) {
            working = nbt.getBoolean(NBT_WORKING);
        }

        if (nbt.hasKey(NBT_PROGRESS)) {
            progress = nbt.getInteger(NBT_PROGRESS);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RefinedStorageUtils.writeItems(items, 0, tag);
        RefinedStorageUtils.writeItems(upgrades, 1, tag);

        tag.setBoolean(NBT_WORKING, working);
        tag.setInteger(NBT_PROGRESS, progress);

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_WORKING, working);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        working = tag.getBoolean(NBT_WORKING);

        super.readUpdate(tag);
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        super.readContainerData(buf);

        progress = buf.readInt();
        duration = buf.readInt();
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        super.writeContainerData(buf);

        buf.writeInt(progress);
        buf.writeInt(recipe != null ? recipe.getDuration() : 0);
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerSolderer.class;
    }

    public boolean isWorking() {
        return working;
    }

    public int getProgressScaled(int i) {
        if (progress > duration) {
            return i;
        }

        return (int) ((float) progress / (float) duration * (float) i);
    }

    public BasicItemHandler getItems() {
        return items;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDroppedItems() {
        return new CombinedInvWrapper(items, upgrades);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) {
                return (T) items;
            }

            int i = facing.ordinal();

            if (itemsFacade[i] == null) {
                itemsFacade[i] = new SoldererItemHandler(this, facing);
            }

            return (T) itemsFacade[i];
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
