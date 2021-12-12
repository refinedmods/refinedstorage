package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;

import java.util.UUID;

public class CancelSingleAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.argument("id", UuidArgument.uuid()).suggests(new AutocraftingIdSuggestionProvider())
            .executes(new CancelSingleAutocraftingCommand());
    }

    @Override
    protected int run(CommandContext<CommandSourceStack> context, INetwork network) {
        UUID id = UuidArgument.getUuid(context, "id");

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
