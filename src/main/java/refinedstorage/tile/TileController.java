package refinedstorage.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageGui;
import refinedstorage.RefinedStorageItems;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.storage.IStorage;
import refinedstorage.storage.IStorageProvider;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.grid.WirelessGridConsumer;
import refinedstorage.util.HandUtils;
import refinedstorage.util.InventoryUtils;

import java.util.*;

public class TileController extends TileBase implements IEnergyReceiver, INetworkTile, IRedstoneModeConfig {
    private List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
    private List<IStorage> storages = new ArrayList<IStorage>();
    private List<WirelessGridConsumer> wirelessGridConsumers = new ArrayList<WirelessGridConsumer>();
    private List<WirelessGridConsumer> wirelessGridConsumersMarkedForRemoval = new ArrayList<WirelessGridConsumer>();

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<TileMachine> machines = new ArrayList<TileMachine>();

    private List<BlockPos> visited = new ArrayList<BlockPos>();

    private EnergyStorage energy = new EnergyStorage(32000);
    private int energyUsage;

    private boolean activeClientSide;

    private boolean destroyed = false;

    @Override
    public void update() {
        super.update();

        if (!worldObj.isRemote && !destroyed) {
            int lastEnergy = energy.getEnergyStored();

            if (ticks % 20 == 0) {
                if (!isActive()) {
                    disconnectAll();
                } else {
                    visited.clear();

                    List<TileMachine> newMachines = new ArrayList<TileMachine>();

                    for (EnumFacing dir : EnumFacing.VALUES) {
                        MachineSearcher.search(this, pos.offset(dir), visited, newMachines);
                    }

                    for (TileMachine machine : machines) {
                        if (!newMachines.contains(machine)) {
                            machine.onDisconnected();
                        }
                    }

                    for (TileMachine machine : newMachines) {
                        if (!machines.contains(machine)) {
                            machine.onConnected(this);
                        }
                    }

                    machines = newMachines;

                    storages.clear();

                    for (TileMachine machine : machines) {
                        if (machine instanceof IStorageProvider) {
                            ((IStorageProvider) machine).provide(storages);
                        }
                    }

                    Collections.sort(storages, new Comparator<IStorage>() {
                        @Override
                        public int compare(IStorage s1, IStorage s2) {
                            return (s1.getPriority() > s2.getPriority()) ? -1 : 1;
                        }
                    });

                    syncItems();

                    energyUsage = 10;

                    for (TileMachine machine : machines) {
                        energyUsage += machine.getEnergyUsage();
                    }
                }
            }

            if (isActive()) {
                switch (getType()) {
                    case NORMAL:
                        energy.extractEnergy(energyUsage, false);
                        break;
                    case CREATIVE:
                        energy.setEnergyStored(energy.getMaxEnergyStored());
                        break;
                }
            }

            wirelessGridConsumers.removeAll(wirelessGridConsumersMarkedForRemoval);
            wirelessGridConsumersMarkedForRemoval.clear();

            Iterator<WirelessGridConsumer> it = wirelessGridConsumers.iterator();

            while (it.hasNext()) {
                WirelessGridConsumer consumer = it.next();

                if (!InventoryUtils.compareStack(consumer.getWirelessGrid(), consumer.getPlayer().getHeldItem(consumer.getHand()))) {
                    onCloseWirelessGrid(consumer.getPlayer());
                    consumer.getPlayer().closeScreen();
                }
            }

            if (lastEnergy != energy.getEnergyStored()) {
                worldObj.updateComparatorOutputLevel(pos, RefinedStorageBlocks.CONTROLLER);
            }
        }
    }

    public EnumControllerType getType() {
        if (worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            return (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return EnumControllerType.NORMAL;
    }

    public void onDestroyed() {
        disconnectAll();

        destroyed = true;
    }

    private void disconnectAll() {
        for (TileMachine machine : machines) {
            machine.onDisconnected();
        }

        machines.clear();
    }

    public List<TileMachine> getMachines() {
        return machines;
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    private void syncItems() {
        itemGroups.clear();

        for (IStorage storage : storages) {
            storage.addItems(itemGroups);
        }

        combineItems();
    }

    private void combineItems() {
        List<Integer> markedIndexes = new ArrayList<Integer>();

        for (int i = 0; i < itemGroups.size(); ++i) {
            if (markedIndexes.contains(i)) {
                continue;
            }

            ItemGroup group = itemGroups.get(i);

            // If the item doesn't exist anymore, remove it from storage to avoid crashes
            if (group.getType() == null) {
                markedIndexes.add(i);
            } else {
                for (int j = i + 1; j < itemGroups.size(); ++j) {
                    if (markedIndexes.contains(j)) {
                        continue;
                    }

                    ItemGroup otherGroup = itemGroups.get(j);

                    if (group.compareNoQuantity(otherGroup)) {
                        group.setQuantity(group.getQuantity() + otherGroup.getQuantity());

                        markedIndexes.add(j);
                    }
                }
            }
        }

        List<ItemGroup> markedItems = new ArrayList<ItemGroup>();

        for (int i : markedIndexes) {
            markedItems.add(itemGroups.get(i));
        }

        itemGroups.removeAll(markedItems);
    }

    public boolean push(ItemStack stack) {
        IStorage foundStorage = null;

        for (IStorage storage : storages) {
            if (storage.canPush(stack)) {
                foundStorage = storage;

                break;
            }
        }

        if (foundStorage == null) {
            return false;
        }

        foundStorage.push(stack);

        syncItems();

        markDirty();

        return true;
    }

    public ItemStack take(ItemStack stack) {
        markDirty();

        return take(stack, InventoryUtils.COMPARE_DAMAGE | InventoryUtils.COMPARE_NBT);
    }

    public ItemStack take(ItemStack stack, int flags) {
        markDirty();

        int requested = stack.stackSize;
        int receiving = 0;

        ItemStack newStack = null;

        for (IStorage storage : storages) {
            ItemStack took = storage.take(stack, flags);

            if (took != null) {
                if (newStack == null) {
                    newStack = took;
                } else {
                    newStack.stackSize += took.stackSize;
                }

                receiving += took.stackSize;
            }

            if (requested == receiving) {
                break;
            }
        }

        syncItems();

        return newStack;
    }

    public void onOpenWirelessGrid(EntityPlayer player, EnumHand hand) {
        wirelessGridConsumers.add(new WirelessGridConsumer(player, hand, player.getHeldItem(hand)));

        player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.WIRELESS_GRID, worldObj, HandUtils.getIdFromHand(hand), 0, 0);

        drainEnergyFromWirelessGrid(player, ItemWirelessGrid.USAGE_OPEN);
    }

    public void onCloseWirelessGrid(EntityPlayer player) {
        WirelessGridConsumer consumer = getWirelessGridConsumer(player);

        if (consumer != null) {
            wirelessGridConsumersMarkedForRemoval.add(consumer);
        }
    }

    public void drainEnergyFromWirelessGrid(EntityPlayer player, int energy) {
        WirelessGridConsumer consumer = getWirelessGridConsumer(player);

        if (consumer != null) {
            ItemWirelessGrid item = RefinedStorageItems.WIRELESS_GRID;
            ItemStack held = consumer.getPlayer().getHeldItem(consumer.getHand());

            item.extractEnergy(held, energy, false);

            if (item.getEnergyStored(held) <= 0) {
                onCloseWirelessGrid(player);
                consumer.getPlayer().closeScreen();
            }
        }
    }

    public WirelessGridConsumer getWirelessGridConsumer(EntityPlayer player) {
        Iterator<WirelessGridConsumer> it = wirelessGridConsumers.iterator();

        while (it.hasNext()) {
            WirelessGridConsumer consumer = it.next();

            if (consumer.getPlayer() == player) {
                return consumer;
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        energy.readFromNBT(nbt);

        if (nbt.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(nbt.getInteger(RedstoneMode.NBT));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        energy.writeToNBT(nbt);

        nbt.setInteger(RedstoneMode.NBT, redstoneMode.id);
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return energy.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energy.getEnergyStored();
    }

    public int getEnergyScaled(int i) {
        return (int) ((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored() * (float) i);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energy.getMaxEnergyStored();
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    public boolean isActive() {
        return energy.getEnergyStored() >= getEnergyUsage() && redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActiveClientSide() {
        return activeClientSide;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        markDirty();

        this.redstoneMode = mode;
    }

    @Override
    public BlockPos getMachinePos() {
        return pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        activeClientSide = buf.readBoolean();

        int lastEnergy = energy.getEnergyStored();

        energy.setEnergyStored(buf.readInt());

        if (lastEnergy != energy.getEnergyStored()) {
            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 2 | 4);
        }

        energyUsage = buf.readInt();

        redstoneMode = RedstoneMode.getById(buf.readInt());

        itemGroups.clear();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            itemGroups.add(new ItemGroup(buf));
        }

        machines.clear();

        size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            TileEntity tile = worldObj.getTileEntity(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));

            if (tile instanceof TileMachine) {
                machines.add((TileMachine) tile);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isActive());

        buf.writeInt(energy.getEnergyStored());
        buf.writeInt(energyUsage);

        buf.writeInt(redstoneMode.id);

        buf.writeInt(itemGroups.size());

        for (ItemGroup group : itemGroups) {
            group.toBytes(buf, itemGroups.indexOf(group));
        }

        buf.writeInt(machines.size());

        for (TileMachine machine : machines) {
            buf.writeInt(machine.getPos().getX());
            buf.writeInt(machine.getPos().getY());
            buf.writeInt(machine.getPos().getZ());
        }
    }
}
