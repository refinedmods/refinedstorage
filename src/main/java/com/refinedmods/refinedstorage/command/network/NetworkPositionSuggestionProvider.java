package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CompletableFuture;

public class NetworkPositionSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerLevel level = DimensionArgument.getDimension(context, "dimension");

        API.instance().getNetworkManager(level).all().forEach(network -> builder.suggest(network.getPosition().getX() + " " + network.getPosition().getY() + " " + network.getPosition().getZ()));

        return builder.buildFuture();
    }
}
