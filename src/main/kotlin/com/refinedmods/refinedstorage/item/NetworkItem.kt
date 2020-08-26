package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.item.INetworkItemProvider
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.util.NetworkUtils
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import java.util.function.Consumer
import java.util.function.Supplier

abstract class NetworkItem(item: Item.Properties?, creative: Boolean, energyCapacity: Supplier<Int>) : EnergyItem(item, creative, energyCapacity), INetworkItemProvider {
    fun onItemRightClick(world: World, player: PlayerEntity, hand: Hand?): ActionResult<ItemStack> {
        val stack: ItemStack = player.getHeldItem(hand)
        if (!world.isClient) {
            applyNetwork(world.server, stack, Consumer { n: INetwork -> n.networkItemManager!!.open(player, player.getHeldItem(hand), player.inventory.currentItem) }, Consumer<Text?> { err: Text? -> player.sendMessage(err, player.getUniqueID()) })
        }
        return ActionResult.resultSuccess(stack)
    }

    fun applyNetwork(server: MinecraftServer?, stack: ItemStack, onNetwork: Consumer<INetwork>, onError: Consumer<Text?>) {
        val notFound = TranslationTextComponent("misc.refinedstorage.network_item.not_found")
        if (!isValid(stack)) {
            onError.accept(notFound)
            return
        }
        val dimension: RegistryKey<World>? = getDimension(stack)
        if (dimension == null) {
            onError.accept(notFound)
            return
        }
        val nodeWorld: World? = server!!.getWorld(dimension)
        if (nodeWorld == null) {
            onError.accept(notFound)
            return
        }
        val network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(nodeWorld.getBlockEntity(BlockPos(getX(stack), getY(stack), getZ(stack)))))
        if (network == null) {
            onError.accept(notFound)
            return
        }
        onNetwork.accept(network)
    }

    override fun addInformation(stack: ItemStack, @Nullable world: World?, tooltip: MutableList<Text?>, flag: ITooltipFlag?) {
        super.addInformation(stack, world, tooltip, flag)
        if (isValid(stack)) {
            tooltip.add(TranslationTextComponent("misc.refinedstorage.network_item.tooltip", getX(stack), getY(stack), getZ(stack)).setStyle(Styles.GRAY))
        }
    }

    fun onItemUse(ctx: ItemUseContext): ActionResult {
        val stack: ItemStack = ctx.getPlayer().getHeldItem(ctx.getHand())
        val network = NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(ctx.getWorld().getBlockEntity(ctx.getPos())))
        if (network != null) {
            var tag = stack.tag
            if (tag == null) {
                tag = CompoundTag()
            }
            tag.putInt(NBT_NODE_X, network.position!!.x)
            tag.putInt(NBT_NODE_Y, network.position!!.y)
            tag.putInt(NBT_NODE_Z, network.position!!.z)
            tag.putString(NBT_DIMENSION, ctx.getWorld().func_234923_W_().func_240901_a_().toString())
            stack.tag = tag
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    fun shouldCauseReequipAnimation(oldStack: ItemStack?, newStack: ItemStack?, slotChanged: Boolean): Boolean {
        return false
    }

    companion object {
        private const val NBT_NODE_X = "NodeX"
        private const val NBT_NODE_Y = "NodeY"
        private const val NBT_NODE_Z = "NodeZ"
        private const val NBT_DIMENSION = "Dimension"
        @kotlin.jvm.JvmStatic
        @Nullable
        fun getDimension(stack: ItemStack): RegistryKey<World>? {
            if (stack.hasTag() && stack.tag!!.contains(NBT_DIMENSION)) {
                val name: Identifier = Identifier.tryCreate(stack.tag!!.getString(NBT_DIMENSION))
                        ?: return null
                return RegistryKey.func_240903_a_(Registry.WORLD_KEY, name)
            }
            return null
        }

        @kotlin.jvm.JvmStatic
        fun getX(stack: ItemStack): Int {
            return stack.tag!!.getInt(NBT_NODE_X)
        }

        @kotlin.jvm.JvmStatic
        fun getY(stack: ItemStack): Int {
            return stack.tag!!.getInt(NBT_NODE_Y)
        }

        @kotlin.jvm.JvmStatic
        fun getZ(stack: ItemStack): Int {
            return stack.tag!!.getInt(NBT_NODE_Z)
        }

        fun isValid(stack: ItemStack): Boolean {
            return (stack.hasTag()
                    && stack.tag!!.contains(NBT_NODE_X)
                    && stack.tag!!.contains(NBT_NODE_Y)
                    && stack.tag!!.contains(NBT_NODE_Z)
                    && stack.tag!!.contains(NBT_DIMENSION))
        }
    }
}