package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.UUIDArgument;

import java.util.UUID;

public class CancelSingleAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.argument("id", UUIDArgument.uuid()).suggests(new AutocraftingIdSuggestionProvider())
            .executes(new CancelSingleAutocraftingCommand());
    }

    @Override
    protected int run(CommandContext<CommandSource> context, INetwork network) {
        UUID id = UUIDArgument.getUuid(context, "id");

        int count = 0;

        ICraftingTask task = network.getCraftingManager().getTask(id);
        if (task != null) {
            count = 1;
            network.getCraftingManager().cancel(task.getId());
        }

        CancelAllAutocraftingCommand.sendCancelMessage(context, count);

        return 0;
    }
}
