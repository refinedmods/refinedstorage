package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.UUID;

public class GetAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("get").then(
            Commands.argument("id", UuidArgument.uuid()).suggests(new AutocraftingIdSuggestionProvider())
                .executes(new GetAutocraftingCommand())
        );
    }

    @Override
    protected int run(CommandContext<CommandSourceStack> context, INetwork network) {
        UUID id = UuidArgument.getUuid(context, "id");

        ICraftingTask task = network.getCraftingManager().getTask(id);
        if (task == null) {
            context.getSource().sendFailure(new TranslatableComponent("commands.refinedstorage.network.autocrafting.get.error.not_found"));
        } else {
            ListAutocraftingCommand.addInfo(context, task);
        }

        return 0;
    }
}
