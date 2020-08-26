package com.refinedmods.refinedstorage.tile.craftingmonitor

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem.Companion.setTabPage
import com.refinedmods.refinedstorage.item.WirelessCraftingMonitorItem.Companion.setTabSelected
import com.refinedmods.refinedstorage.network.craftingmonitor.WirelessCraftingMonitorSettingsUpdateMessage
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.NetworkUtils
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.Text
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import java.util.*

class WirelessCraftingMonitor(val stack: ItemStack, @field:Nullable @param:Nullable private val server: MinecraftServer, override val slotId: Int) : ICraftingMonitor {

    private val nodeDimension: RegistryKey<World>
    private val nodePos: BlockPos
    override var tabPage: Int
        private set
    override var tabSelected: Optional<UUID?>
        private set

    fun setSettings(tabSelected: Optional<UUID?>, tabPage: Int) {
        this.tabSelected = tabSelected
        this.tabPage = tabPage
        setTabSelected(stack, tabSelected)
        setTabPage(stack, tabPage)
    }

    override val title: Text
        get() = TranslationTextComponent("gui.refinedstorage.wireless_crafting_monitor")

    override fun onCancelled(player: ServerPlayerEntity?, @Nullable id: UUID?) {
        val network = network
        if (network != null) {
            network.itemGridHandler!!.onCraftingCancelRequested(player, id)
        }
    }

    override val redstoneModeParameter: TileDataParameter<Int?, *>?
        get() = null
    override val tasks: Collection<ICraftingTask?>?
        get() {
            val network = network
            return if (network != null) {
                network.craftingManager.getTasks()
            } else emptyList()
        }

    @get:Nullable
    override val craftingManager: ICraftingManager?
        get() {
            val network = network
            return network?.craftingManager
        }
    private val network: INetwork?
        private get() {
            val world: World? = server.getWorld(nodeDimension)
            return if (world != null) {
                NetworkUtils.getNetworkFromNode(NetworkUtils.getNodeFromTile(world.getBlockEntity(nodePos)))
            } else null
        }
    override val isActiveOnClient: Boolean
        get() = true

    override fun onClosed(player: PlayerEntity?) {
        val network = network
        if (network != null) {
            network.networkItemManager!!.close(player)
        }
    }

    override fun onTabSelectionChanged(taskId: Optional<UUID?>) {
        if (taskId.isPresent && tabSelected.isPresent && taskId.get() == tabSelected.get()) {
            tabSelected = Optional.empty()
        } else {
            tabSelected = taskId
        }
        RS.NETWORK_HANDLER.sendToServer(WirelessCraftingMonitorSettingsUpdateMessage(tabSelected, tabPage))
    }

    override fun onTabPageChanged(page: Int) {
        if (page >= 0) {
            tabPage = page
            RS.NETWORK_HANDLER.sendToServer(WirelessCraftingMonitorSettingsUpdateMessage(tabSelected, tabPage))
        }
    }

    init {
        nodeDimension = WirelessCraftingMonitorItem.getDimension(stack)
        nodePos = BlockPos(WirelessCraftingMonitorItem.getX(stack), WirelessCraftingMonitorItem.getY(stack), WirelessCraftingMonitorItem.getZ(stack))
        tabPage = WirelessCraftingMonitorItem.getTabPage(stack)
        tabSelected = WirelessCraftingMonitorItem.getTabSelected(stack)
    }
}