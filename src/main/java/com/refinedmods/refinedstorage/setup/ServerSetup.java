package com.refinedmods.refinedstorage.setup;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.command.disk.CreateDiskCommand;
import com.refinedmods.refinedstorage.command.disk.ListDiskCommand;
import com.refinedmods.refinedstorage.command.network.GetNetworkCommand;
import com.refinedmods.refinedstorage.command.network.ListNetworkCommand;
import com.refinedmods.refinedstorage.command.pattern.PatternDumpCommand;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerSetup {
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(Commands.literal(RS.ID)
            .then(Commands.literal("pattern")
                .then(PatternDumpCommand.register()))
            .then(Commands.literal("disk")
                .then(CreateDiskCommand.register())
                .then(ListDiskCommand.register()))
            .then(Commands.literal("network")
                .then(GetNetworkCommand.register())
                .then(ListNetworkCommand.register())));
    }
}
