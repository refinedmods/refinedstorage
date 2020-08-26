package com.refinedmods.refinedstorage.capability

import com.google.common.base.Preconditions
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.storage.IStorage
import java.util.concurrent.Callable

object NetworkNodeProxyCapability {
//    @JvmField
//    @CapabilityInject(INetworkNodeProxy::class)
//    val NETWORK_NODE_PROXY_CAPABILITY: Capability<INetworkNodeProxy<*>>? = null
//    @JvmStatic
//    fun register() {
//        CapabilityManager.INSTANCE.register(INetworkNodeProxy::class.java, Storage(), Factory())
//        Preconditions.checkNotNull<Any>(NETWORK_NODE_PROXY_CAPABILITY, "Capability not registered")
//    }

//    private class Storage : IStorage<INetworkNodeProxy<*>?> {
//        @Nullable
//        fun writeNBT(capability: Capability<INetworkNodeProxy<*>?>?, instance: INetworkNodeProxy<*>?, side: Direction?): INBT? {
//            return null
//        }
//
//        fun readNBT(capability: Capability<INetworkNodeProxy<*>?>?, instance: INetworkNodeProxy<*>?, side: Direction?, nbt: INBT?) {
//            // NO OP
//        }
//    }

    private class Factory : Callable<INetworkNodeProxy<*>> {
        override fun call(): INetworkNodeProxy<*> {
            return object : INetworkNodeProxy<INetworkNode> {

                override val node: INetworkNode
                    get() {
                        throw UnsupportedOperationException("Cannot use default implementation")
                    }
            }
        }
    }
}