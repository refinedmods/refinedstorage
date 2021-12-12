package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class StorageDiskIdSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        API.instance().getStorageDiskManager(context.getSource().getLevel())
            .getAll()
            .keySet()
            .forEach(id -> builder.suggest(id.toString()));

        return builder.buildFuture();
    }
}
