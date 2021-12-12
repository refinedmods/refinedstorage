package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.command.network.autocrafting.CancelAllAutocraftingCommand;
import com.refinedmods.refinedstorage.command.network.autocrafting.GetAutocraftingCommand;
import com.refinedmods.refinedstorage.command.network.autocrafting.ListAutocraftingCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;

public class GetNetworkCommand extends NetworkCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("get")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("dimension", DimensionArgument.dimension())
                .then(Commands.argument("pos", BlockPosArgument.blockPos()).suggests(new NetworkPositionSuggestionProvider())
                    .executes(new GetNetworkCommand())
                    .then(Commands.literal("autocrafting")
                        .then(ListAutocraftingCommand.register())
                        .then(GetAutocraftingCommand.register())
                        .then(CancelAllAutocraftingCommand.register())
                    )));
    }

    @Override
    protected int run(CommandContext<CommandSourceStack> context, INetwork network) {
        ListNetworkCommand.sendInfo(context, new ListNetworkCommand.NetworkInList(network), true);
        return 0;
    }
}
