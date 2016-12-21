package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCard;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCardContainer;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.network.security.SecurityCard;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemSecurityCard;
import com.raoulvdberge.refinedstorage.tile.INetworkNodeHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkNodeSecurityManager extends NetworkNode implements ISecurityCardContainer {
    private static final String NBT_OWNER = "Owner";

    private List<ISecurityCard> actualCards = new ArrayList<>();

    private ItemHandlerBasic cards = new ItemHandlerBasic(9 * 2, new ItemHandlerListenerNetworkNode(this), new ItemValidatorBasic(RSItems.SECURITY_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!holder.world().isRemote) {
                rebuildCards();
            }

            if (network != null) {
                network.getSecurityManager().rebuild();
            }
        }
    };
    private ItemHandlerBasic editCard = new ItemHandlerBasic(1, new ItemHandlerListenerNetworkNode(this), new ItemValidatorBasic(RSItems.SECURITY_CARD));

    @Nullable
    private UUID owner;

    public NetworkNodeSecurityManager(INetworkNodeHolder holder) {
        super(holder);
    }

    @Override
    public int getEnergyUsage() {
        int usage = RS.INSTANCE.config.securityManagerUsage;

        for (int i = 0; i < cards.getSlots(); ++i) {
            if (!cards.getStackInSlot(i).isEmpty()) {
                usage += RS.INSTANCE.config.securityManagerPerSecurityCardUsage;
            }
        }

        return usage;
    }

    @Override
    public void update() {
        super.update();

        if (ticks == 1) {
            rebuildCards();
        }
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;

        markDirty();
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    private void rebuildCards() {
        actualCards.clear();

        for (int i = 0; i < cards.getSlots(); ++i) {
            ItemStack stack = cards.getStackInSlot(i);

            if (!stack.isEmpty()) {
                UUID uuid = ItemSecurityCard.getOwner(stack);

                if (uuid == null) {
                    continue;
                }

                SecurityCard card = new SecurityCard(uuid);

                for (Permission permission : Permission.values()) {
                    card.getPermissions().put(permission, ItemSecurityCard.hasPermission(stack, permission));
                }

                actualCards.add(card);
            }
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_OWNER)) {
            owner = UUID.fromString(tag.getString(NBT_OWNER));
        }

        RSUtils.readItems(cards, 0, tag);
        RSUtils.readItems(editCard, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        if (owner != null) {
            tag.setString(NBT_OWNER, owner.toString());
        }

        RSUtils.writeItems(cards, 0, tag);
        RSUtils.writeItems(editCard, 1, tag);

        return tag;
    }

    @Override
    public void onConnectedStateChange(INetworkMaster network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getSecurityManager().rebuild();
    }

    public ItemHandlerBasic getCardsItems() {
        return cards;
    }

    public ItemHandlerBasic getEditCard() {
        return editCard;
    }

    public void updatePermission(Permission permission, boolean state) {
        ItemStack card = getEditCard().getStackInSlot(0);

        if (!card.isEmpty()) {
            ItemSecurityCard.setPermission(card, permission, state);
        }
    }

    @Override
    public List<ISecurityCard> getCards() {
        return actualCards;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(cards, editCard);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
