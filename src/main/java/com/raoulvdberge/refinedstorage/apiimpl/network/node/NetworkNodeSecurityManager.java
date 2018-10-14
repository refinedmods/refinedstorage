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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkNodeSecurityManager extends NetworkNode implements ISecurityCardContainer {
    public static final String ID = "security_manager";

    private List<ISecurityCard> cards = new ArrayList<>();
    private ISecurityCard globalCard;

    private ItemHandlerBase cardsInv = new ItemHandlerBase(9 * 2, new ListenerNetworkNode(this), new ItemValidatorBasic(RSItems.SECURITY_CARD)) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!world.isRemote) {
                invalidate();
            }

            if (network != null) {
                network.getSecurityManager().invalidate();
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

        for (int i = 0; i < cardsInv.getSlots(); ++i) {
            if (!cardsInv.getStackInSlot(i).isEmpty()) {
                usage += RS.INSTANCE.config.securityManagerPerSecurityCardUsage;
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
                UUID uuid = ItemSecurityCard.getOwner(stack);

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
            card.getPermissions().put(permission, ItemSecurityCard.hasPermission(stack, permission));
        }

        return card;
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(cardsInv, 0, tag);
        StackUtils.readItems(editCard, 1, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(cardsInv, 0, tag);
        StackUtils.writeItems(editCard, 1, tag);

        return tag;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getSecurityManager().invalidate();
    }

    public ItemHandlerBase getCardsItems() {
        return cardsInv;
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

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
