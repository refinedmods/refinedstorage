package com.raoulvdberge.refinedstorage.setup;

import com.raoulvdberge.refinedstorage.command.PatternDumpCommand;
import net.minecraft.command.Commands;
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
}
