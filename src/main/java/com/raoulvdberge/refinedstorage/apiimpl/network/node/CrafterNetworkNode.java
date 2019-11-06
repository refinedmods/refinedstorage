package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.UpgradeItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.raoulvdberge.refinedstorage.item.UpgradeItem;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CrafterNetworkNode extends NetworkNode implements ICraftingPatternContainer {
    public enum CrafterMode {
        IGNORE,
        SIGNAL_UNLOCKS_AUTOCRAFTING,
        SIGNAL_LOCKS_AUTOCRAFTING,
        PULSE_INSERTS_NEXT_SET;

        public static CrafterMode getById(int id) {
            if (id >= 0 && id < values().length) {
                return values()[id];
            }

            return IGNORE;
        }
    }

    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "crafter");

    private static final ITextComponent DEFAULT_NAME = new TranslationTextComponent("gui.refinedstorage.crafter");

    private static final String NBT_DISPLAY_NAME = "DisplayName";
    private static final String NBT_UUID = "CrafterUuid";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_LOCKED = "Locked";
    private static final String NBT_WAS_POWERED = "WasPowered";

    private BaseItemHandler patternsInventory = new BaseItemHandler(9) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }
        .addValidator(new PatternItemValidator(world))
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) -> {
            if (!reading) {
                if (!world.isRemote) {
                    invalidate();
                }

                if (network != null) {
                    network.getCraftingManager().invalidate();
                }
            }
        });

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private UpgradeItemHandler upgrades = (UpgradeItemHandler) new UpgradeItemHandler(4, UpgradeItem.Type.SPEED)
        .addListener(new NetworkNodeInventoryListener(this));

    // Used to prevent infinite recursion on getRootContainer() when there's e.g. two crafters facing each other.
    private boolean visited = false;

    private CrafterMode mode = CrafterMode.IGNORE;
    private boolean locked = false;
    private boolean wasPowered;

    @Nullable
    private ITextComponent displayName;

    @Nullable
    private UUID uuid = null;

    public CrafterNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    private void invalidate() {
        patterns.clear();

        for (int i = 0; i < patternsInventory.getSlots(); ++i) {
            ItemStack patternStack = patternsInventory.getStackInSlot(i);

            if (!patternStack.isEmpty()) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(world, patternStack, this);

                if (pattern.isValid()) {
                    patterns.add(pattern);
                }
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getCrafter().getUsage() + upgrades.getEnergyUsage() + (RS.SERVER_CONFIG.getCrafter().getPatternUsage() * patterns.size());
    }

    @Override
    public void update() {
        super.update();

        if (ticks == 1) {
            invalidate();
        }

        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET) {
            if (world.isBlockPowered(pos)) {
                this.wasPowered = true;

                markDirty();
            } else if (wasPowered) {
                this.wasPowered = false;
                this.locked = false;

                markDirty();
            }
        }
    }

    @Override
    protected void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getCraftingManager().invalidate();
    }

    @Override
    public void onDisconnected(INetwork network) {
        super.onDisconnected(network);

        network.getCraftingManager().getTasks().stream()
            .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
            .forEach(task -> network.getCraftingManager().cancel(task.getId()));
    }

    @Override
    public void onDirectionChanged(Direction direction) {
        super.onDirectionChanged(direction);

        if (network != null) {
            network.getCraftingManager().invalidate();
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(patternsInventory, 0, tag);

        invalidate();

        StackUtils.readItems(upgrades, 1, tag);

        if (tag.contains(NBT_DISPLAY_NAME)) {
            displayName = ITextComponent.Serializer.fromJson(tag.getString(NBT_DISPLAY_NAME));
        }

        if (tag.hasUniqueId(NBT_UUID)) {
            uuid = tag.getUniqueId(NBT_UUID);
        }

        if (tag.contains(NBT_MODE)) {
            mode = CrafterMode.getById(tag.getInt(NBT_MODE));
        }

        if (tag.contains(NBT_LOCKED)) {
            locked = tag.getBoolean(NBT_LOCKED);
        }

        if (tag.contains(NBT_WAS_POWERED)) {
            wasPowered = tag.getBoolean(NBT_WAS_POWERED);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(patternsInventory, 0, tag);
        StackUtils.writeItems(upgrades, 1, tag);

        if (displayName != null) {
            tag.putString(NBT_DISPLAY_NAME, ITextComponent.Serializer.toJson(displayName));
        }

        if (uuid != null) {
            tag.putUniqueId(NBT_UUID, uuid);
        }

        tag.putInt(NBT_MODE, mode.ordinal());
        tag.putBoolean(NBT_LOCKED, locked);
        tag.putBoolean(NBT_WAS_POWERED, wasPowered);

        return tag;
    }

    @Override
    public int getUpdateInterval() {
        switch (upgrades.getUpgradeCount(UpgradeItem.Type.SPEED)) {
            case 0:
                return 10;
            case 1:
                return 8;
            case 2:
                return 6;
            case 3:
                return 4;
            case 4:
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public int getMaximumSuccessfulCraftingUpdates() {
        switch (upgrades.getUpgradeCount(UpgradeItem.Type.SPEED)) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            default:
                return 1;
        }
    }

    @Override
    @Nullable
    public IItemHandler getConnectedInventory() {
        ICraftingPatternContainer proxy = getRootContainer();
        if (proxy == null) {
            return null;
        }

        return WorldUtils.getItemHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public IFluidHandler getConnectedFluidInventory() {
        ICraftingPatternContainer proxy = getRootContainer();
        if (proxy == null) {
            return null;
        }

        return WorldUtils.getFluidHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
    }

    @Override
    @Nullable
    public TileEntity getConnectedTile() {
        ICraftingPatternContainer proxy = getRootContainer();
        if (proxy == null) {
            return null;
        }

        return proxy.getFacingTile();
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Override
    @Nullable
    public IItemHandlerModifiable getPatternInventory() {
        return patternsInventory;
    }

    @Override
    public ITextComponent getName() {
        if (displayName != null) {
            return displayName;
        }

        TileEntity facing = getConnectedTile();

        if (facing instanceof INameable && ((INameable) facing).getName() != null) {
            return ((INameable) facing).getName();
        }

        if (facing != null) {
            return new TranslationTextComponent(world.getBlockState(facing.getPos()).getBlock().getTranslationKey());
        }

        return DEFAULT_NAME;
    }

    public void setDisplayName(ITextComponent displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public ITextComponent getDisplayName() {
        return displayName;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    public CrafterMode getMode() {
        return mode;
    }

    public void setMode(CrafterMode mode) {
        this.mode = mode;
        this.wasPowered = false;
        this.locked = false;

        this.markDirty();
    }

    public IItemHandler getPatternItems() {
        return patternsInventory;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(patternsInventory, upgrades);
    }

    @Override
    @Nullable
    public ICraftingPatternContainer getRootContainer() {
        if (visited) {
            return null;
        }

        INetworkNode facing = API.instance().getNetworkNodeManager((ServerWorld) world).getNode(pos.offset(getDirection()));
        if (!(facing instanceof ICraftingPatternContainer) || facing.getNetwork() != network) {
            return this;
        }

        visited = true;
        ICraftingPatternContainer facingContainer = ((ICraftingPatternContainer) facing).getRootContainer();
        visited = false;

        return facingContainer;
    }

    public Optional<ICraftingPatternContainer> getRootContainerNotSelf() {
        ICraftingPatternContainer root = getRootContainer();

        if (root != null && root != this) {
            return Optional.of(root);
        }

        return Optional.empty();
    }

    @Override
    public UUID getUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();

            markDirty();
        }

        return uuid;
    }

    @Override
    public boolean isLocked() {
        Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
        if (root.isPresent()) {
            return root.get().isLocked();
        }

        switch (mode) {
            case IGNORE:
                return false;
            case SIGNAL_LOCKS_AUTOCRAFTING:
                return world.isBlockPowered(pos);
            case SIGNAL_UNLOCKS_AUTOCRAFTING:
                return !world.isBlockPowered(pos);
            case PULSE_INSERTS_NEXT_SET:
                return locked;
            default:
                return false;
        }
    }

    @Override
    public void onUsedForProcessing() {
        Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
        if (root.isPresent()) {
            root.get().onUsedForProcessing();

            return;
        }

        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET) {
            this.locked = true;

            markDirty();
        }
    }
}
