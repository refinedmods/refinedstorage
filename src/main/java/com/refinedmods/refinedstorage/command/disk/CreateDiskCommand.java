package com.refinedmods.refinedstorage.command.disk;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk;
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class CreateDiskCommand implements Command<CommandSource> {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("create")
            .requires(cs -> cs.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("id", UUIDArgument.uuid()).suggests(new StorageDiskIdSuggestionProvider())
                    .executes(new CreateDiskCommand())
                )
            );
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = EntityArgument.getPlayer(context, "player");
        UUID id = UUIDArgument.getUuid(context, "id");

        IStorageDisk<?> disk = API.instance().getStorageDiskManager(context.getSource().getLevel()).get(id);
        if (disk == null) {
            context.getSource().sendFailure(new TranslationTextComponent("commands.refinedstorage.disk.create.error.disk_not_found", id));
        } else {
            IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(disk.getFactoryId());

            if (factory != null) {
                ItemStack stack = factory.createDiskItem(disk, id);

                // @Volatile: From GiveCommand
                boolean flag = player.inventory.add(stack);
                if (flag && stack.isEmpty()) {
                    stack.setCount(1);

                    ItemEntity itemEntity = player.drop(stack, false);
                    if (itemEntity != null) {
                        itemEntity.makeFakeItem();
                    }

                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity itemEntity = player.drop(stack, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                        itemEntity.setOwner(player.getUUID());
                    }
                }

                context.getSource().sendSuccess(new TranslationTextComponent(
                    "commands.refinedstorage.disk.create.success",
                    new StringTextComponent(id.toString()).setStyle(Styles.YELLOW),
                    context.getSource().getDisplayName().copy().setStyle(Styles.YELLOW)
                ), false);
            }
        }

        return 0;
    }
}
