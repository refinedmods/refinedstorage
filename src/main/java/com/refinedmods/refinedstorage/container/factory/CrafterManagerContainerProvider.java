package com.refinedmods.refinedstorage.container.factory;

import com.refinedmods.refinedstorage.container.CrafterManagerContainer;
import com.refinedmods.refinedstorage.screen.EmptyScreenInfoProvider;
import com.refinedmods.refinedstorage.tile.CrafterManagerTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class CrafterManagerContainerProvider implements INamedContainerProvider {
    private final CrafterManagerTile tile;

    public CrafterManagerContainerProvider(CrafterManagerTile tile) {
        this.tile = tile;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui.refinedstorage.crafter_manager");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        CrafterManagerContainer container = new CrafterManagerContainer(tile, playerEntity, windowId);

        container.setScreenInfoProvider(new EmptyScreenInfoProvider());
        container.initSlotsServer();

        return container;
    }

    public static void writeToBuffer(PacketBuffer buf, World world, BlockPos pos) {
        buf.writeBlockPos(pos);

        Map<ITextComponent, List<IItemHandlerModifiable>> containerData = ((CrafterManagerTile) world.getBlockEntity(pos)).getNode().getNetwork().getCraftingManager().getNamedContainers();

        buf.writeInt(containerData.size());

        for (Map.Entry<ITextComponent, List<IItemHandlerModifiable>> entry : containerData.entrySet()) {
            buf.writeComponent(entry.getKey());

            int slots = 0;
            for (IItemHandlerModifiable handler : entry.getValue()) {
                slots += handler.getSlots();
            }

            buf.writeInt(slots);
        }
    }
}
