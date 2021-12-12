package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class GetAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("get").then(
            Commands.argument("id", UUIDArgument.uuid()).suggests(new AutocraftingIdSuggestionProvider())
                .executes(new GetAutocraftingCommand())
        );
    }

    @Override
    protected int run(CommandContext<CommandSource> context, INetwork network) {
        UUID id = UUIDArgument.getUuid(context, "id");

        ICraftingTask task = network.getCraftingManager().getTask(id);
        if (task == null) {
            context.getSource().sendFailure(new TranslationTextComponent("commands.refinedstorage.network.autocrafting.get.error.not_found"));
        } else {
            ListAutocraftingCommand.addInfo(context, task);
        }

        return 0;
    }
}
