package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.block.BlockDirection;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class BlockModels {
    private final BlockModelGenerator generator;

    public BlockModels(BlockModelGenerator blockModelGenerator) {
        this.generator = blockModelGenerator;
    }

    public void simpleBlockStateModel(Block block, Function<BlockState, ModelFile> model) {
        generator.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model.apply(state)).build());
    }

    public void anyDirectionalRSBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        generator.getVariantBuilder(block)
            .forAllStates(state -> {
                Direction dir = state.getValue(BlockDirection.ANY.getProperty());

                int xRotation = 0;
                if (dir == Direction.DOWN) {
                    xRotation = 180;
                }
                if (dir.getAxis().isHorizontal()) {
                    xRotation = 90;
                }

                return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(xRotation)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                    .build();
            });
    }

    public void wirelessTransmitterBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        generator.getVariantBuilder(block)
            .forAllStates(state -> {
                Direction dir = state.getValue(BlockDirection.ANY.getProperty());

                int xRotation;
                if (dir.getAxis() == Direction.Axis.Y) {
                    xRotation = dir == Direction.UP ? 180 : 0;
                } else {
                    xRotation = dir.getAxis().isHorizontal() ? 90 : 0;
                }

                return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(xRotation)
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
                    .build();
            });
    }

    public void horizontalRSBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
        generator.getVariantBuilder(block)
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(modelFunc.apply(state))
                .rotationY(((int) state.getValue(BlockDirection.HORIZONTAL.getProperty()).toYRot() + angleOffset) % 360)
                .build()
            );
    }

    public void customLoaderRSBlock(Block block, ResourceLocation path, ModelFile connectedModel, ModelFile disconnectedModel) {
        var model = generator.models().getBuilder(path.toString())
            .renderType("cutout").customLoader((builder, filehelper) ->
                new CustomLoaderBuilderBasic(new ResourceLocation(RS.ID, "disk_manipulator"), builder, filehelper)).end();

        generator.getVariantBuilder(block).setModels(generator.getVariantBuilder(block).partialState(),
            ConfiguredModel.builder().modelFile(model).buildLast());
    }

    public BlockModelBuilder createDetectorModel(String name, ResourceLocation torch) {
        return generator.models().withExistingParent(name, new ResourceLocation(RS.ID, "detector"))
            .texture("torch", torch);
    }

    public BlockModelBuilder createWirelessTransmitterModel(String name, ResourceLocation cutout) {
        return generator.models().withExistingParent(name, new ResourceLocation(RS.ID, "wireless_transmitter"))
            .texture("cutout", cutout);
    }

    public BlockModelBuilder createCubeCutoutModel(String name, ResourceLocation down, ResourceLocation downCutout, ResourceLocation up, ResourceLocation upCutout, ResourceLocation east, ResourceLocation eastCutout, ResourceLocation west, ResourceLocation westCutout, ResourceLocation north, ResourceLocation northCutout, ResourceLocation south, ResourceLocation southCutout) {
        return generator.models().withExistingParent(name, new ResourceLocation(RS.ID, "cube_cutout"))
            .texture("particle", north)
            .texture("east", east)
            .texture("south", south)
            .texture("west", west)
            .texture("up", up)
            .texture("down", down)
            .texture("north", north)
            .texture("cutout_down", downCutout)
            .texture("cutout_east", eastCutout)
            .texture("cutout_west", westCutout)
            .texture("cutout_south", southCutout)
            .texture("cutout_north", northCutout)
            .texture("cutout_up", upCutout);
    }

    public BlockModelBuilder createControllerNearlyCutoutModel(String name, ResourceLocation particle, ResourceLocation all, ResourceLocation grayCutout, ResourceLocation cutout) {
        return generator.models().withExistingParent(name, new ResourceLocation(RS.ID, "block/controller_nearly"))
            .texture("particle", particle)
            .texture("all", all)
            .texture("cutout_gray", grayCutout)
            .texture("cutout", cutout);
    }

    public BlockModelBuilder createCubeAllCutoutModel(String name, ResourceLocation particle, ResourceLocation all, ResourceLocation cutout) {
        return generator.models().withExistingParent(name, new ResourceLocation(RS.ID, "cube_all_cutout"))
            .texture("particle", particle)
            .texture("all", all)
            .texture("cutout", cutout);
    }

    public BlockModelBuilder createCubeNorthCutoutModel(String name, ResourceLocation down, ResourceLocation up, ResourceLocation north, ResourceLocation south, ResourceLocation east, ResourceLocation west, ResourceLocation particle, ResourceLocation cutout) {
        return generator.models().withExistingParent(name, new ResourceLocation(RS.ID, "cube_north_cutout"))
            .texture("particle", particle)
            .texture("east", east)
            .texture("south", south)
            .texture("west", west)
            .texture("up", up)
            .texture("down", down)
            .texture("north", north)
            .texture("cutout", cutout);
    }

    class CustomLoaderBuilderBasic extends CustomLoaderBuilder<BlockModelBuilder> {
        protected CustomLoaderBuilderBasic(ResourceLocation loaderId, BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
            super(loaderId, parent, existingFileHelper);
        }
    }
}
