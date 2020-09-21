package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.block.DetectorBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelGenerator extends BlockStateProvider {
    private final ResourceLocation BOTTOM = new ResourceLocation(RS.ID, "block/bottom");
    private BlockModels models;

    public BlockModelGenerator(DataGenerator generator, String id, ExistingFileHelper existingFileHelper) {
        super(generator, id, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        models = new BlockModels(this);
        RSBlocks.GRID.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.CRAFTING_GRID.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.PATTERN_GRID.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.FLUID_GRID.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.CRAFTING_MONITOR.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.CRAFTER_MANAGER.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.DISK_MANIPULATOR.forEach((color, block) -> genNorthCutoutModel(block.get(), color));
        RSBlocks.CONTROLLER.forEach((color, block) -> genControllerModel(block.get(), block.get(), color));
        RSBlocks.CREATIVE_CONTROLLER.forEach((color, block) -> genControllerModel(block.get(), RSBlocks.CONTROLLER.get(color).get(), color));
        RSBlocks.CRAFTER.forEach((color, block) -> genCrafterModel(block.get(), color));
        RSBlocks.RELAY.forEach((color, block) -> genCubeAllCutoutModel(block.get(), color));
        RSBlocks.NETWORK_TRANSMITTER.forEach((color, block) -> genCubeAllCutoutModel(block.get(), color));
        RSBlocks.NETWORK_RECEIVER.forEach((color, block) -> genCubeAllCutoutModel(block.get(), color));
        RSBlocks.SECURITY_MANAGER.forEach((color, block) -> genCubeCutoutModel(block.get(), color));
        RSBlocks.DETECTOR.forEach((color, block) -> genDetectorModel(block.get(), color));
        RSBlocks.WIRELESS_TRANSMITTER.forEach((color, block) -> genWirelessTransmitterModel(block.get(), color));
    }

    private void genWirelessTransmitterModel(Block block, DyeColor color) {
        models.wirelessTransmitterBlock(block, state -> {
            if (!state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createWirelessTransmitterModel(
                    "block/" + getBlockName(block, color) + "/disconnected",
                    getRL(block, color, "cutouts/disconnected"));
            } else {
                return models.createWirelessTransmitterModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    getRL(block, color, "cutouts/" + color));
            }
        }, 0);
    }

    private void genDetectorModel(Block block, DyeColor color) {
        models.simpleBlockStateModel(block, state -> {
            if (!state.get(DetectorBlock.POWERED)) {
                return models.createDetectorModel(
                    "block/" + getBlockName(block, color) + "/off",
                    getRL(block, color, "cutouts/off"));
            } else {
                return models.createDetectorModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    getRL(block, color, "cutouts/" + color));
            }
        });
    }

    private void genCubeCutoutModel(Block block, DyeColor color) {
        models.horizontalRSBlock(block, state -> {
            if (!state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block, color) + "/disconnected",
                    BOTTOM,
                    BOTTOM,
                    getRL(block, color, "top"),
                    getRL(block, color, "cutouts/top_disconnected"),
                    getRL(block, color, "right"),
                    getRL(block, color, "cutouts/right_disconnected"),
                    getRL(block, color, "left"),
                    getRL(block, color, "cutouts/left_disconnected"),
                    getRL(block, color, "front"),
                    getRL(block, color, "cutouts/front_disconnected"),
                    getRL(block, color, "back"),
                    getRL(block, color, "cutouts/back_disconnected"));
            } else {
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    BOTTOM,
                    BOTTOM,
                    getRL(block, color, "top"),
                    getRL(block, color, "cutouts/top" + "_" + color),
                    getRL(block, color, "right"),
                    getRL(block, color, "cutouts/right" + "_" + color),
                    getRL(block, color, "left"),
                    getRL(block, color, "cutouts/left" + "_" + color),
                    getRL(block, color, "front"),
                    getRL(block, color, "cutouts/front" + "_" + color),
                    getRL(block, color, "back"),
                    getRL(block, color, "cutouts/back" + "_" + color));
            }
        }, 180);
    }

    private void genCubeAllCutoutModel(Block block, DyeColor color) {
        models.simpleBlockStateModel(block, state -> {
            if (state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    getRL(block, color, getBlockName(block, color)),
                    getRL(block, color, getBlockName(block, color)),
                    getRL(block, color, "cutouts/" + color));
            } else {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block, color) + "/disconnected",
                    getRL(block, color, getBlockName(block, color)),
                    getRL(block, color, getBlockName(block, color)),
                    getRL(block, color, "cutouts/disconnected"));
            }
        });
    }

    private void genCrafterModel(Block block, DyeColor color) {
        models.anyDirectionalRSBlock(block, state -> {
            if (!state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block, color) + "/disconnected",
                    BOTTOM,
                    BOTTOM,
                    getRL(block, color, "top"),
                    getRL(block, color, "cutouts/top_disconnected"),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_disconnected"),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_disconnected"),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_disconnected"),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_disconnected"));
            } else {
                return models.createCubeCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    BOTTOM,
                    BOTTOM,
                    getRL(block, color, "top"),
                    getRL(block, color, "cutouts/top_" + color),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_" + color),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_" + color),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_" + color),
                    getRL(block, color, "side"),
                    getRL(block, color, "cutouts/side_" + color));
            }
        }, 180);
    }

    private void genControllerModel(Block block, Block modelBlock, DyeColor color) {
        models.simpleBlockStateModel(block, state -> {
            if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.OFF)) {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/off",
                    getRL(modelBlock, color, "off"),
                    getRL(modelBlock, color, "off"),
                    getRL(modelBlock, color, "cutouts/off")
                );
            } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_OFF)) {
                return models.createControllerNearlyCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/nearly_off",
                    getRL(modelBlock, color, "off"),
                    getRL(modelBlock, color, "on"),
                    getRL(modelBlock, color, "cutouts/nearly_off"),
                    getRL(modelBlock, color, "cutouts/nearly_off_gray"));
            } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_ON)) {
                return models.createControllerNearlyCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/nearly_on",
                    getRL(modelBlock, color, "off"),
                    getRL(modelBlock, color, "on"),
                    getRL(modelBlock, color, "cutouts/nearly_on"),
                    getRL(modelBlock, color, "cutouts/nearly_on_gray"));
            } else {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/" + color,
                    getRL(modelBlock, color, "off"),
                    getRL(modelBlock, color, "on"),
                    getRL(modelBlock, color, "cutouts/" + color));
            }
        });
    }

    private void genNorthCutoutModel(Block block, DyeColor color) {
        models.horizontalRSBlock(block, state -> {
                if (!state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createCubeNorthCutoutModel(
                        "block/" + getBlockName(block, color) + "/disconnected",
                        BOTTOM,
                        getRL(block, color, "top"),
                        getRL(block, color, "front"),
                        getRL(block, color, "back"),
                        getRL(block, color, "right"),
                        getRL(block, color, "left"),
                        getRL(block, color, "right"),
                        getRL(block, color, "cutouts/disconnected"));
                } else {
                    return models.createCubeNorthCutoutModel(
                        "block/" + getBlockName(block, color) + "/" + color,
                        BOTTOM,
                        getRL(block, color, "top"),
                        getRL(block, color, "front"),
                        getRL(block, color, "back"),
                        getRL(block, color, "right"),
                        getRL(block, color, "left"),
                        getRL(block, color, "right"),
                        getRL(block, color, "cutouts/" + color));
                }
            }
            , 180);
    }

    private ResourceLocation getRL(Block block, DyeColor color, String name) {
        return new ResourceLocation(RS.ID, "block/" + getBlockName(block, color) + "/" + name);
    }

    private String getBlockName(Block block, DyeColor color) {
        String name = block.getRegistryName().getPath();
        if (color == DyeColor.LIGHT_BLUE) {
            return name;
        } else {
            return name.substring(name.indexOf(color.getString()) + color.getString().length() + 1);
        }
    }
}
