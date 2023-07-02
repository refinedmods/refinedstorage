package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class CreateDiskCommand implements Command<CommandSourceStack> {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("create")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("id", UuidArgument.uuid()).suggests(new StorageDiskIdSuggestionProvider())
                    .executes(new CreateDiskCommand())
                )
            );
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(context, "player");
        UUID id = UuidArgument.getUuid(context, "id");

        IStorageDisk<?> disk = API.instance().getStorageDiskManager(context.getSource().getLevel()).get(id);
        if (disk == null) {
            context.getSource().sendFailure(Component.translatable("commands.refinedstorage.disk.create.error.disk_not_found", id));
        } else {
            IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(disk.getFactoryId());

            if (factory != null) {
                ItemStack stack = factory.createDiskItem(disk, id);

                // @Volatile: From GiveCommand
                boolean flag = player.getInventory().add(stack);
                if (flag && stack.isEmpty()) {
                    stack.setCount(1);

                    ItemEntity itemEntity = player.drop(stack, false);
                    if (itemEntity != null) {
                        itemEntity.makeFakeItem();
                    }

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.containerMenu.broadcastChanges();
                } else {
                    ItemEntity itemEntity = player.drop(stack, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                        itemEntity.setThrower(player.getUUID());
                    }
                }

                context.getSource().sendSuccess(() -> Component.translatable(
                    "commands.refinedstorage.disk.create.success",
                    Component.literal(id.toString()).setStyle(Styles.YELLOW),
                    context.getSource().getDisplayName().copy().setStyle(Styles.YELLOW)
                ), false);
            }
        }

        return 0;
    }
}
