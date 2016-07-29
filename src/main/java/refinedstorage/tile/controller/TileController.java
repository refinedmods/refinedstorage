package refinedstorage.tile.controller;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.IWirelessGridHandler;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.IGroupedStorage;
import refinedstorage.api.storage.IStorage;
import refinedstorage.apiimpl.autocrafting.BasicCraftingTask;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;
import refinedstorage.apiimpl.autocrafting.ProcessingCraftingTask;
import refinedstorage.apiimpl.network.GridHandler;
import refinedstorage.apiimpl.network.WirelessGridHandler;
import refinedstorage.apiimpl.storage.GroupedStorage;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.container.ContainerGrid;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridDelta;
import refinedstorage.network.MessageGridUpdate;
import refinedstorage.tile.ISynchronizedContainer;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.externalstorage.ExternalStorage;

import java.util.*;

@Optional.InterfaceList({
    @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaConsumer", modid = "Tesla"),
    @Optional.Interface(iface = "net.darkhax.tesla.api.ITeslaHolder", modid = "Tesla")
})
public class TileController extends TileBase implements INetworkMaster, IEnergyReceiver, ITeslaHolder, ITeslaConsumer, ISynchronizedContainer, IRedstoneModeConfig {
    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_ENERGY_CAPACITY = "EnergyCapacity";

    private static final String NBT_CRAFTING_TASKS = "CraftingTasks";

    private GridHandler gridHandler = new GridHandler(this);
    private WirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private IGroupedStorage storage = new GroupedStorage(this);

    private Comparator<IStorage> sizeComparator = new Comparator<IStorage>() {
        @Override
        public int compare(IStorage left, IStorage right) {
            if (left.getStored() == right.getStored()) {
                return 0;
            }

            return (left.getStored() > right.getStored()) ? -1 : 1;
        }
    };

    private Comparator<IStorage> priorityComparator = new Comparator<IStorage>() {
        @Override
        public int compare(IStorage left, IStorage right) {
            if (left.getPriority() == right.getPriority()) {
                return 0;
            }

            return (left.getPriority() > right.getPriority()) ? -1 : 1;
        }
    };

    private List<INetworkNode> nodes = new ArrayList<INetworkNode>();
    private Set<BlockPos> nodesPos = new HashSet<BlockPos>();

    private List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

    private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(RefinedStorage.INSTANCE.controllerCapacity);
    private IC2Energy IC2Energy;
    private int energyUsage;

    private int lastEnergyDisplay;
    private int lastEnergyComparator;

    private boolean couldRun;

    private EnumControllerType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<ClientNode> clientNodes = new ArrayList<ClientNode>();

    public TileController() {
        if (RefinedStorage.hasIC2()) {
            this.IC2Energy = new IC2Energy(this);
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
    public void update() {
        if (!worldObj.isRemote) {
            if (IC2Energy != null) {
                IC2Energy.update();
            }

            if (canRun()) {
                Collections.sort(storage.getStorages(), sizeComparator);
                Collections.sort(storage.getStorages(), priorityComparator);

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

                rebuildNodes();
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

    public void disconnectAll() {
        for (INetworkNode node : nodes) {
            if (node.isConnected()) {
                node.onDisconnected(this);
            }
        }

        nodes.clear();
        nodesPos.clear();
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (IC2Energy != null) {
            IC2Energy.invalidate();
        }
    }

    @Override
    public List<INetworkNode> getNodes() {
        return nodes;
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
        if (IC2Energy != null) {
            IC2Energy.invalidate();
        }
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

        for (INetworkNode node : nodes) {
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
    public void rebuildNodes() {
        if (!canRun()) {
            if (!nodes.isEmpty()) {
                disconnectAll();
            }

            return;
        }

        List<INetworkNode> newNodes = new ArrayList<INetworkNode>();
        Set<BlockPos> newNodesPos = new HashSet<BlockPos>();

        Set<BlockPos> checked = new HashSet<BlockPos>();
        Queue<BlockPos> toCheck = new ArrayDeque<BlockPos>();

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos pos = this.pos.offset(facing);

            checked.add(pos);
            toCheck.add(pos);
        }

        BlockPos currentPos;
        while ((currentPos = toCheck.poll()) != null) {
            TileEntity tile = worldObj.getTileEntity(currentPos);

            if (tile instanceof TileController && !pos.equals(tile.getPos())) {
                worldObj.createExplosion(null, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 4.5f, true);
            }

            if (!(tile instanceof INetworkNode)) {
                continue;
            }

            INetworkNode node = (INetworkNode) tile;

            newNodes.add(node);
            newNodesPos.add(node.getPosition());

            if (node.canConduct()) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    BlockPos pos = currentPos.offset(facing);

                    if (checked.add(pos)) {
                        toCheck.add(pos);
                    }
                }
            }
        }

        List<INetworkNode> oldNodes = new ArrayList<INetworkNode>(nodes);
        Set<BlockPos> oldNodesPos = new HashSet<BlockPos>(nodesPos);

        this.nodes = newNodes;
        this.nodesPos = newNodesPos;

        for (INetworkNode newNode : nodes) {
            if (!oldNodesPos.contains(newNode.getPosition())) {
                newNode.onConnected(this);
            }
        }

        for (INetworkNode oldNode : oldNodes) {
            if (!nodesPos.contains(oldNode.getPosition())) {
                oldNode.onDisconnected(this);
            }
        }
    }

    @Override
    public void sendStorageToClient() {
        for (EntityPlayer player : worldObj.playerEntities) {
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
        for (EntityPlayer player : worldObj.playerEntities) {
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

    @Optional.Method(modid = "Tesla")
    @Override
    public long getStoredPower() {
        return energy.getEnergyStored();
    }

    @Optional.Method(modid = "Tesla")
    @Override
    public long getCapacity() {
        return energy.getMaxEnergyStored();
    }

    @Optional.Method(modid = "Tesla")
    @Override
    public long givePower(long power, boolean simulated) {
        return energy.receiveEnergy((int) power, simulated);
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
        if (!worldObj.isRemote) {
            int usage = RefinedStorage.INSTANCE.controllerUsage;

            for (INetworkNode node : nodes) {
                if (node.canUpdate()) {
                    usage += node.getEnergyUsage();
                }
            }

            return usage;
        }

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

        for (INetworkNode node : nodes) {
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
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (RefinedStorage.hasTesla() && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
            return (T) this;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (RefinedStorage.hasTesla() && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) || super.hasCapability(capability, facing);
    }
}
