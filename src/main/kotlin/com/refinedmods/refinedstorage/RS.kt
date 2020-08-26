package com.refinedmods.refinedstorage

//import com.refinedmods.refinedstorage.config.ClientConfig
//import com.refinedmods.refinedstorage.config.ServerConfig
//import com.refinedmods.refinedstorage.network.NetworkHandler
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object RS: ModInitializer {
    val log = getCustomLogger(RS::class)
    const val ID = "refinedstorage"
//    val NETWORK_HANDLER = NetworkHandler()
//    val MAIN_GROUP: ItemGroup = FabricItemGroupBuilder
//            .create(Identifier(ID, ID))
//            .icon {ItemStack(RSBlocks.CREATIVE_CONTROLLER) }
//            .build() // TODO add to item group
//    val SERVER_CONFIG = ServerConfig()
//    val CLIENT_CONFIG = ClientConfig()

    override fun onInitialize() {
        // TODO Register stuff!
        log.info("Initializing...")
//        DistExecutor.safeRunWhenOn(Dist.CLIENT, { { ClientSetup() } })
//        MinecraftForge.EVENT_BUS.register(ServerSetup())
//        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec())
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec())
//        val commonSetup = CommonSetup()
//        FMLJavaModLoadingContext.get().getModEventBus().addListener({ e: ? -> commonSetup.onCommonSetup(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block::class.java, { e:<net.minecraft.block.Block?> -> commonSetup.onRegisterBlocks(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType::class.java, { e:<<>?> -> commonSetup.onRegisterTiles(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item::class.java, { e:<net.minecraft.item.Item?> -> commonSetup.onRegisterItems(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(IRecipeSerializer::class.java, { e:<<>?> -> commonSetup.onRegisterRecipeSerializers(e) })
//        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType::class.java, { e:<<>?> -> commonSetup.onRegisterContainers(e) })
//        deliver()
        log.info("Initialized!")
    }
}