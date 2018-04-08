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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkNodeCrafter extends NetworkNode implements ICraftingPatternContainer {
    public static final String ID = "crafter";

    public static final String DEFAULT_NAME = "gui.refinedstorage:crafter";

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

    // Used to prevent infinite recursion on getProxyPatternContainer() when
    // there's eg. two crafters facing each other.
    private boolean visited = false;

    @Nullable
    private String displayName;

    @Nullable
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
            setBlocked(network, false);
        }

        network.getCraftingManager().rebuild();
    }

    @Override
    protected void onDirectionChanged() {
        if (network != null) {
            network.getCraftingManager().rebuild();
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(patterns, 0, tag);
        StackUtils.readItems(upgrades, 1, tag);

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
        if (proxy == null) {
            return null;
        }
        return WorldUtils.getItemHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
    }

    @Override
    public TileEntity getConnectedTile() {
        ICraftingPatternContainer proxy = getProxyPatternContainer();
        if (proxy == null) {
            return null;
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
    @Nullable
    public ICraftingPatternContainer getProxyPatternContainer() {
        if (visited) {
            return null;
        }

        INetworkNode facing = API.instance().getNetworkNodeManager(world).getNode(pos.offset(getDirection()));
        if (!(facing instanceof ICraftingPatternContainer) || facing.getNetwork() != network) {
            return this;
        }

        visited = true;
        ICraftingPatternContainer facingContainer = ((ICraftingPatternContainer)facing).getProxyPatternContainer();
        visited = false;
        return facingContainer;
    }

    @Override
    public boolean isBlocked() {
        ICraftingPatternContainer proxy = getProxyPatternContainer();
        return proxy != null && network != null && network.getCraftingManager().isContainerBlocked(proxy.getUuid());
    }

    @Override
    public void setBlocked(INetwork network, boolean blocked) {
        if (blocked) {
            ICraftingPatternContainer proxy = getProxyPatternContainer();
            if (proxy != null) {
                network.getCraftingManager().addContainerBlock(getUuid(), proxy.getUuid());
            }
        } else {
            network.getCraftingManager().removeContainerBlock(getUuid());
        }
    }

    @Override
    public UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
            markDirty();
        }
        return uuid;
    }
}
