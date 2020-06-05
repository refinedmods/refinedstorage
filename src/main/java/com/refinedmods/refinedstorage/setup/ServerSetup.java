package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskManager;
import com.refinedmods.refinedstorage.command.PatternDumpCommand;
import net.minecraft.command.Commands;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class ServerSetup {
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent e) {
        e.getCommandDispatcher().register(
            Commands.literal("refinedstorage")
                .then(PatternDumpCommand.register(e.getCommandDispatcher()))
        );
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save e) {
        API.instance().getStorageDiskManager((ServerWorld) e.getWorld()).save();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        //load gets called for each dimension. But we only need to call this once.
        if (!e.getWorld().isRemote() && e.getWorld().getDimension().getType().equals(DimensionType.OVERWORLD)) {
            API.instance().getStorageDiskManager((ServerWorld) e.getWorld()).read();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        //Overworld is the only dimension that only gets unloaded when the save game is switched
        //Other dimensions may get unloaded at any point
        if (!e.getWorld().isRemote() && e.getWorld().getDimension().getType().equals(DimensionType.OVERWORLD)) {
            StorageDiskManager.resetManager();
        }
    }
}
