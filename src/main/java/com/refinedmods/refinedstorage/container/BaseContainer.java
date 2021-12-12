package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import com.refinedmods.refinedstorage.container.transfer.TransferManager;
import com.refinedmods.refinedstorage.network.FluidFilterSlotUpdateMessage;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataWatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainer extends Container {
    @Nullable
    private final BaseTile tile;
    @Nullable
    private TileDataWatcher listener;
    private final PlayerEntity player;

    protected final TransferManager transferManager = new TransferManager(this);

    private final List<FluidFilterSlot> fluidSlots = new ArrayList<>();
    private final List<FluidStack> fluids = new ArrayList<>();

    protected BaseContainer(@Nullable ContainerType<?> type, @Nullable BaseTile tile, PlayerEntity player, int windowId) {
        super(type, windowId);

        this.tile = tile;

        if (tile != null && player instanceof ServerPlayerEntity) {
            listener = new TileDataWatcher((ServerPlayerEntity) player, tile.getDataManager());
        }

        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    @Nullable
    public BaseTile getTile() {
        return tile;
    }

    protected void addPlayerInventory(int xInventory, int yInventory) {
        int disabledSlotNumber = getDisabledSlotNumber();

        int id = 9;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                if (id == disabledSlotNumber) {
                    addSlot(new LegacyDisabledSlot(player.inventory, id, xInventory + x * 18, yInventory + y * 18));
                } else {
                    addSlot(new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18));
                }

                id++;
            }
        }

        id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            if (id == disabledSlotNumber) {
                addSlot(new LegacyDisabledSlot(player.inventory, id, x, y));
            } else {
                addSlot(new Slot(player.inventory, id, x, y));
            }

            id++;
        }
    }

    public List<FluidFilterSlot> getFluidSlots() {
        return fluidSlots;
    }

    @Override
    public ItemStack clicked(int id, int dragType, ClickType clickType, PlayerEntity player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        int disabledSlotNumber = getDisabledSlotNumber();

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 &&
            clickType == ClickType.SWAP &&
            dragType == disabledSlotNumber) {
            return ItemStack.EMPTY;
        }

        if (slot instanceof FilterSlot) {
            if (((FilterSlot) slot).isSizeAllowed()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    slot.set(ItemStack.EMPTY);
                } else if (!player.inventory.getCarried().isEmpty()) {
                    slot.set(player.inventory.getCarried().copy());
                }
            } else if (player.inventory.getCarried().isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(player.inventory.getCarried())) {
                slot.set(player.inventory.getCarried().copy());
            }

            return player.inventory.getCarried();
        } else if (slot instanceof FluidFilterSlot) {
            if (((FluidFilterSlot) slot).isSizeAllowed()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    ((FluidFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
                } else if (!player.inventory.getCarried().isEmpty()) {
                    ((FluidFilterSlot) slot).onContainerClicked(player.inventory.getCarried());
                }
            } else if (player.inventory.getCarried().isEmpty()) {
                ((FluidFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
            } else {
                ((FluidFilterSlot) slot).onContainerClicked(player.inventory.getCarried());
            }

            return player.inventory.getCarried();
        } else if (slot instanceof LegacyFilterSlot) {
            if (player.inventory.getCarried().isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(player.inventory.getCarried())) {
                slot.set(player.inventory.getCarried().copy());
            }

            return player.inventory.getCarried();
        } else if (slot instanceof LegacyDisabledSlot) {
            return ItemStack.EMPTY;
        }

        return super.clicked(id, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotIndex) {
        return transferManager.transfer(slotIndex);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return isTileStillThere();
    }

    private boolean isTileStillThere() {
        if (tile != null) {
            return tile.getLevel().getBlockEntity(tile.getBlockPos()) == tile;
        }

        return true;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot instanceof FilterSlot || slot instanceof FluidFilterSlot || slot instanceof LegacyFilterSlot) {
            return false;
        }

        return super.canTakeItemForPickAll(stack, slot);
    }

    protected int getDisabledSlotNumber() {
        return -1;
    }

    @Override
    protected Slot addSlot(Slot slot) {
        if (slot instanceof FluidFilterSlot) {
            fluids.add(FluidStack.EMPTY);
            fluidSlots.add((FluidFilterSlot) slot);
        }

        return super.addSlot(slot);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // Prevent sending changes about a tile that doesn't exist anymore.
        // This prevents crashes when sending network node data (network node would crash because it no longer exists and we're querying it from the various tile data parameters).
        if (listener != null && isTileStillThere()) {
            listener.detectAndSendChanges();
        }

        if (this.getPlayer() instanceof ServerPlayerEntity) {
            for (int i = 0; i < this.fluidSlots.size(); ++i) {
                FluidFilterSlot slot = this.fluidSlots.get(i);

                FluidStack cached = this.fluids.get(i);
                FluidStack actual = slot.getFluidInventory().getFluid(slot.getSlotIndex());

                if (!API.instance().getComparer().isEqual(cached, actual, IComparer.COMPARE_QUANTITY | IComparer.COMPARE_NBT)) {
                    this.fluids.set(i, actual.copy());

                    RS.NETWORK_HANDLER.sendTo((ServerPlayerEntity) getPlayer(), new FluidFilterSlotUpdateMessage(((Slot)slot).index, actual));
                }
            }
        }
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);

        if (listener != null) {
            listener.onClosed();
        }
    }
}
