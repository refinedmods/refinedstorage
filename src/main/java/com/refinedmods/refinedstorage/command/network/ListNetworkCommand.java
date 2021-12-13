package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

import java.text.DecimalFormat;
import java.util.Comparator;

public class ListNetworkCommand implements Command<CommandSourceStack> {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("list")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("dimension", DimensionArgument.dimension())
                .executes(new ListNetworkCommand()));
    }

    public static void sendInfo(CommandContext<CommandSourceStack> context, NetworkInList listItem, boolean detailed) {
        context.getSource().sendSuccess(
            new TranslatableComponent(
                "commands.refinedstorage.network.list.pos",
                listItem.network.getPosition().getX(),
                listItem.network.getPosition().getY(),
                listItem.network.getPosition().getZ()
            )
                .append(" [")
                .append(new TranslatableComponent(
                    "commands.refinedstorage.network.list.tick_times",
                    new TextComponent(TIME_FORMATTER.format(listItem.tickTime)).setStyle(Styles.YELLOW),
                    new TextComponent(TIME_FORMATTER.format(listItem.tps)).setStyle(Styles.YELLOW)
                ))
                .append("]"), false);

        if (detailed) {
            context.getSource().sendSuccess(new TranslatableComponent("commands.refinedstorage.network.list.autocrafting_tasks",
                new TextComponent(listItem.network.getCraftingManager().getTasks().size() + "").setStyle(Styles.YELLOW)
            ), false);

            context.getSource().sendSuccess(new TranslatableComponent("commands.refinedstorage.network.list.nodes",
                new TextComponent(listItem.network.getNodeGraph().all().size() + "").setStyle(Styles.YELLOW)
            ), false);

            context.getSource().sendSuccess(new TranslatableComponent("commands.refinedstorage.network.list.energy_usage",
                new TextComponent(listItem.network.getEnergyUsage() + "").setStyle(Styles.YELLOW)
            ), false);
        }
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = DimensionArgument.getDimension(context, "dimension");

        API.instance().getNetworkManager(level)
            .all()
            .stream()
            .map(NetworkInList::new)
            .sorted(Comparator.comparingDouble(network -> network.tickTime))
            .forEach(listItem -> sendInfo(context, listItem, false));

        return 0;
    }

    public static class NetworkInList {
        private final double tickTime;
        private final double tps;
        private final INetwork network;

        public NetworkInList(INetwork network) {
            this.network = network;
            // @Volatile: From CommandTps
            this.tickTime = mean(network.getTickTimes()) * 1.0E-6D;
            this.tps = Math.min(1000.0 / tickTime, 20);
        }

        private long mean(long[] values) {
            long sum = 0L;
            for (long v : values) {
                sum += v;
            }
            return sum / values.length;
        }
    }
}
