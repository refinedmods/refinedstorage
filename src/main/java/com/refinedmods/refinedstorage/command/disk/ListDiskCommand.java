package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ListDiskCommand implements Command<CommandSource> {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("list")
            .requires(cs -> cs.hasPermission(2))
            .executes(new ListDiskCommand())
            .then(ListDiskForPlayerCommand.register());
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        API.instance().getStorageDiskManager(context.getSource().getLevel())
            .getAll()
            .keySet()
            .forEach(id -> context.getSource().sendSuccess(new StringTextComponent(id.toString()), false));

        return 0;
    }
}
