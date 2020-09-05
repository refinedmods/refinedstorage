package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class GetNetworkCommand implements Command<CommandSource> {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("get")
            .then(Commands.argument("dimension", DimensionArgument.getDimension())
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(new GetNetworkCommand())));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerWorld world = DimensionArgument.getDimensionArgument(context, "dimension");
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");

        INetwork network = API.instance().getNetworkManager(world).getNetwork(pos);

        if (network == null) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("commands.refinedstorage.network.get.error.not_found"));
        } else {
            ListNetworkCommand.sendInfo(context, new ListNetworkCommand.NetworkInList(network), true);
        }

        return 0;
    }
}
