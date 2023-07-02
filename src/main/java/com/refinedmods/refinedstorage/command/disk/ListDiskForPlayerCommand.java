package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class ListDiskForPlayerCommand implements Command<CommandSourceStack> {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.argument("player", EntityArgument.player()).executes(new ListDiskForPlayerCommand());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "player");

        API.instance().getStorageDiskManager(context.getSource().getLevel())
            .getAll()
            .entrySet()
            .stream()
            .filter(entry -> player.getGameProfile().getId().equals(entry.getValue().getOwner()))
            .map(Map.Entry::getKey)
            .forEach(id -> context.getSource().sendSuccess(() -> Component.literal(id.toString()), false));

        return 0;
    }
}
