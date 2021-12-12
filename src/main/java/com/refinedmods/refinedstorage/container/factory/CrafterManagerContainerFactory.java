package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CrafterManagerContainer;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrafterManagerContainerFactory implements IContainerFactory<CrafterManagerContainer> {
    @Override
    public CrafterManagerContainer create(int windowId, PlayerInventory inv, PacketBuffer buf) {
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
