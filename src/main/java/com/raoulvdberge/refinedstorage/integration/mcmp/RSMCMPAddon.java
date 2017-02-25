package com.raoulvdberge.refinedstorage.integration.mcmp;

import com.raoulvdberge.refinedstorage.RSBlocks;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartOcclusionHelper;
import mcmultipart.api.ref.MCMPCapabilities;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@MCMPAddon
public class RSMCMPAddon implements IMCMPAddon {
    @Override
    public void registerParts(IMultipartRegistry registry) {
        MinecraftForge.EVENT_BUS.register(this);

        registry.registerPartWrapper(RSBlocks.CABLE, new PartCable());
        registry.registerStackWrapper(Item.getItemFromBlock(RSBlocks.CABLE), s -> true, RSBlocks.CABLE);
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<TileEntity> e) {
        e.addCapability(new ResourceLocation("refinedstorage:cable"), new ICapabilityProvider() {
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

    public static boolean hasConnectionWith(IBlockAccess world, BlockPos pos, List<AxisAlignedBB> aabbs) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile != null && tile.hasCapability(MCMPCapabilities.MULTIPART_TILE, null)) {
            IMultipartTile multipartTile = tile.getCapability(MCMPCapabilities.MULTIPART_TILE, null);

            if (multipartTile instanceof PartCableTile && ((PartCableTile) multipartTile).getInfo() != null) {
                for (IPartInfo info : ((PartCableTile) multipartTile).getInfo().getContainer().getParts().values()) {
                    IMultipart multipart = info.getPart();

                    if (MultipartOcclusionHelper.testBoxIntersection(aabbs, multipart.getOcclusionBoxes(info))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
