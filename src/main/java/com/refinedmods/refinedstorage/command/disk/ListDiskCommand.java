package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class ListDiskCommand implements Command<CommandSourceStack> {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list")
            .requires(cs -> cs.hasPermission(2))
            .executes(new ListDiskCommand())
            .then(ListDiskForPlayerCommand.register());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        API.instance().getStorageDiskManager(context.getSource().getLevel())
            .getAll()
            .keySet()
            .forEach(id -> context.getSource().sendSuccess(new TextComponent(id.toString()), false));

        return 0;
    }
}
