package refinedstorage.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.*;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.IGroupedStorage;
import refinedstorage.api.storage.IStorage;
import refinedstorage.apiimpl.autocrafting.BasicCraftingTask;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;
import refinedstorage.apiimpl.autocrafting.ProcessingCraftingTask;
import refinedstorage.apiimpl.network.GridHandler;
import refinedstorage.apiimpl.network.NetworkNodeGraph;
import refinedstorage.apiimpl.network.WirelessGridHandler;
import refinedstorage.apiimpl.storage.GroupedStorage;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.integration.ic2.ControllerEnergyIC2;
import refinedstorage.integration.ic2.ControllerEnergyIC2None;
import refinedstorage.integration.ic2.IControllerEnergyIC2;
import refinedstorage.integration.ic2.IntegrationIC2;
import refinedstorage.integration.tesla.ControllerEnergyTesla;
import refinedstorage.integration.tesla.IntegrationTesla;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridDelta;
import refinedstorage.network.MessageGridUpdate;
import refinedstorage.tile.config.IRedstoneConfigurable;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;
import refinedstorage.tile.externalstorage.ExternalStorage;

import java.util.*;

public class TileController extends TileBase implements INetworkMaster, IEnergyReceiver, IRedstoneConfigurable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    public static final TileDataParameter<Integer> ENERGY_USAGE = TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.getEnergyUsage();
        }
    });

    public static final TileDataParameter<Integer> ENERGY_STORED = TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.getEnergy().getEnergyStored();
        }
    });

    public static final TileDataParameter<Integer> ENERGY_CAPACITY = TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.getEnergy().getMaxEnergyStored();
        }
    });

    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_ENERGY_CAPACITY = "EnergyCapacity";

    private static final String NBT_CRAFTING_TASKS = "CraftingTasks";

    private static final Comparator<IStorage> SIZE_COMPARATOR = (left, right) -> {
        if (left.getStored() == right.getStored()) {
            return 0;
        }

        return (left.getStored() > right.getStored()) ? -1 : 1;
    };

    private static final Comparator<IStorage> PRIORITY_COMPARATOR = (left, right) -> {
        if (left.getPriority() == right.getPriority()) {
            return 0;
        }

        return (left.getPriority() > right.getPriority()) ? -1 : 1;
    };

    private GridHandler gridHandler = new GridHandler(this);
    private WirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);
    private IGroupedStorage storage = new GroupedStorage(this);

    private List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

    private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(RefinedStorage.INSTANCE.controllerCapacity);
    private IControllerEnergyIC2 energyEU;
    private ControllerEnergyTesla energyTesla;

    private int lastEnergyDisplay;
    private int lastEnergyComparator;

    private boolean couldRun;

    private EnumControllerType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<ClientNode> clientNodes = new ArrayList<ClientNode>();

    public TileController() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);

        if (IntegrationIC2.isLoaded()) {
            this.energyEU = new ControllerEnergyIC2(this);
        } else {
            this.energyEU = new ControllerEnergyIC2None();
        }

        if (IntegrationTesla.isLoaded()) {
            this.energyTesla = new ControllerEnergyTesla(energy);
        }
    }

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
        return energy.getEnergyStored() > 0 && redstoneMode.isEnabled(worldObj, pos);
    }

    @Override
    public INetworkNodeGraph getNodeGraph() {
        return nodeGraph;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            energyEU.update();

            if (canRun()) {
                Collections.sort(storage.getStorages(), SIZE_COMPARATOR);
                Collections.sort(storage.getStorages(), PRIORITY_COMPARATOR);

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

                updateTopCraftingTask(true);
            }

            wirelessGridHandler.update();

            if (getType() == EnumControllerType.NORMAL) {
                if (!RefinedStorage.INSTANCE.controllerUsesEnergy) {
                    energy.setEnergyStored(energy.getMaxEnergyStored());
                } else if (energy.getEnergyStored() - getEnergyUsage() >= 0) {
                    energy.extractEnergy(getEnergyUsage(), false);
                } else {
                    energy.setEnergyStored(0);
                }
            } else if (getType() == EnumControllerType.CREATIVE) {
                energy.setEnergyStored(energy.getMaxEnergyStored());
            }

            if (couldRun != canRun()) {
                couldRun = canRun();

                NetworkUtils.rebuildGraph(this);
            }

            if (getEnergyScaledForDisplay() != lastEnergyDisplay) {
                lastEnergyDisplay = getEnergyScaledForDisplay();

                updateBlock();
            }

            if (getEnergyScaledForComparator() != lastEnergyComparator) {
                lastEnergyComparator = getEnergyScaledForComparator();

                worldObj.updateComparatorOutputLevel(pos, RefinedStorageBlocks.CONTROLLER);
            }
        }

        super.update();
    }

    private void updateTopCraftingTask(boolean withSpeed) {
        if (!craftingTasks.empty()) {
            markDirty();

            ICraftingTask top = craftingTasks.peek();

            ICraftingPatternContainer container = top.getPattern().getContainer(worldObj);

            if (container != null && (!withSpeed || (ticks % container.getSpeed()) == 0) && top.update(worldObj, this)) {
                top.onDone(this);

                craftingTasks.pop();
            }
        }
    }

    @Override
    public void invalidate() {
        energyEU.invalidate();

        super.invalidate();
    }

    public List<ClientNode> getClientNodes() {
        return clientNodes;
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
        super.onChunkUnload();

        energyEU.onChunkUnload();
    }

    public IGroupedStorage getStorage() {
        return storage;
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
    public List<ICraftingPattern> getPatterns(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

        for (ICraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (CompareUtils.compareStack(output, pattern, flags)) {
                    patterns.add(craftingPattern);
                }
            }
        }

        return patterns;
    }

    @Override
    public ICraftingPattern getPattern(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = getPatterns(pattern, flags);

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
                ItemStack stored = storage.get(input, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);

                score += stored != null ? stored.stackSize : 0;
            }

            if (score > highestScore) {
                highestScore = score;
                highestPattern = i;
            }
        }

        return patterns.get(highestPattern);
    }

    @Override
    public void rebuildPatterns() {
        patterns.clear();

        for (INetworkNode node : nodeGraph.all()) {
            if (node instanceof TileCrafter && node.canUpdate()) {
                TileCrafter crafter = (TileCrafter) node;

                for (int i = 0; i < crafter.getPatterns().getSlots(); ++i) {
                    ItemStack pattern = crafter.getPatterns().getStackInSlot(i);

                    if (pattern != null && ItemPattern.isValid(pattern)) {
                        patterns.add(new CraftingPattern(
                            crafter.getPos(),
                            ItemPattern.isProcessing(pattern),
                            ItemPattern.getInputs(pattern),
                            ItemPattern.getOutputs(pattern),
                            ItemPattern.getByproducts(pattern)
                        ));
                    }
                }
            }
        }

        storage.rebuild();
    }

    @Override
    public void sendStorageToClient() {
        for (EntityPlayer player : worldObj.getMinecraftServer().getPlayerList().getPlayerList()) {
            if (isWatchingGrid(player)) {
                sendStorageToClient((EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void sendStorageToClient(EntityPlayerMP player) {
        RefinedStorage.INSTANCE.network.sendTo(new MessageGridUpdate(this), player);
    }

    @Override
    public void sendStorageDeltaToClient(ItemStack stack, int delta) {
        for (EntityPlayer player : worldObj.getMinecraftServer().getPlayerList().getPlayerList()) {
            if (isWatchingGrid(player)) {
                RefinedStorage.INSTANCE.network.sendTo(new MessageGridDelta(this, stack, delta), (EntityPlayerMP) player);
            }
        }
    }

    private boolean isWatchingGrid(EntityPlayer player) {
        return player.openContainer.getClass() == ContainerGrid.class && pos.equals(((ContainerGrid) player.openContainer).getGrid().getNetworkPosition());
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        if (stack == null || stack.getItem() == null || storage.getStorages().isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        int orginalSize = size;

        ItemStack remainder = stack;

        for (IStorage storage : this.storage.getStorages()) {
            remainder = storage.insertItem(remainder, size, simulate);

            if (storage instanceof ExternalStorage && !simulate) {
                ((ExternalStorage) storage).updateCacheForcefully();
            }

            if (remainder == null) {
                break;
            } else {
                size = remainder.stackSize;
            }
        }

        int inserted = remainder != null ? (orginalSize - remainder.stackSize) : orginalSize;

        if (!simulate && inserted > 0) {
            for (int i = 0; i < inserted; ++i) {
                if (!craftingTasks.empty() && craftingTasks.peek() instanceof ProcessingCraftingTask) {
                    if (((ProcessingCraftingTask) craftingTasks.peek()).onInserted(stack)) {
                        updateTopCraftingTask(false);
                    }
                }
            }

            storage.add(ItemHandlerHelper.copyStackWithSize(stack, inserted), false);
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags) {
        int requested = size;
        int received = 0;

        ItemStack newStack = null;

        for (IStorage storage : this.storage.getStorages()) {
            ItemStack took = storage.extractItem(stack, requested - received, flags);

            if (took != null) {
                if (storage instanceof ExternalStorage) {
                    ((ExternalStorage) storage).updateCacheForcefully();
                }

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
            storage.remove(newStack);
        }

        return newStack;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

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
        super.writeToNBT(tag);

        energy.writeToNBT(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.ordinal());

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

        tag.setInteger(NBT_ENERGY_CAPACITY, energy.getMaxEnergyStored());
        tag.setInteger(NBT_ENERGY, energy.getEnergyStored());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        energy.setCapacity(tag.getInteger(NBT_ENERGY_CAPACITY));
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
        return (int) ((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored() * (float) i);
    }

    public int getEnergyScaledForDisplay() {
        return getEnergyScaled(7);
    }

    public int getEnergyScaledForComparator() {
        return getEnergyScaled(15);
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

        markDirty();
    }

    @Override
    public int getEnergyUsage() {
        int usage = RefinedStorage.INSTANCE.controllerBaseUsage;

        for (INetworkNode node : nodeGraph.all()) {
            if (node.canUpdate()) {
                usage += node.getEnergyUsage();
            }
        }

        return usage;
    }

    public EnumControllerType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            this.type = (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    // @TODO: Sync client nodes
    /*@Override
    public void readContainerData(ByteBuf buf) {
        energy.setEnergyStored(buf.readInt());
        this.energyUsage = buf.readInt();
        this.redstoneMode = RedstoneMode.getById(buf.readInt());

        List<ClientNode> nodes = new ArrayList<ClientNode>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            ClientNode node = new ClientNode();

            node.energyUsage = buf.readInt();
            node.amount = buf.readInt();
            node.stack = ByteBufUtils.readItemStack(buf);

            nodes.add(node);
        }

        this.clientNodes = nodes;
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        buf.writeInt(energy.getEnergyStored());
        buf.writeInt(getEnergyUsage());

        buf.writeInt(redstoneMode.id);

        List<ClientNode> clientNodes = new ArrayList<ClientNode>();

        for (INetworkNode node : nodeGraph.all()) {
            if (node.canUpdate()) {
                IBlockState state = worldObj.getBlockState(node.getPosition());

                ClientNode clientNode = new ClientNode();

                clientNode.energyUsage = node.getEnergyUsage();
                clientNode.amount = 1;
                clientNode.stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

                if (clientNode.stack.getItem() != null) {
                    if (clientNodes.contains(clientNode)) {
                        for (ClientNode other : clientNodes) {
                            if (other.equals(clientNode)) {
                                other.amount++;

                                break;
                            }
                        }
                    } else {
                        clientNodes.add(clientNode);
                    }
                }
            }
        }

        buf.writeInt(clientNodes.size());

        for (ClientNode node : clientNodes) {
            buf.writeInt(node.energyUsage);
            buf.writeInt(node.amount);
            ByteBufUtils.writeItemStack(buf, node.stack);
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerController.class;
    }*/

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
            return (T) energyTesla;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) || super.hasCapability(capability, facing);
    }

    public class ClientNode {
        public ItemStack stack;
        public int amount;
        public int energyUsage;

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }

            if (!(other instanceof ClientNode)) {
                return false;
            }

            return energyUsage == ((ClientNode) other).energyUsage && CompareUtils.compareStack(stack, ((ClientNode) other).stack);
        }

        @Override
        public int hashCode() {
            int result = stack.hashCode();
            result = 31 * result + energyUsage;
            return result;
        }
    }
}
