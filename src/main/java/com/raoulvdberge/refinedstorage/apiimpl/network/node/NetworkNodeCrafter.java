package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NetworkNodeCrafter extends NetworkNode implements ICraftingPatternContainer {
    public static final String ID = "crafter";

    public static final String DEFAULT_NAME = "gui.refinedstorage:crafter";

    private static final String NBT_BLOCKED = "Blocked";
    private static final String NBT_BLOCKED_ON = "BlockedOn";
    private static final String NBT_DISPLAY_NAME = "DisplayName";
    private static final String NBT_UUID = "CrafterUuid";

    private ItemHandlerBase patterns = new ItemHandlerBase(9, new ItemHandlerListenerNetworkNode(this), s -> isValidPatternInSlot(world, s)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!world.isRemote) {
                rebuildPatterns();
            }

            if (network != null) {
                network.getCraftingManager().rebuild();
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    public static boolean isValidPatternInSlot(World world, ItemStack stack) {
        return stack.getItem() instanceof ICraftingPatternProvider && ((ICraftingPatternProvider) stack.getItem()).create(world, stack, null).isValid();
    }

    private List<ICraftingPattern> actualPatterns = new ArrayList<>();

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED);

    // If true, this crafter is blocked on a pattern from itself.
    private boolean blocked = false;

    // If non-null, this crafter is blocked on a child.
    private UUID blockedOn = null;

    // Used to prevent infinite recursion on getProxyPatternContainer() when
    // there's eg. two crafters facing each other.
    private boolean visited = false;

    @Nullable
    private String displayName;

    private UUID uuid = null;

    public NetworkNodeCrafter(World world, BlockPos pos) {
        super(world, pos);
    }

    private void rebuildPatterns() {
        actualPatterns.clear();

        for (int i = 0; i < patterns.getSlots(); ++i) {
            ItemStack patternStack = patterns.getStackInSlot(i);

            if (!patternStack.isEmpty()) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(world, patternStack, this);

                if (pattern.isValid()) {
                    actualPatterns.add(pattern);
                }
            }
        }
    }

    private ICraftingPatternContainer getFacingPatternContainer(INetwork network) {
        INetworkNode facing = API.instance().getNetworkNodeManager(world).getNode(pos.offset(getDirection()));
        if (facing instanceof ICraftingPatternContainer) {
            ICraftingPatternContainer facingPatternContainer = (ICraftingPatternContainer)facing;
            if (facing.getNetwork() == network) {
                return facingPatternContainer;
            }
        }
        return null;
    }

    private void updateCraftingManagerBlockingContainers(INetwork network, boolean blocking) {
        Set<UUID> blockingContainers = network.getCraftingManager().getBlockingContainers();
        if (blocking) {
            blockingContainers.add(getUuid());
        } else {
            blockingContainers.remove(getUuid());
        }
    }

    private void setBlockedInternal(INetwork network, boolean blocked) {
        if (this.blocked == blocked) {
            return;
        }

        this.blocked = blocked;
        markDirty();

        updateCraftingManagerBlockingContainers(network, blocked);

        if (blocked) {
            ICraftingPatternContainer proxy = getProxyPatternContainer();
            if (proxy != null && proxy != this) {
                proxy.setBlockedOn(getUuid());
            }
        }
    }

    @Nonnull
    private UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
            markDirty();
        }
        return uuid;
    }

    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.crafterUsage + upgrades.getEnergyUsage() + (RS.INSTANCE.config.crafterPerPatternUsage * actualPatterns.size());
    }

    @Override
    public void update() {
        super.update();

        if (ticks == 1) {
            rebuildPatterns();
        }
    }

    @Override
    protected void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        if (!state) {
            network.getCraftingManager().getTasks().stream()
                .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
                .forEach(task -> network.getCraftingManager().cancel(task));
            setBlockedInternal(network, false);
        }

        network.getCraftingManager().rebuild();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(patterns, 0, tag);
        StackUtils.readItems(upgrades, 1, tag);

        if (tag.hasKey(NBT_BLOCKED)) {
            blocked = tag.getBoolean(NBT_BLOCKED);
        }

        if (tag.hasUniqueId(NBT_BLOCKED_ON)) {
            blockedOn = tag.getUniqueId(NBT_BLOCKED_ON);
        }

        if (tag.hasKey(NBT_DISPLAY_NAME)) {
            displayName = tag.getString(NBT_DISPLAY_NAME);
        }

        if (tag.hasUniqueId(NBT_UUID)) {
            uuid = tag.getUniqueId(NBT_UUID);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(patterns, 0, tag);
        StackUtils.writeItems(upgrades, 1, tag);

        tag.setBoolean(NBT_BLOCKED, blocked);

        if (blockedOn != null) {
            tag.setUniqueId(NBT_BLOCKED_ON, blockedOn);
        }

        if (displayName != null) {
            tag.setString(NBT_DISPLAY_NAME, displayName);
        }

        if (uuid != null) {
            tag.setUniqueId(NBT_UUID, uuid);
        }

        return tag;
    }

    @Override
    public int getSpeedUpdateCount() {
        return upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);
    }

    @Override
    public IItemHandler getConnectedInventory() {
        ICraftingPatternContainer proxy = getProxyPatternContainer();
        if (proxy == null || proxy == this) {
            return WorldUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());
        }
        return proxy.getConnectedInventory();
    }

    @Override
    public TileEntity getConnectedTile() {
        ICraftingPatternContainer proxy = getProxyPatternContainer();
        if (proxy == null || proxy == this) {
            return getFacingTile();
        }
        return proxy.getFacingTile();
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return actualPatterns;
    }

    @Override
    @Nullable
    public IItemHandlerModifiable getPatternInventory() {
        return patterns;
    }

    @Override
    public String getName() {
        if (displayName != null) {
            return displayName;
        }

        TileEntity facing = getConnectedTile();

        if (facing instanceof IWorldNameable) {
            return ((IWorldNameable) facing).getName();
        }

        if (facing != null) {
            return world.getBlockState(pos.offset(getDirection())).getBlock().getUnlocalizedName() + ".name";
        }

        return DEFAULT_NAME;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    public IItemHandler getPatternItems() {
        return patterns;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(patterns, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public boolean isBlocked() {
        if (blocked || network != null && network.getCraftingManager().getBlockingContainers().contains(blockedOn)) {
            return true;
        }
        ICraftingPatternContainer proxy = getProxyPatternContainer();
        if (proxy == null || proxy == this) {
            return false;
        }
        return proxy.isBlocked();
    }

    @Override
    public void setBlocked(boolean blocked) {
        setBlockedInternal(network, blocked);
    }

    @Override
    public void setBlockedOn(UUID blockedOn) {
        this.blockedOn = blockedOn;
        markDirty();
    }

    @Override
    public ICraftingPatternContainer getProxyPatternContainer() {
        if (visited) {
            return null;
        }

        ICraftingPatternContainer facing = getFacingPatternContainer(network);
        if (facing != null) {
            visited = true;
            ICraftingPatternContainer facingContainer = facing.getProxyPatternContainer();
            visited = false;
            return facingContainer;
        }

        return this;
    }
}
