package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.command.CreateDiskCommand;
import com.refinedmods.refinedstorage.command.PatternDumpCommand;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerSetup {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal(RS.ID).then(PatternDumpCommand.register()));
        e.getDispatcher().register(Commands.literal(RS.ID).then(CreateDiskCommand.register()));
    }
}
