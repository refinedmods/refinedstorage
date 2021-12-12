package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public abstract class NetworkCommand implements Command<CommandSource> {
    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerWorld world = DimensionArgument.getDimension(context, "dimension");
        BlockPos pos = BlockPosArgument.getOrLoadBlockPos(context, "pos");

        INetwork network = API.instance().getNetworkManager(world).getNetwork(pos);

        if (network == null) {
            context.getSource().sendFailure(new TranslationTextComponent("commands.refinedstorage.network.get.error.not_found"));
            return 0;
        } else {
            return run(context, network);
        }
    }

    protected abstract int run(CommandContext<CommandSource> context, INetwork network) throws CommandSyntaxException;
}
