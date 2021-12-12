package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.world.server.ServerWorld;

import java.util.concurrent.CompletableFuture;

public class NetworkPositionSuggestionProvider implements SuggestionProvider<CommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerWorld world = DimensionArgument.getDimension(context, "dimension");

        API.instance().getNetworkManager(world).all().forEach(network -> builder.suggest(network.getPosition().getX() + " " + network.getPosition().getY() + " " + network.getPosition().getZ()));

        return builder.buildFuture();
    }
}
