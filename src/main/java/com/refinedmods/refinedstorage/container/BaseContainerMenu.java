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
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationWatcher;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseContainerMenu extends AbstractContainerMenu {
    protected final TransferManager transferManager = new TransferManager(this);
    @Nullable
    private final BaseBlockEntity blockEntity;
    private final Player player;
    private final List<FluidFilterSlot> fluidSlots = new ArrayList<>();
    private final List<FluidStack> fluids = new ArrayList<>();
    @Nullable
    private BlockEntitySynchronizationWatcher listener;

    protected BaseContainerMenu(@Nullable MenuType<?> type, @Nullable BaseBlockEntity blockEntity, Player player, int windowId) {
        super(type, windowId);

        this.blockEntity = blockEntity;

        if (blockEntity != null && player instanceof ServerPlayer) {
            listener = new BlockEntitySynchronizationWatcher((ServerPlayer) player, blockEntity.getDataManager());
        }

        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Nullable
    public BaseBlockEntity getBlockEntity() {
        return blockEntity;
    }

    protected void addPlayerInventory(int xInventory, int yInventory) {
        int disabledSlotNumber = getDisabledSlotNumber();

        int id = 9;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                if (id == disabledSlotNumber) {
                    addSlot(new LegacyDisabledSlot(player.getInventory(), id, xInventory + x * 18, yInventory + y * 18));
                } else {
                    addSlot(new Slot(player.getInventory(), id, xInventory + x * 18, yInventory + y * 18));
                }

                id++;
            }
        }

        id = 0;

        for (int i = 0; i < 9; i++) {
            int x = xInventory + i * 18;
            int y = yInventory + 4 + (3 * 18);

            if (id == disabledSlotNumber) {
                addSlot(new LegacyDisabledSlot(player.getInventory(), id, x, y));
            } else {
                addSlot(new Slot(player.getInventory(), id, x, y));
            }

            id++;
        }
    }

    public List<FluidFilterSlot> getFluidSlots() {
        return fluidSlots;
    }

    @Override
    public void clicked(int id, int dragType, ClickType clickType, Player player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        int disabledSlotNumber = getDisabledSlotNumber();

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 &&
            clickType == ClickType.SWAP &&
            dragType == disabledSlotNumber) {
            return;
        }

        if (slot instanceof FilterSlot) {
            if (((FilterSlot) slot).isSizeAllowed()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    slot.set(ItemStack.EMPTY);
                } else if (!player.containerMenu.getCarried().isEmpty()) {
                    slot.set(player.containerMenu.getCarried().copy());
                }
            } else if (player.containerMenu.getCarried().isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(player.containerMenu.getCarried())) {
                slot.set(player.containerMenu.getCarried().copy());
            }

            return;
        } else if (slot instanceof FluidFilterSlot) {
            if (((FluidFilterSlot) slot).isSizeAllowed()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    ((FluidFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
                } else if (!player.containerMenu.getCarried().isEmpty()) {
                    ((FluidFilterSlot) slot).onContainerClicked(player.containerMenu.getCarried());
                }
            } else if (player.containerMenu.getCarried().isEmpty()) {
                ((FluidFilterSlot) slot).onContainerClicked(ItemStack.EMPTY);
            } else {
                ((FluidFilterSlot) slot).onContainerClicked(player.containerMenu.getCarried());
            }

            return;
        } else if (slot instanceof LegacyFilterSlot) {
            if (player.containerMenu.getCarried().isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else if (slot.mayPlace(player.containerMenu.getCarried())) {
                slot.set(player.containerMenu.getCarried().copy());
            }

            return;
        } else if (slot instanceof LegacyDisabledSlot) {
            return;
        }

        super.clicked(id, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return transferManager.transfer(slotIndex);
    }

    @Override
    public boolean stillValid(Player player) {
        return isBlockEntityStillPresent();
    }

    private boolean isBlockEntityStillPresent() {
        if (blockEntity != null) {
            return blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos()) == blockEntity;
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
        if (listener != null && isBlockEntityStillPresent()) {
            listener.detectAndSendChanges();
        }

        if (this.getPlayer() instanceof ServerPlayer) {
            for (int i = 0; i < this.fluidSlots.size(); ++i) {
                FluidFilterSlot slot = this.fluidSlots.get(i);

                FluidStack cached = this.fluids.get(i);
                FluidStack actual = slot.getFluidInventory().getFluid(slot.getSlotIndex());

                if (!API.instance().getComparer().isEqual(cached, actual, IComparer.COMPARE_QUANTITY | IComparer.COMPARE_NBT)) {
                    this.fluids.set(i, actual.copy());

                    RS.NETWORK_HANDLER.sendTo((ServerPlayer) getPlayer(), new FluidFilterSlotUpdateMessage(((Slot) slot).index, actual));
                }
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (listener != null) {
            listener.onClosed();
        }
    }
}
