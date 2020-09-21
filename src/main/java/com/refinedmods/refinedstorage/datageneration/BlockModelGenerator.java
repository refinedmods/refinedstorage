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
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelGenerator extends BlockStateProvider {
    private final ResourceLocation BOTTOM = new ResourceLocation(RS.ID, "block/bottom");

    private final BlockModels models;

    public BlockModelGenerator(DataGenerator generator, String id, ExistingFileHelper existingFileHelper) {
        super(generator, id, existingFileHelper);
        models = new BlockModels(this);
    }

    @Override
    protected void registerStatesAndModels() {
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
                    resourceLocation(block, color, "cutouts/disconnected")
                );
            } else {
                ModelFile model = models.createWirelessTransmitterModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    resourceLocation(block, color, "cutouts/" + color)
                );

                simpleBlockItem(block, model);
                return model;
            }
        }, 0);
    }

    private void genDetectorModel(Block block, DyeColor color) {
        models.simpleBlockStateModel(block, state -> {
            if (!state.get(DetectorBlock.POWERED)) {
                return models.createDetectorModel(
                    "block/" + getBlockName(block, color) + "/off",
                    resourceLocation(block, color, "cutouts/off")
                );
            } else {
                ModelFile model = models.createDetectorModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    resourceLocation(block, color, "cutouts/" + color)
                );

                simpleBlockItem(block, model);
                return model;
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
                    resourceLocation(block, color, "top"),
                    resourceLocation(block, color, "cutouts/top_disconnected"),
                    resourceLocation(block, color, "right"),
                    resourceLocation(block, color, "cutouts/right_disconnected"),
                    resourceLocation(block, color, "left"),
                    resourceLocation(block, color, "cutouts/left_disconnected"),
                    resourceLocation(block, color, "front"),
                    resourceLocation(block, color, "cutouts/front_disconnected"),
                    resourceLocation(block, color, "back"),
                    resourceLocation(block, color, "cutouts/back_disconnected")
                );
            } else {
                ModelFile model = models.createCubeCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    BOTTOM,
                    BOTTOM,
                    resourceLocation(block, color, "top"),
                    resourceLocation(block, color, "cutouts/top" + "_" + color),
                    resourceLocation(block, color, "right"),
                    resourceLocation(block, color, "cutouts/right" + "_" + color),
                    resourceLocation(block, color, "left"),
                    resourceLocation(block, color, "cutouts/left" + "_" + color),
                    resourceLocation(block, color, "front"),
                    resourceLocation(block, color, "cutouts/front" + "_" + color),
                    resourceLocation(block, color, "back"),
                    resourceLocation(block, color, "cutouts/back" + "_" + color)
                );

                simpleBlockItem(block, model);
                return model;
            }
        }, 180);
    }

    private void genCubeAllCutoutModel(Block block, DyeColor color) {
        models.simpleBlockStateModel(block, state -> {
            if (state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    resourceLocation(block, color, getBlockName(block, color)),
                    resourceLocation(block, color, getBlockName(block, color)),
                    resourceLocation(block, color, "cutouts/" + color)
                );
            } else {
                ModelFile model = models.createCubeAllCutoutModel(
                    "block/" + getBlockName(block, color) + "/disconnected",
                    resourceLocation(block, color, getBlockName(block, color)),
                    resourceLocation(block, color, getBlockName(block, color)),
                    resourceLocation(block, color, "cutouts/disconnected")
                );

                simpleBlockItem(block, model);
                return model;
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
                    resourceLocation(block, color, "top"),
                    resourceLocation(block, color, "cutouts/top_disconnected"),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_disconnected"),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_disconnected"),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_disconnected"),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_disconnected")
                );
            } else {
                ModelFile model = models.createCubeCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    BOTTOM,
                    BOTTOM,
                    resourceLocation(block, color, "top"),
                    resourceLocation(block, color, "cutouts/top_" + color),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_" + color),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_" + color),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_" + color),
                    resourceLocation(block, color, "side"),
                    resourceLocation(block, color, "cutouts/side_" + color)
                );

                simpleBlockItem(block, model);
                return model;
            }
        }, 180);
    }

    private void genControllerModel(Block block, Block modelBlock, DyeColor color) {
        models.simpleBlockStateModel(block, state -> {
            if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.OFF)) {
                return models.createCubeAllCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/off",
                    resourceLocation(modelBlock, color, "off"),
                    resourceLocation(modelBlock, color, "off"),
                    resourceLocation(modelBlock, color, "cutouts/off")
                );
            } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_OFF)) {
                return models.createControllerNearlyCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/nearly_off",
                    resourceLocation(modelBlock, color, "off"),
                    resourceLocation(modelBlock, color, "on"),
                    resourceLocation(modelBlock, color, "cutouts/nearly_off"),
                    resourceLocation(modelBlock, color, "cutouts/nearly_off_gray")
                );
            } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_ON)) {
                return models.createControllerNearlyCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/nearly_on",
                    resourceLocation(modelBlock, color, "off"),
                    resourceLocation(modelBlock, color, "on"),
                    resourceLocation(modelBlock, color, "cutouts/nearly_on"),
                    resourceLocation(modelBlock, color, "cutouts/nearly_on_gray")
                );
            } else {
                ModelFile model = models.createCubeAllCutoutModel(
                    "block/" + getBlockName(modelBlock, color) + "/" + color,
                    resourceLocation(modelBlock, color, "off"),
                    resourceLocation(modelBlock, color, "on"),
                    resourceLocation(modelBlock, color, "cutouts/" + color)
                );

                simpleBlockItem(block, model);
                return model;
            }
        });
    }

    private void genNorthCutoutModel(Block block, DyeColor color) {
        models.horizontalRSBlock(block, state -> {
            if (!state.get(NetworkNodeBlock.CONNECTED)) {
                return models.createCubeNorthCutoutModel(
                    "block/" + getBlockName(block, color) + "/disconnected",
                    BOTTOM,
                    resourceLocation(block, color, "top"),
                    resourceLocation(block, color, "front"),
                    resourceLocation(block, color, "back"),
                    resourceLocation(block, color, "right"),
                    resourceLocation(block, color, "left"),
                    resourceLocation(block, color, "right"),
                    resourceLocation(block, color, "cutouts/disconnected")
                );
            } else {
                ModelFile model = models.createCubeNorthCutoutModel(
                    "block/" + getBlockName(block, color) + "/" + color,
                    BOTTOM,
                    resourceLocation(block, color, "top"),
                    resourceLocation(block, color, "front"),
                    resourceLocation(block, color, "back"),
                    resourceLocation(block, color, "right"),
                    resourceLocation(block, color, "left"),
                    resourceLocation(block, color, "right"),
                    resourceLocation(block, color, "cutouts/" + color)
                );

                simpleBlockItem(block, model);
                return model;
            }
        }, 180);
    }

    private ResourceLocation resourceLocation(Block block, DyeColor color, String name) {
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
