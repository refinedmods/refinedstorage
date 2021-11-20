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
            .requires(cs -> cs.hasPermissionLevel(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("id", UUIDArgument.func_239194_a_()).suggests(new StorageDiskIdSuggestionProvider())
                    .executes(new CreateDiskCommand())
                )
            );
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = EntityArgument.getPlayer(context, "player");
        UUID id = UUIDArgument.func_239195_a_(context, "id");

        IStorageDisk<?> disk = API.instance().getStorageDiskManager(context.getSource().getWorld()).get(id);
        if (disk == null) {
            context.getSource().sendErrorMessage(new TranslationTextComponent("commands.refinedstorage.disk.create.error.disk_not_found", id));
        } else {
            IStorageDiskFactory factory = API.instance().getStorageDiskRegistry().get(disk.getFactoryId());

            if (factory != null) {
                ItemStack stack = factory.createDiskItem(disk, id);

                // @Volatile: From GiveCommand
                boolean flag = player.inventory.addItemStackToInventory(stack);
                if (flag && stack.isEmpty()) {
                    stack.setCount(1);

                    ItemEntity itemEntity = player.dropItem(stack, false);
                    if (itemEntity != null) {
                        itemEntity.makeFakeItem();
                    }

                    player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    player.container.detectAndSendChanges();
                } else {
                    ItemEntity itemEntity = player.dropItem(stack, false);
                    if (itemEntity != null) {
                        itemEntity.setNoPickupDelay();
                        itemEntity.setOwnerId(player.getUniqueID());
                    }
                }

                context.getSource().sendFeedback(new TranslationTextComponent(
                    "commands.refinedstorage.disk.create.success",
                    new StringTextComponent(id.toString()).setStyle(Styles.YELLOW),
                    context.getSource().getDisplayName().deepCopy().setStyle(Styles.YELLOW)
                ), false);
            }
        }

        return 0;
    }
}
