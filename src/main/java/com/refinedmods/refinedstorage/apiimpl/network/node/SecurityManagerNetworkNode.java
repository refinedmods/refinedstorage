package com.refinedmods.refinedstorage.apiimpl.network.node;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSItems;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.security.ISecurityCard;
import com.refinedmods.refinedstorage.api.network.security.ISecurityCardContainer;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.network.security.SecurityCard;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.SecurityCardItem;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SecurityManagerNetworkNode extends NetworkNode implements ISecurityCardContainer {
    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "security_manager");

    private final List<ISecurityCard> cards = new ArrayList<>();
    private final BaseItemHandler editCard = new BaseItemHandler(1)
        .addValidator(new ItemValidator(RSItems.SECURITY_CARD.get()))
        .addListener(new NetworkNodeInventoryListener(this));
    private ISecurityCard globalCard;    private final BaseItemHandler cardsInv = new BaseItemHandler(9 * 2)
        .addValidator(new ItemValidator(RSItems.SECURITY_CARD.get()))
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener(((handler, slot, reading) -> {
            if (!world.isClientSide) {
                invalidate();
            }

            if (network != null) {
                network.getSecurityManager().invalidate();
            }
        }));

    public SecurityManagerNetworkNode(Level world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public int getEnergyUsage() {
        int usage = RS.SERVER_CONFIG.getSecurityManager().getUsage();

        for (int i = 0; i < cardsInv.getSlots(); ++i) {
            if (!cardsInv.getStackInSlot(i).isEmpty()) {
                usage += RS.SERVER_CONFIG.getSecurityManager().getUsagePerCard();
            }
        }

        return usage;
    }

    @Override
    public void update() {
        super.update();

        if (ticks == 1) {
            invalidate();
        }
    }

    private void invalidate() {
        this.cards.clear();
        this.globalCard = null;

        for (int i = 0; i < cardsInv.getSlots(); ++i) {
            ItemStack stack = cardsInv.getStackInSlot(i);

            if (!stack.isEmpty()) {
                UUID uuid = SecurityCardItem.getOwner(stack);

                if (uuid == null) {
                    this.globalCard = createCard(stack, null);

                    continue;
                }

                this.cards.add(createCard(stack, uuid));
            }
        }
    }

    private ISecurityCard createCard(ItemStack stack, @Nullable UUID uuid) {
        SecurityCard card = new SecurityCard(uuid);

        for (Permission permission : Permission.values()) {
            card.getPermissions().put(permission, SecurityCardItem.hasPermission(stack, permission));
        }

        return card;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        StackUtils.readItems(cardsInv, 0, tag);
        StackUtils.readItems(editCard, 1, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(cardsInv, 0, tag);
        StackUtils.writeItems(editCard, 1, tag);

        return tag;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        super.onConnectedStateChange(network, state, cause);

        network.getSecurityManager().invalidate();
    }

    public BaseItemHandler getCardsItems() {
        return cardsInv;
    }

    public BaseItemHandler getEditCard() {
        return editCard;
    }

    public void updatePermission(Permission permission, boolean state) {
        ItemStack card = getEditCard().getStackInSlot(0);

        if (!card.isEmpty()) {
            SecurityCardItem.setPermission(card, permission, state);
        }
    }

    @Override
    public List<ISecurityCard> getCards() {
        return cards;
    }

    @Nullable
    @Override
    public ISecurityCard getGlobalCard() {
        return globalCard;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(cardsInv, editCard);
    }


}
