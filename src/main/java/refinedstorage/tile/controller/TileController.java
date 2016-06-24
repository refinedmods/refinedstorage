package refinedstorage.tile.controller;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.api.network.IWirelessGridHandler;
import refinedstorage.api.storage.CompareFlags;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.apiimpl.autocrafting.BasicCraftingTask;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;
import refinedstorage.apiimpl.autocrafting.ProcessingCraftingTask;
import refinedstorage.apiimpl.network.GridHandler;
import refinedstorage.apiimpl.network.WirelessGridHandler;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.container.ContainerGrid;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridItems;
import refinedstorage.tile.ISynchronizedContainer;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.TileWirelessTransmitter;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.*;

public class TileController extends TileBase implements INetworkMaster, IEnergyReceiver, ISynchronizedContainer, IRedstoneModeConfig {
    public static final int ENERGY_CAPACITY = 32000;

    public static final String NBT_CRAFTING_TASKS = "CraftingTasks";
    public static final String NBT_ENERGY = "Energy";

    private GridHandler gridHandler = new GridHandler(this);
    private WirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private List<ItemStack> items = new ArrayList<ItemStack>();
    private List<ItemStack> combinedItems = new ArrayList<ItemStack>();
    private Set<Integer> combinedItemsIndices = new HashSet<Integer>();

    private List<IStorage> storages = new ArrayList<IStorage>();

    private List<INetworkSlave> slaves = new ArrayList<INetworkSlave>();
    private List<INetworkSlave> slavesToAdd = new ArrayList<INetworkSlave>();
    private List<INetworkSlave> slavesToRemove = new ArrayList<INetworkSlave>();

    private List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

    private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);
    private int energyUsage;

    private boolean couldRun;
    private long lastEnergyUpdate;

    private EnumControllerType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<ClientSlave> clientSlaves;

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public EnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public boolean canRun() {
        return energy.getEnergyStored() > 0 && energy.getEnergyStored() >= energyUsage && redstoneMode.isEnabled(worldObj, pos);
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            boolean forceUpdate = !slavesToAdd.isEmpty() || !slavesToRemove.isEmpty();

            for (INetworkSlave newSlave : slavesToAdd) {
                boolean found = false;

                for (int i = 0; i < slaves.size(); ++i) {
                    INetworkSlave slave = slaves.get(i);

                    if (slave.getPosition().equals(newSlave.getPosition())) {
                        slaves.set(i, newSlave);

                        found = true;

                        break;
                    }
                }

                if (!found) {
                    slaves.add(newSlave);
                }
            }

            slavesToAdd.clear();

            slaves.removeAll(slavesToRemove);
            slavesToRemove.clear();

            int lastEnergy = energy.getEnergyStored();

            if (canRun()) {
                if (ticks % 20 == 0 || forceUpdate) {
                    updateSlaves();
                }

                for (ICraftingTask taskToCancel : craftingTasksToCancel) {
                    taskToCancel.onCancelled(this);
                }

                craftingTasks.removeAll(craftingTasksToCancel);
                craftingTasksToCancel.clear();

                for (ICraftingTask task : craftingTasksToAdd) {
                    craftingTasks.push(task);
                }

                craftingTasksToAdd.clear();

                for (ICraftingTask task : craftingTasksToAddAsLast) {
                    craftingTasks.add(0, task);
                }

                craftingTasksToAddAsLast.clear();

                if (!craftingTasks.empty()) {
                    ICraftingTask top = craftingTasks.peek();

                    if (ticks % top.getPattern().getContainer(worldObj).getSpeed() == 0 && top.update(worldObj, this)) {
                        top.onDone(this);

                        craftingTasks.pop();
                    }
                }
            } else if (!slaves.isEmpty()) {
                disconnectSlaves();

                updateSlaves();
            }

            if (couldRun != canRun()) {
                couldRun = canRun();

                worldObj.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.CONTROLLER);
            }

            wirelessGridHandler.update();

            if (getType() == EnumControllerType.NORMAL && energyUsage > 0) {
                if (energy.getEnergyStored() - energyUsage >= 0) {
                    energy.extractEnergy(energyUsage, false);
                } else {
                    energy.setEnergyStored(0);
                }
            } else if (getType() == EnumControllerType.CREATIVE) {
                energy.setEnergyStored(energy.getMaxEnergyStored());
            }

            if (energy.getEnergyStored() != lastEnergy) {
                worldObj.updateComparatorOutputLevel(pos, RefinedStorageBlocks.CONTROLLER);

                if (System.currentTimeMillis() - lastEnergyUpdate > 1500) {
                    lastEnergyUpdate = System.currentTimeMillis();

                    RefinedStorageUtils.updateBlock(worldObj, pos);
                }
            }
        }

        super.update();
    }

    @Override
    public List<INetworkSlave> getSlaves() {
        return slaves;
    }

    public List<ClientSlave> getClientSlaves() {
        return clientSlaves;
    }

    @Override
    public void addSlave(INetworkSlave slave) {
        slavesToAdd.add(slave);
    }

    @Override
    public void removeSlave(INetworkSlave slave) {
        slavesToRemove.add(slave);
    }

    public void disconnectSlaves() {
        for (INetworkSlave slave : getSlaves()) {
            slave.disconnect(worldObj);
        }

        slaves.clear();
    }

    @Override
    public IGridHandler getGridHandler() {
        return gridHandler;
    }

    @Override
    public IWirelessGridHandler getWirelessGridHandler() {
        return wirelessGridHandler;
    }

    @Override
    public void onChunkUnload() {
        disconnectSlaves();
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public List<ICraftingTask> getCraftingTasks() {
        return craftingTasks;
    }

    @Override
    public void addCraftingTask(ICraftingTask task) {
        craftingTasksToAdd.add(task);

        markDirty();
    }

    @Override
    public void addCraftingTaskAsLast(ICraftingTask task) {
        craftingTasksToAddAsLast.add(task);

        markDirty();
    }

    @Override
    public ICraftingTask createCraftingTask(ICraftingPattern pattern) {
        if (pattern.isProcessing()) {
            return new ProcessingCraftingTask(pattern);
        } else {
            return new BasicCraftingTask(pattern);
        }
    }

    @Override
    public void cancelCraftingTask(ICraftingTask task) {
        craftingTasksToCancel.add(task);

        markDirty();
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Override
    public List<ICraftingPattern> getPattern(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

        for (ICraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (RefinedStorageUtils.compareStack(output, pattern, flags)) {
                    patterns.add(craftingPattern);
                }
            }
        }

        return patterns;
    }

    @Override
    public ICraftingPattern getPatternWithBestScore(ItemStack pattern) {
        return getPatternWithBestScore(pattern, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    @Override
    public ICraftingPattern getPatternWithBestScore(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = getPattern(pattern, flags);

        if (patterns.isEmpty()) {
            return null;
        } else if (patterns.size() == 1) {
            return patterns.get(0);
        }

        int highestScore = 0;
        int highestPattern = 0;

        for (int i = 0; i < patterns.size(); ++i) {
            int score = 0;

            for (ItemStack input : patterns.get(i).getInputs()) {
                ItemStack stored = getItem(input, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);

                score += stored != null ? stored.stackSize : 0;
            }

            if (score > highestScore) {
                highestScore = score;
                highestPattern = i;
            }
        }

        return patterns.get(highestPattern);
    }

    private void updateSlaves() {
        this.energyUsage = 0;
        this.storages.clear();
        this.patterns.clear();

        int range = 0;

        for (INetworkSlave slave : slaves) {
            if (!slave.canUpdate()) {
                continue;
            }

            if (slave instanceof TileWirelessTransmitter) {
                range += ((TileWirelessTransmitter) slave).getRange();
            }

            if (slave instanceof IStorageProvider) {
                ((IStorageProvider) slave).provide(storages);
            }

            if (slave instanceof TileCrafter) {
                TileCrafter crafter = (TileCrafter) slave;

                for (int i = 0; i < crafter.getPatterns().getSlots(); ++i) {
                    ItemStack pattern = crafter.getPatterns().getStackInSlot(i);

                    if (pattern != null && ItemPattern.isValid(pattern)) {
                        patterns.add(new CraftingPattern(crafter.getPos().getX(), crafter.getPos().getY(), crafter.getPos().getZ(), ItemPattern.isProcessing(pattern), ItemPattern.getInputs(pattern), ItemPattern.getOutputs(pattern), ItemPattern.getByproducts(pattern)));
                    }
                }
            }

            this.energyUsage += slave.getEnergyUsage();
        }

        wirelessGridHandler.setRange(range);

        Collections.sort(storages, new Comparator<IStorage>() {
            @Override
            public int compare(IStorage left, IStorage right) {
                int leftStored = left.getStored();
                int rightStored = right.getStored();

                if (leftStored == rightStored) {
                    return 0;
                }

                return (leftStored > rightStored) ? -1 : 1;
            }
        });

        Collections.sort(storages, new Comparator<IStorage>() {
            @Override
            public int compare(IStorage left, IStorage right) {
                if (left.getPriority() == right.getPriority()) {
                    return 0;
                }

                return (left.getPriority() > right.getPriority()) ? -1 : 1;
            }
        });

        updateItems();
        updateItemsWithClient();
    }

    private void updateItems() {
        items.clear();

        for (IStorage storage : storages) {
            storage.addItems(items);
        }

        for (ICraftingPattern pattern : patterns) {
            for (ItemStack output : pattern.getOutputs()) {
                ItemStack patternStack = output.copy();
                patternStack.stackSize = 0;
                items.add(patternStack);
            }
        }

        combinedItems.clear();
        combinedItemsIndices.clear();

        for (int i = 0; i < items.size(); ++i) {
            if (combinedItemsIndices.contains(i)) {
                continue;
            }

            ItemStack stack = items.get(i);

            for (int j = i + 1; j < items.size(); ++j) {
                if (combinedItemsIndices.contains(j)) {
                    continue;
                }

                ItemStack otherStack = items.get(j);

                if (RefinedStorageUtils.compareStackNoQuantity(stack, otherStack)) {
                    // We copy here so we don't modify the quantity of the ItemStack IStorage uses.
                    // We re-get the ItemStack because the stack may change from a previous iteration in this loop
                    ItemStack newStack = items.get(i).copy();
                    newStack.stackSize += otherStack.stackSize;
                    items.set(i, newStack);

                    combinedItems.add(otherStack);
                    combinedItemsIndices.add(j);
                }
            }
        }

        items.removeAll(combinedItems);
    }

    @Override
    public void updateItemsWithClient() {
        for (EntityPlayer player : worldObj.playerEntities) {
            if (player.openContainer.getClass() == ContainerGrid.class && pos.equals(((ContainerGrid) player.openContainer).getGrid().getNetworkPosition())) {
                updateItemsWithClient((EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void updateItemsWithClient(EntityPlayerMP player) {
        RefinedStorage.NETWORK.sendTo(new MessageGridItems(this), player);
    }

    @Override
    public ItemStack push(ItemStack stack, int size, boolean simulate) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }

        if (storages.isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        int orginalSize = size;

        ItemStack remainder = stack;

        for (IStorage storage : storages) {
            remainder = storage.push(remainder, size, simulate);

            if (remainder == null) {
                break;
            } else {
                size = remainder.stackSize;
            }
        }

        int sizePushed = remainder != null ? (orginalSize - remainder.stackSize) : orginalSize;

        if (!simulate && sizePushed > 0) {
            updateItems();
            updateItemsWithClient();

            for (int i = 0; i < sizePushed; ++i) {
                if (!craftingTasks.empty()) {
                    ICraftingTask top = craftingTasks.peek();

                    if (top instanceof ProcessingCraftingTask) {
                        ((ProcessingCraftingTask) top).onPushed(stack);
                    }
                }
            }
        }

        return remainder;
    }

    @Override
    public ItemStack take(ItemStack stack, int size) {
        return take(stack, size, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    @Override
    public ItemStack take(ItemStack stack, int size, int flags) {
        int requested = size;
        int received = 0;

        ItemStack newStack = null;

        for (IStorage storage : storages) {
            ItemStack took = storage.take(stack, requested - received, flags);

            if (took != null) {
                if (newStack == null) {
                    newStack = took;
                } else {
                    newStack.stackSize += took.stackSize;
                }

                received += took.stackSize;
            }

            if (requested == received) {
                break;
            }
        }

        if (newStack != null) {
            updateItems();
            updateItemsWithClient();
        }

        return newStack;
    }

    @Override
    public ItemStack getItem(ItemStack stack, int flags) {
        for (ItemStack otherStack : items) {
            if (RefinedStorageUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        energy.readFromNBT(tag);

        if (tag.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(tag.getInteger(RedstoneMode.NBT));
        }

        if (tag.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = tag.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                NBTTagCompound taskTag = taskList.getCompoundTagAt(i);

                CraftingPattern pattern = CraftingPattern.readFromNBT(taskTag.getCompoundTag(CraftingPattern.NBT));

                if (pattern != null) {
                    switch (taskTag.getInteger("Type")) {
                        case BasicCraftingTask.ID:
                            addCraftingTask(new BasicCraftingTask(taskTag, pattern));
                            break;
                        case ProcessingCraftingTask.ID:
                            addCraftingTask(new ProcessingCraftingTask(taskTag, pattern));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        energy.writeToNBT(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.id);

        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            NBTTagCompound taskTag = new NBTTagCompound();
            task.writeToNBT(taskTag);
            list.appendTag(taskTag);
        }

        tag.setTag(NBT_CRAFTING_TASKS, list);

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_ENERGY, energy.getEnergyStored());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        energy.setEnergyStored(tag.getInteger(NBT_ENERGY));

        super.readUpdate(tag);
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
        return (int) ((float) energy.getEnergyStored() / (float) ENERGY_CAPACITY * (float) i);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    public EnumControllerType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            this.type = (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        energy.setEnergyStored(buf.readInt());
        this.energyUsage = buf.readInt();
        this.redstoneMode = RedstoneMode.getById(buf.readInt());

        List<ClientSlave> slaves = new ArrayList<ClientSlave>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            ClientSlave slave = new ClientSlave();

            slave.energyUsage = buf.readInt();
            slave.amount = buf.readInt();
            slave.stack = ByteBufUtils.readItemStack(buf);

            slaves.add(slave);
        }

        this.clientSlaves = slaves;
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        buf.writeInt(energy.getEnergyStored());
        buf.writeInt(energyUsage);

        buf.writeInt(redstoneMode.id);

        List<ClientSlave> clientSlaves = new ArrayList<ClientSlave>();

        for (INetworkSlave slave : slaves) {
            if (slave.canUpdate()) {
                IBlockState state = worldObj.getBlockState(slave.getPosition());

                ClientSlave clientSlave = new ClientSlave();

                clientSlave.energyUsage = slave.getEnergyUsage();
                clientSlave.amount = 1;
                clientSlave.stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

                if (clientSlave.stack.getItem() != null) {
                    if (clientSlaves.contains(clientSlave)) {
                        for (ClientSlave other : clientSlaves) {
                            if (other.equals(clientSlave)) {
                                other.amount++;

                                break;
                            }
                        }
                    } else {
                        clientSlaves.add(clientSlave);
                    }
                }
            }
        }

        buf.writeInt(clientSlaves.size());

        for (ClientSlave slave : clientSlaves) {
            buf.writeInt(slave.energyUsage);
            buf.writeInt(slave.amount);
            ByteBufUtils.writeItemStack(buf, slave.stack);
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerController.class;
    }
}
