package com.raoulvdberge.refinedstorage.command;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandCreateDisk extends CommandBase {
    @Override
    public String getName() {
        return "createdisk";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.refinedstorage.createdisk.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException("commands.refinedstorage.createdisk.usage");
        } else {
            PlayerEntity player = getPlayer(server, sender, args[0]);
            Item item = getItemByText(sender, args[1]);
            int metadata = parseInt(args[2]);

            UUID id;
            try {
                id = UUID.fromString(args[3]);
            } catch (IllegalArgumentException e) {
                throw new CommandException("commands.refinedstorage.createdisk.error.diskNotFound", args[3]);
            }

            if (!(item instanceof IStorageDiskProvider)) {
                throw new CommandException("commands.refinedstorage.createdisk.error.notADisk");
            }

            IStorageDisk disk = API.instance().getStorageDiskManager(sender.getEntityWorld()).get(id);
            if (disk == null) {
                throw new CommandException("commands.refinedstorage.createdisk.error.diskNotFound", args[3]);
            }

            ItemStack diskStack = new ItemStack(item, 1, metadata);
            ((IStorageDiskProvider) item).setId(diskStack, id);

            if (player.inventory.addItemStackToInventory(diskStack)) {
                // From CommandGive
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.inventoryContainer.detectAndSendChanges();
            }

            notifyCommandListener(sender, this, "commands.refinedstorage.createdisk.success", args[3], player.getName());
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, Item.REGISTRY.getKeys());
        } else if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, API.instance().getStorageDiskManager(sender.getEntityWorld()).getAll().keySet().stream().map(UUID::toString).collect(Collectors.toList()));
        }

        return Collections.emptyList();
    }
}
