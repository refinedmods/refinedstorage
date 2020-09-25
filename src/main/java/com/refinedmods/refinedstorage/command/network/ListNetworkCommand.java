package com.refinedmods.refinedstorage.command.network;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.text.DecimalFormat;

public class ListNetworkCommand implements Command<CommandSource> {
    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("list")
            .requires(cs -> cs.hasPermissionLevel(2))
            .then(Commands.argument("dimension", DimensionArgument.getDimension())
                .executes(new ListNetworkCommand()));
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerWorld world = DimensionArgument.getDimensionArgument(context, "dimension");

        API.instance().getNetworkManager(world)
            .all()
            .stream()
            .map(NetworkInList::new)
            .sorted((a, b) -> Double.compare(b.tickTime, a.tickTime))
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
    }

    public static void sendInfo(CommandContext<CommandSource> context, NetworkInList listItem, boolean detailed) {
        context.getSource().sendFeedback(
            new TranslationTextComponent(
                "commands.refinedstorage.network.list.pos",
                listItem.network.getPosition().getX(),
                listItem.network.getPosition().getY(),
                listItem.network.getPosition().getZ()
            )
                .appendString(" [")
                .append(new TranslationTextComponent(
                    "commands.refinedstorage.network.list.tick_times",
                    new StringTextComponent(TIME_FORMATTER.format(listItem.tickTime)).setStyle(Styles.YELLOW),
                    new StringTextComponent(TIME_FORMATTER.format(listItem.tps)).setStyle(Styles.YELLOW)
                ))
                .appendString("]"), false);

        if (detailed) {
            context.getSource().sendFeedback(new TranslationTextComponent("commands.refinedstorage.network.list.autocrafting_tasks",
                new StringTextComponent(listItem.network.getCraftingManager().getTasks().size() + "").setStyle(Styles.YELLOW)
            ), false);

            context.getSource().sendFeedback(new TranslationTextComponent("commands.refinedstorage.network.list.nodes",
                new StringTextComponent(listItem.network.getNodeGraph().all().size() + "").setStyle(Styles.YELLOW)
            ), false);

            context.getSource().sendFeedback(new TranslationTextComponent("commands.refinedstorage.network.list.energy_usage",
                new StringTextComponent(listItem.network.getEnergyUsage() + "").setStyle(Styles.YELLOW)
            ), false);
        }
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values) {
            sum += v;
        }
        return sum / values.length;
    }
}
