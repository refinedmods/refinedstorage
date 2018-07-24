package com.raoulvdberge.refinedstorage.apiimpl.network.node;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCard;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityCardContainer;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.apiimpl.network.security.SecurityCard;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.item.validator.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemSecurityCard;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkNodeSecurityManager extends NetworkNode implements ISecurityCardContainer {
    public static final String ID = "security_manager";

    private List<ISecurityCard> actualCards = new ArrayList<>();

    private ItemHandlerBase cards = new ItemHandlerBase(9 * 2, new ListenerNetworkNode(this), new ItemValidatorBasic(RSItems.SECURITY_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!world.isRemote) {
                rebuildCards();
            }

            if (network != null) {
                network.getSecurityManager().rebuild();
            }
        }
    };
    private ItemHandlerBase editCard = new ItemHandlerBase(1, new ListenerNetworkNode(this), new ItemValidatorBasic(RSItems.SECURITY_CARD));

    public NetworkNodeSecurityManager(World world, BlockPos pos) {
        super(world, pos);
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

        StackUtils.readItems(cards, 0, tag);
        StackUtils.readItems(editCard, 1, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(cards, 0, tag);
        StackUtils.writeItems(editCard, 1, tag);

        return tag;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getSecurityManager().rebuild();
    }

    public ItemHandlerBase getCardsItems() {
        return cards;
    }

    public ItemHandlerBase getEditCard() {
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
