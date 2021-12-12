package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public abstract class NetworkCommand implements Command<CommandSourceStack> {
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel world = DimensionArgument.getDimension(context, "dimension");
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

        INetwork network = API.instance().getNetworkManager(world).getNetwork(pos);

        if (network == null) {
            context.getSource().sendFailure(new TranslatableComponent("commands.refinedstorage.network.get.error.not_found"));
            return 0;
        } else {
            return run(context, network);
        }
    }

    protected abstract int run(CommandContext<CommandSourceStack> context, INetwork network) throws CommandSyntaxException;
}
