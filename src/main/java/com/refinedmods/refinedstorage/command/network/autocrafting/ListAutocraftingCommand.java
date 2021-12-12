package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingRequestInfo;
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.NetworkCommand;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ListAutocraftingCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("list").executes(new ListAutocraftingCommand());
    }

    @Override
    protected int run(CommandContext<CommandSource> context, INetwork network) {
        network.getCraftingManager().getTasks().forEach(task -> addInfo(context, task));
        return 0;
    }

    public static void addInfo(CommandContext<CommandSource> context, ICraftingTask task) {
        context.getSource().sendSuccess(
            new StringTextComponent(getAmount(task.getRequested()) + "x ")
                .append(getName(task.getRequested()).copy().setStyle(Styles.YELLOW))
                .append(" ")
                .append("(" + task.getCompletionPercentage() + "%)")
                .append(" ")
                .append(new StringTextComponent("[" + task.getId().toString() + "]").setStyle(Styles.GRAY)),
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

    private static ITextComponent getName(ICraftingRequestInfo info) {
        if (info.getItem() != null) {
            return info.getItem().getHoverName();
        }

        if (info.getFluid() != null) {
            return info.getFluid().getDisplayName();
        }

        return StringTextComponent.EMPTY;
    }
}
