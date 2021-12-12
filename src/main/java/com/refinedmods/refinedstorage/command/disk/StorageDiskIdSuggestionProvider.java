package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandSource;

import java.util.concurrent.CompletableFuture;

public class StorageDiskIdSuggestionProvider implements SuggestionProvider<CommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        API.instance().getStorageDiskManager(context.getSource().getLevel())
            .getAll()
            .keySet()
            .forEach(id -> builder.suggest(id.toString()));

        return builder.buildFuture();
    }
}
