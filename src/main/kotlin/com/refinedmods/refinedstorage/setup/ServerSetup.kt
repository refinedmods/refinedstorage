package com.refinedmods.refinedstorage.setup

import RegistryEvent.Register
import com.refinedmods.refinedstorage.command.PatternDumpCommand.Companion.register
import net.minecraft.command.Commands
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class ServerSetup {
    @SubscribeEvent
    fun onRegisterCommands(e: RegisterCommandsEvent) {
        e.getDispatcher().register(
                Commands.literal("refinedstorage")
                        .then(register(e.getDispatcher()))
        )
    }
}