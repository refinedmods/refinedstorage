package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class BlockModelGenerator extends BlockStateProvider {
    private final ResourceLocation BOTTOM = new ResourceLocation(RS.ID, "block/bottom");
    private BlockModels models;

    public BlockModelGenerator(DataGenerator generator, String id, ExistingFileHelper existingFileHelper) {
        super(generator, id, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        models = new BlockModels(this);
        genNorthCutoutModel(RSBlocks.GRID);
        genNorthCutoutModel(RSBlocks.CRAFTING_GRID);
        genNorthCutoutModel(RSBlocks.PATTERN_GRID);
        genNorthCutoutModel(RSBlocks.FLUID_GRID);
        genNorthCutoutModel(RSBlocks.CRAFTING_MONITOR);
        genNorthCutoutModel(RSBlocks.CRAFTER_MANAGER);
        genNorthCutoutModel(RSBlocks.DISK_MANIPULATOR);
        genControllerModel(RSBlocks.CONTROLLER);
        genControllerModel(RSBlocks.CREATIVE_CONTROLLER);
        genCrafterModel(RSBlocks.CRAFTER);
        genCubeAllCutoutModel(RSBlocks.RELAY);
        genCubeAllCutoutModel(RSBlocks.NETWORK_TRANSMITTER);
        genCubeAllCutoutModel(RSBlocks.NETWORK_RECEIVER);
        genCubeCutoutModel(RSBlocks.SECURITY_MANAGER);

    }

    private void genCubeCutoutModel(Block block) {
        models.horizontalRSBlock(block, state -> {
            if (!state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block) + "/disconnected",
                    BOTTOM,
                    BOTTOM,
                    getRL(block, "top"),
                    getRL(block, "cutouts/top_disconnected"),
                    getRL(block, "right"),
                    getRL(block, "cutouts/right_disconnected"),
                    getRL(block, "left"),
                    getRL(block, "cutouts/left_disconnected"),
                    getRL(block, "front"),
                    getRL(block, "cutouts/front_disconnected"),
                    getRL(block, "back"),
                    getRL(block, "cutouts/back_disconnected"));
            } else {
                DyeColor color = state.get(BlockUtils.COLOR_PROPERTY);
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block) + "/" + color,
                    BOTTOM,
                    BOTTOM,
                    getRL(block, "top"),
                    getRL(block, "cutouts/top" + "_" + color),
                    getRL(block, "right"),
                    getRL(block, "cutouts/right" + "_" + color),
                    getRL(block, "left"),
                    getRL(block, "cutouts/left" + "_" + color),
                    getRL(block, "front"),
                    getRL(block, "cutouts/front" + "_" + color),
                    getRL(block, "back"),
                    getRL(block, "cutouts/back" + "_" + color));
            }
        }, 180);
    }

    private void genCubeAllCutoutModel(Block block) {
        models.simpleBlockStateModel(block, state -> {
            if (state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block) + "/" + state.get(BlockUtils.COLOR_PROPERTY),
                    getRL(block, getBlockName(block)),
                    getRL(block, getBlockName(block)),
                    getRL(block, "cutouts/" + state.get(BlockUtils.COLOR_PROPERTY)));
            } else {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block) + "/disconnected",
                    getRL(block, getBlockName(block)),
                    getRL(block, getBlockName(block)),
                    getRL(block, "cutouts/disconnected"));
            }
        });
    }

    private void genCrafterModel(Block block) {
        models.anyDirectionalRSBlock(block, state -> {
            if (!state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block) + "/disconnected",
                    BOTTOM,
                    BOTTOM,
                    getRL(block, "top"),
                    getRL(block, "cutouts/top_disconnected"),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_disconnected"),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_disconnected"),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_disconnected"),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_disconnected"));
            } else {
                DyeColor color = state.get(BlockUtils.COLOR_PROPERTY);
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block) + "/" + color,
                    BOTTOM,
                    BOTTOM,
                    getRL(block, "top"),
                    getRL(block, "cutouts/top_" + color),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_" + color),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_" + color),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_" + color),
                    getRL(block, "side"),
                    getRL(block, "cutouts/side_" + color));
            }
        }, 180);
    }

    private void genControllerModel(Block block) {
        //avoid assigning new models to Creative Controller
        ControllerBlock controllerBlock = RSBlocks.CONTROLLER;
        models.simpleBlockStateModel(block, state -> {
            if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.OFF)) {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block) + "/off",
                    getRL(controllerBlock, "off"),
                    getRL(controllerBlock, "off"),
                    getRL(controllerBlock, "cutouts/off")
                );
            } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_OFF)) {
                return models.createControllerNearlyCutoutModel(
                    "block/" + getBlockName(block) + "/nearly_off",
                    getRL(controllerBlock, "off"),
                    getRL(controllerBlock, "on"),
                    getRL(controllerBlock, "cutouts/nearly_off"),
                    getRL(controllerBlock, "cutouts/nearly_off_gray"));
            } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_ON)) {
                return models.createControllerNearlyCutoutModel(
                    "block/" + getBlockName(block) + "/nearly_on",
                    getRL(controllerBlock, "off"),
                    getRL(controllerBlock, "on"),
                    getRL(controllerBlock, "cutouts/nearly_on"),
                    getRL(controllerBlock, "cutouts/nearly_on_gray"));
            } else {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block) + "/on_" + state.get(BlockUtils.COLOR_PROPERTY),
                    getRL(controllerBlock, "off"),
                    getRL(controllerBlock, "on"),
                    getRL(controllerBlock, "cutouts/on_" + state.get(BlockUtils.COLOR_PROPERTY)));
            }
        });
    }

    private void genNorthCutoutModel(Block block) {
        models.horizontalRSBlock(block, state -> {
                if (!state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createCubeNorthCutoutModel(
                        "block/" + getBlockName(block) + "/disconnected",
                        BOTTOM,
                        getRL(block, "top"),
                        getRL(block, "front"),
                        getRL(block, "back"),
                        getRL(block, "right"),
                        getRL(block, "left"),
                        getRL(block, "right"),
                        getRL(block, "cutouts/disconnected"));
                } else {
                    return models.createCubeNorthCutoutModel(
                        "block/" + getBlockName(block) + "/" + state.get(BlockUtils.COLOR_PROPERTY),
                        BOTTOM,
                        getRL(block, "top"),
                        getRL(block, "front"),
                        getRL(block, "back"),
                        getRL(block, "right"),
                        getRL(block, "left"),
                        getRL(block, "right"),
                        getRL(block, "cutouts/" + state.get(BlockUtils.COLOR_PROPERTY)));
                }
            }
            , 180);
    }

    private ResourceLocation getRL(Block block, String name) {
        return new ResourceLocation(RS.ID, "block/" + getBlockName(block) + "/" + name);
    }

    private String getBlockName(Block block) {
        return Objects.requireNonNull(block.getRegistryName()).getPath();
    }
}
