package com.raoulvdberge.refinedstorage.integration.mcmp;

import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.tile.*;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartOcclusionHelper;
import mcmultipart.api.ref.MCMPCapabilities;
import mcmultipart.api.slot.EnumCenterSlot;
import mcmultipart.block.BlockMultipartContainer;
import mcmultipart.block.TileMultipartContainer;
import mcmultipart.util.MCMPWorldWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@MCMPAddon
public class RSMCMPAddon implements IMCMPAddon {
    @Override
    public void registerParts(IMultipartRegistry registry) {
        MinecraftForge.EVENT_BUS.register(this);

        register(registry, RSBlocks.CABLE);
        register(registry, RSBlocks.CONSTRUCTOR);
        register(registry, RSBlocks.DESTRUCTOR);
        register(registry, RSBlocks.IMPORTER);
        register(registry, RSBlocks.EXPORTER);
        register(registry, RSBlocks.EXTERNAL_STORAGE);
        register(registry, RSBlocks.READER);
        register(registry, RSBlocks.WRITER);
    }

    private void register(IMultipartRegistry registry, BlockCable block) {
        registry.registerPartWrapper(block, new PartCable(block));
        registry.registerStackWrapper(Item.getItemFromBlock(block), s -> true, block);
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<TileEntity> e) {
        TileEntity tile = e.getObject();

        if (tile instanceof TileCable) {
            register(e, "cable");
        } else if (tile instanceof TileConstructor) {
            register(e, "constructor");
        } else if (tile instanceof TileDestructor) {
            register(e, "destructor");
        } else if (tile instanceof TileImporter) {
            register(e, "importer");
        } else if (tile instanceof TileExporter) {
            register(e, "exporter");
        } else if (tile instanceof TileExternalStorage) {
            register(e, "external_storage");
        } else if (tile instanceof TileReader) {
            register(e, "reader");
        } else if (tile instanceof TileWriter) {
            register(e, "writer");
        }
    }

    private void register(AttachCapabilitiesEvent<TileEntity> e, String id) {
        e.addCapability(new ResourceLocation("refinedstorage:" + id), new ICapabilityProvider() {
            private PartCableTile tile;

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == MCMPCapabilities.MULTIPART_TILE;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                if (capability == MCMPCapabilities.MULTIPART_TILE) {
                    if (tile == null) {
                        tile = new PartCableTile(e.getObject());
                    }

                    return MCMPCapabilities.MULTIPART_TILE.cast(tile);
                }

                return null;
            }
        });
    }

    public static boolean hasObstructingMultipart(TileEntity tile, List<AxisAlignedBB> testBoxes) {
        if (tile != null && tile.hasCapability(MCMPCapabilities.MULTIPART_TILE, null)) {
            IMultipartTile multipartTile = tile.getCapability(MCMPCapabilities.MULTIPART_TILE, null);

            if (multipartTile instanceof PartCableTile && ((PartCableTile) multipartTile).getInfo() != null) {
                for (IPartInfo info : ((PartCableTile) multipartTile).getInfo().getContainer().getParts().values()) {
                    IMultipart multipart = info.getPart();

                    if (!(multipart instanceof PartCable) && MultipartOcclusionHelper.testBoxIntersection(testBoxes, multipart.getOcclusionBoxes(info))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    public static TileEntity unwrapTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileMultipartContainer) {
            Optional<IMultipartTile> multipartTile = ((TileMultipartContainer) tile).getPartTile(EnumCenterSlot.CENTER);

            if (multipartTile.isPresent()) {
                return multipartTile.get().getTileEntity();
            }
        }

        return tile;
    }

    public static World unwrapWorld(World world) {
        if (world instanceof MCMPWorldWrapper) {
            return ((MCMPWorldWrapper) world).getActualWorld();
        }

        return world;
    }

    public static Block unwrapBlock(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockMultipartContainer) {
            Optional<TileMultipartContainer> multipartContainer = BlockMultipartContainer.getTile(world, pos);

            if (multipartContainer.isPresent()) {
                Optional<IPartInfo> info = multipartContainer.get().get(EnumCenterSlot.CENTER);

                if (info.isPresent()) {
                    return info.get().getPart().getBlock();
                }
            }
        }

        return state.getBlock();
    }
}
