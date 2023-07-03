package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;


public class CancelAllAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("cancel")
            .executes(new CancelAllAutocraftingCommand())
            .then(CancelSingleAutocraftingCommand.register());
    }

    public static void sendCancelMessage(CommandContext<CommandSourceStack> context, int count) {
        final String translationKey = count == 1
            ? "commands.refinedstorage.network.autocrafting.cancel.single"
            : "commands.refinedstorage.network.autocrafting.cancel.multiple";
        context.getSource().sendSuccess(() -> Component.translatable(translationKey, Component.literal(String.valueOf(count)).setStyle(Styles.YELLOW)), false);
    }

    @Override
    protected int run(CommandContext<CommandSourceStack> context, INetwork network) {
        int count = network.getCraftingManager().getTasks().size();

        network.getCraftingManager().getTasks().forEach(task -> network.getCraftingManager().cancel(task.getId()));

        sendCancelMessage(context, count);

        return 0;
    }
}
