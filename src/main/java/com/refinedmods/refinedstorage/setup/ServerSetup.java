package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.command.PatternDumpCommand;
import com.refinedmods.refinedstorage.util.SaveDataManager;
import net.minecraft.command.Commands;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
        if (!e.getWorld().isRemote()) {
            SaveDataManager.INSTANCE.save((ServerWorld) e.getWorld());
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load e) {
        if (!e.getWorld().isRemote()) {
            SaveDataManager.INSTANCE.read((ServerWorld) e.getWorld());
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        //Overworld is the only dimension that only gets unloaded when the save game is switched
        //Other dimensions may get unloaded at any point
        if (!e.getWorld().isRemote()) {
            SaveDataManager.INSTANCE.removeManagers((ServerWorld) e.getWorld());
        }
    }
}
