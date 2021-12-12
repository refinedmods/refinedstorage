package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CancelAllAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("cancel")
            .executes(new CancelAllAutocraftingCommand())
            .then(CancelSingleAutocraftingCommand.register());
    }

    @Override
    protected int run(CommandContext<CommandSource> context, INetwork network) {
        int count = network.getCraftingManager().getTasks().size();

        network.getCraftingManager().getTasks().forEach(task -> network.getCraftingManager().cancel(task.getId()));

        sendCancelMessage(context, count);

        return 0;
    }

    public static void sendCancelMessage(CommandContext<CommandSource> context, int count) {
        String translationKey = "commands.refinedstorage.network.autocrafting.cancel.multiple";
        if (count == 1) {
            translationKey = "commands.refinedstorage.network.autocrafting.cancel.single";
        }

        context.getSource().sendSuccess(new TranslationTextComponent(translationKey, new StringTextComponent("" + count).setStyle(Styles.YELLOW)), false);
    }
}
