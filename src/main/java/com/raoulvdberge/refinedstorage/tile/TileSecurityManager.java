package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSItems;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.Permission;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBasic;
import com.raoulvdberge.refinedstorage.inventory.ItemValidatorBasic;
import com.raoulvdberge.refinedstorage.item.ItemSecurityCard;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileSecurityManager extends TileNode {
    private ItemHandlerBasic cards = new ItemHandlerBasic(9 * 2, this, new ItemValidatorBasic(RSItems.SECURITY_CARD));
    private ItemHandlerBasic editCard = new ItemHandlerBasic(1, this, new ItemValidatorBasic(RSItems.SECURITY_CARD));

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
    public void updateNode() {
        // NO OP
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        RSUtils.readItems(cards, 0, tag);
        RSUtils.readItems(editCard, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        RSUtils.writeItems(cards, 0, tag);
        RSUtils.writeItems(editCard, 1, tag);

        return tag;
    }

    public ItemHandlerBasic getCards() {
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
}
