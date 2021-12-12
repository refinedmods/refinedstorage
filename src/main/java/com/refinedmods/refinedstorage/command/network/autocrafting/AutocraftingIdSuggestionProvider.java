package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.concurrent.CompletableFuture;

public class AutocraftingIdSuggestionProvider implements SuggestionProvider<CommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerWorld world = DimensionArgument.getDimension(context, "dimension");
        BlockPos pos = BlockPosArgument.getOrLoadBlockPos(context, "pos");
        INetwork network = API.instance().getNetworkManager(world).getNetwork(pos);

        if (network != null) {
            network.getCraftingManager().getTasks().forEach(task -> builder.suggest(task.getId().toString()));
        }

        return builder.buildFuture();
    }
}
