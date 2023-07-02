package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ListAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list").executes(new ListAutocraftingCommand());
    }

    public static void addInfo(CommandContext<CommandSourceStack> context, ICraftingTask task) {
        context.getSource().sendSuccess(() ->
                Component.literal(getAmount(task.getRequested()) + "x ")
                .append(getName(task.getRequested()).copy().setStyle(Styles.YELLOW))
                .append(" ")
                .append("(" + task.getCompletionPercentage() + "%)")
                .append(" ")
                .append(Component.literal("[" + task.getId().toString() + "]").setStyle(Styles.GRAY)),
            false
        );
    }

    private static int getAmount(ICraftingRequestInfo info) {
        if (info.getItem() != null) {
            return info.getItem().getCount();
        }

        if (info.getFluid() != null) {
            return info.getFluid().getAmount();
        }

        return 0;
    }

    private static Component getName(ICraftingRequestInfo info) {
        if (info.getItem() != null) {
            return info.getItem().getHoverName();
        }

        if (info.getFluid() != null) {
            return info.getFluid().getDisplayName();
        }

        return Component.empty();
    }

    @Override
    protected int run(CommandContext<CommandSourceStack> context, INetwork network) {
        network.getCraftingManager().getTasks().forEach(task -> addInfo(context, task));
        return 0;
    }
}
