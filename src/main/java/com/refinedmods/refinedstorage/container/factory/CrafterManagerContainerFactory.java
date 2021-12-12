package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CrafterManagerContainer;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import  net.minecraftforge.network.IContainerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrafterManagerContainerFactory implements IContainerFactory<CrafterManagerContainer> {
    @Override
    public CrafterManagerContainer create(int windowId, Inventory inv, FriendlyByteBuf buf) {
        Map<String, Integer> data = new LinkedHashMap<>();

        BlockPos pos = buf.readBlockPos();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            data.put(buf.readComponent().getString(), buf.readInt());
        }

        CrafterManagerContainer container = new CrafterManagerContainer((CrafterManagerTile) inv.player.level.getBlockEntity(pos), inv.player, windowId);

        container.setScreenInfoProvider(new EmptyScreenInfoProvider());
        container.initSlots(data);

        return container;
    }
}
