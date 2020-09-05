package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class ListDiskCommand implements Command<CommandSource> {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("list").executes(new ListDiskCommand()).then(ListDiskForPlayerCommand.register());
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        API.instance().getStorageDiskManager(context.getSource().getWorld())
            .getAll()
            .keySet()
            .forEach(id -> context.getSource().sendFeedback(new StringTextComponent(id.toString()), false));

        return 0;
    }
}
