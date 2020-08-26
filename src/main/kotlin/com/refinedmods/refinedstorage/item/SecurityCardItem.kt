package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.render.Styles
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.text.Text
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import java.util.*

class SecurityCardItem : Item(Properties().group(RS.MAIN_GROUP).maxStackSize(1)) {
    fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand?): ActionResult<ItemStack> {
        val stack: ItemStack = player.getHeldItem(hand)
        if (!world.isClient) {
            stack.tag = CompoundTag()
            stack.tag!!.putString(NBT_OWNER, player.gameProfile.id.toString())
            stack.tag!!.putString(NBT_OWNER_NAME, player.gameProfile.name)
        }
        return ActionResult.resultSuccess(stack)
    }

    fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        if (stack.hasTag() && stack.tag!!.contains(NBT_OWNER_NAME)) {
            tooltip.add(TranslationTextComponent("item.refinedstorage.security_card.owner", stack.tag!!.getString(NBT_OWNER_NAME)).setStyle(Styles.GRAY))
        }
        for (permission in Permission.values()) {
            if (hasPermission(stack, permission)) {
                tooltip.add(StringTextComponent("- ").append(TranslationTextComponent("gui.refinedstorage.security_manager.permission." + permission.id)).setStyle(Styles.GRAY))
            }
        }
    }

    companion object {
        private const val NBT_OWNER = "Owner"
        private const val NBT_OWNER_NAME = "OwnerName"
        private const val NBT_PERMISSION = "Permission_%d"
        @Nullable
        fun getOwner(stack: ItemStack): UUID? {
            return if (stack.hasTag() && stack.tag!!.contains(NBT_OWNER)) {
                UUID.fromString(stack.tag!!.getString(NBT_OWNER))
            } else null
        }

        @kotlin.jvm.JvmStatic
        fun hasPermission(stack: ItemStack, permission: Permission): Boolean {
            val id = String.format(NBT_PERMISSION, permission.id)
            return if (stack.hasTag() && stack.tag!!.contains(id)) {
                stack.tag!!.getBoolean(id)
            } else false
        }

        fun setPermission(stack: ItemStack, permission: Permission, state: Boolean) {
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.putBoolean(String.format(NBT_PERMISSION, permission.id), state)
        }

        fun isValid(stack: ItemStack): Boolean {
            return stack.hasTag() && stack.tag!!.contains(NBT_OWNER)
        }
    }

    init {
        this.setRegistryName(RS.ID, "security_card")
    }
}