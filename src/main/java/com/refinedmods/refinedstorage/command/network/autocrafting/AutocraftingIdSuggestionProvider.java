package com.refinedmods.refinedstorage.command.network.autocrafting;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CompletableFuture;

public class AutocraftingIdSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        ServerLevel world = DimensionArgument.getDimension(context, "dimension");
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        INetwork network = API.instance().getNetworkManager(world).getNetwork(pos);

        if (network != null) {
            network.getCraftingManager().getTasks().forEach(task -> builder.suggest(task.getId().toString()));
        }

        return builder.buildFuture();
    }
}
