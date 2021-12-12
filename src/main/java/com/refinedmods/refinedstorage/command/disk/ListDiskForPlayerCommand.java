package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Map;

public class ListDiskForPlayerCommand implements Command<CommandSource> {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.argument("player", EntityArgument.player()).executes(new ListDiskForPlayerCommand());
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = EntityArgument.getPlayer(context, "player");

        API.instance().getStorageDiskManager(context.getSource().getLevel())
            .getAll()
            .entrySet()
            .stream()
            .filter(entry -> player.getGameProfile().getId().equals(entry.getValue().getOwner()))
            .map(Map.Entry::getKey)
            .forEach(id -> context.getSource().sendSuccess(new StringTextComponent(id.toString()), false));

        return 0;
    }
}
