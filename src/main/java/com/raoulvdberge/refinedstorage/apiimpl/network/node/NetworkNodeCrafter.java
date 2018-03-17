package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
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

public class NetworkNodeCrafter extends NetworkNode implements ICraftingPatternContainer {
    public static final String ID = "crafter";

    public static final String DEFAULT_NAME = "gui.refinedstorage:crafter";

    private static final String NBT_BLOCKED = "Blocked";
    private static final String NBT_DISPLAY_NAME = "DisplayName";

    private ItemHandlerBase patterns = new ItemHandlerBase(9, new ItemHandlerListenerNetworkNode(this), s -> s.getItem() instanceof ICraftingPatternProvider && ((ICraftingPatternProvider) s.getItem()).create(world, s, this).isValid()) {
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

    private List<ICraftingPattern> actualPatterns = new ArrayList<>();

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ItemHandlerListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED);

    private boolean blocked = false;

    @Nullable
    private String displayName;

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

        if (tag.hasKey(NBT_DISPLAY_NAME)) {
            displayName = tag.getString(NBT_DISPLAY_NAME);
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

        if (displayName != null) {
            tag.setString(NBT_DISPLAY_NAME, displayName);
        }

        return tag;
    }

    @Override
    public int getSpeedUpdateCount() {
        return upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);
    }

    @Override
    public IItemHandler getFacingInventory() {
        return WorldUtils.getItemHandler(getFacingTile(), getDirection().getOpposite());
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return actualPatterns;
    }

    @Override
    public IItemHandlerModifiable getPatternInventory() {
        return patterns;
    }

    @Override
    public String getName() {
        if (displayName != null) {
            return displayName;
        }

        TileEntity facing = getFacingTile();

        if (facing instanceof IWorldNameable) {
            return ((IWorldNameable) facing).getName();
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
        return blocked;
    }

    @Override
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;

        markDirty();
    }
}
