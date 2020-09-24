package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.block.DetectorBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

import java.util.Map;

public class BlockModelGenerator extends BlockStateProvider {
    private final ResourceLocation BOTTOM = new ResourceLocation(RS.ID, "block/bottom");

    private final BlockModels models;

    public BlockModelGenerator(DataGenerator generator, String id, ExistingFileHelper existingFileHelper) {
        super(generator, id, existingFileHelper);
        models = new BlockModels(this);
    }

    @Override
    protected void registerStatesAndModels() {
        genNorthCutoutModels(RSBlocks.GRID);
        genNorthCutoutModels(RSBlocks.CRAFTING_GRID);
        genNorthCutoutModels(RSBlocks.PATTERN_GRID);
        genNorthCutoutModels(RSBlocks.FLUID_GRID);
        genNorthCutoutModels(RSBlocks.CRAFTING_MONITOR);
        genNorthCutoutModels(RSBlocks.CRAFTER_MANAGER);
        genNorthCutoutModels(RSBlocks.DISK_MANIPULATOR);
        genControllerModels(RSBlocks.CONTROLLER);
        genControllerModels(RSBlocks.CREATIVE_CONTROLLER);
        genCrafterModels();
        genCubeAllCutoutModels(RSBlocks.RELAY);
        genCubeAllCutoutModels(RSBlocks.NETWORK_TRANSMITTER);
        genCubeAllCutoutModels(RSBlocks.NETWORK_RECEIVER);
        genSecurityManagerModels();
        genDetectorModels();
        genWirelessTransmitterModels();
    }

    private void genWirelessTransmitterModels() {
        for (DyeColor color : DyeColor.values()) {
            Block block = RSBlocks.WIRELESS_TRANSMITTER.get(color).get();
            String folderName = RSBlocks.WIRELESS_TRANSMITTER.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.wirelessTransmitterBlock(block, state -> {
                if (!state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createWirelessTransmitterModel(
                        "block/" + folderName + "/disconnected",
                        resourceLocation(folderName, "cutouts/disconnected")
                    );
                } else {
                    ModelFile model = models.createWirelessTransmitterModel(
                        "block/" + folderName + "/" + color,
                        resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 0);
        }
    }

    private void genDetectorModels() {
        for (DyeColor color : DyeColor.values()) {
            Block block = RSBlocks.DETECTOR.get(color).get();
            String folderName = RSBlocks.DETECTOR.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.simpleBlockStateModel(block, state -> {
                if (!state.get(DetectorBlock.POWERED)) {
                    return models.createDetectorModel(
                        "block/" + folderName + "/off",
                        resourceLocation(folderName, "cutouts/off")
                    );
                } else {
                    ModelFile model = models.createDetectorModel(
                        "block/" + folderName + "/" + color,
                        resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            });
        }
    }

    private void genSecurityManagerModels() {
        for (DyeColor color : DyeColor.values()) {
            Block block = RSBlocks.SECURITY_MANAGER.get(color).get();
            String folderName = RSBlocks.SECURITY_MANAGER.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.horizontalRSBlock(block, state -> {
                if (!state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createCubeCutoutModel(
                        "block/" + folderName + "/disconnected",
                        BOTTOM,
                        BOTTOM,
                        resourceLocation(folderName, "top"),
                        resourceLocation(folderName, "cutouts/top_disconnected"),
                        resourceLocation(folderName, "right"),
                        resourceLocation(folderName, "cutouts/right_disconnected"),
                        resourceLocation(folderName, "left"),
                        resourceLocation(folderName, "cutouts/left_disconnected"),
                        resourceLocation(folderName, "front"),
                        resourceLocation(folderName, "cutouts/front_disconnected"),
                        resourceLocation(folderName, "back"),
                        resourceLocation(folderName, "cutouts/back_disconnected")
                    );
                } else {
                    ModelFile model = models.createCubeCutoutModel(
                        "block/" + folderName + "/" + color,
                        BOTTOM,
                        BOTTOM,
                        resourceLocation(folderName, "top"),
                        resourceLocation(folderName, "cutouts/top" + "_" + color),
                        resourceLocation(folderName, "right"),
                        resourceLocation(folderName, "cutouts/right" + "_" + color),
                        resourceLocation(folderName, "left"),
                        resourceLocation(folderName, "cutouts/left" + "_" + color),
                        resourceLocation(folderName, "front"),
                        resourceLocation(folderName, "cutouts/front" + "_" + color),
                        resourceLocation(folderName, "back"),
                        resourceLocation(folderName, "cutouts/back" + "_" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 180);
        }
    }

    private <T extends Block> void genCubeAllCutoutModels(Map<DyeColor, RegistryObject<T>> blockMap) {
        for (DyeColor color : DyeColor.values()) {
            Block block = blockMap.get(color).get();
            String folderName = blockMap.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.simpleBlockStateModel(block, state -> {
                if (state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createCubeAllCutoutModel(
                        "block/" + folderName + "/" + color,
                        resourceLocation(folderName, folderName),
                        resourceLocation(folderName, folderName),
                        resourceLocation(folderName, "cutouts/" + color)
                    );
                } else {
                    ModelFile model = models.createCubeAllCutoutModel(
                        "block/" + folderName + "/disconnected",
                        resourceLocation(folderName, folderName),
                        resourceLocation(folderName, folderName),
                        resourceLocation(folderName, "cutouts/disconnected")
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            });
        }
    }

    private void genCrafterModels() {
        for (DyeColor color : DyeColor.values()) {
            Block block = RSBlocks.CRAFTER.get(color).get();
            String folderName = RSBlocks.CRAFTER.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.anyDirectionalRSBlock(block, state -> {
                if (!state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createCubeCutoutModel(
                        "block/" + folderName + "/disconnected",
                        BOTTOM,
                        BOTTOM,
                        resourceLocation(folderName, "top"),
                        resourceLocation(folderName, "cutouts/top_disconnected"),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_disconnected"),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_disconnected"),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_disconnected"),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_disconnected")
                    );
                } else {
                    ModelFile model = models.createCubeCutoutModel(
                        "block/" + folderName + "/" + color,
                        BOTTOM,
                        BOTTOM,
                        resourceLocation(folderName, "top"),
                        resourceLocation(folderName, "cutouts/top_" + color),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_" + color),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_" + color),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_" + color),
                        resourceLocation(folderName, "side"),
                        resourceLocation(folderName, "cutouts/side_" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 180);
        }
    }

    private <T extends Block> void genControllerModels(Map<DyeColor, RegistryObject<T>> blockMap) {
        for (DyeColor color : DyeColor.values()) {
            Block block = blockMap.get(color).get();
            String folderName = RSBlocks.CONTROLLER.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.simpleBlockStateModel(block, state -> {
                if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.OFF)) {
                    return models.createCubeAllCutoutModel(
                        "block/" + folderName + "/off",
                        resourceLocation(folderName, "off"),
                        resourceLocation(folderName, "off"),
                        resourceLocation(folderName, "cutouts/off")
                    );
                } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_OFF)) {
                    return models.createControllerNearlyCutoutModel(
                        "block/" + folderName + "/nearly_off",
                        resourceLocation(folderName, "off"),
                        resourceLocation(folderName, "on"),
                        resourceLocation(folderName, "cutouts/nearly_off"),
                        resourceLocation(folderName, "cutouts/nearly_off_gray")
                    );
                } else if (state.get(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_ON)) {
                    return models.createControllerNearlyCutoutModel(
                        "block/" + folderName + "/nearly_on",
                        resourceLocation(folderName, "off"),
                        resourceLocation(folderName, "on"),
                        resourceLocation(folderName, "cutouts/nearly_on"),
                        resourceLocation(folderName, "cutouts/nearly_on_gray")
                    );
                } else {
                    ModelFile model = models.createCubeAllCutoutModel(
                        "block/" + folderName + "/" + color,
                        resourceLocation(folderName, "off"),
                        resourceLocation(folderName, "on"),
                        resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            });
        }
    }

    private <T extends Block> void genNorthCutoutModels(Map<DyeColor, RegistryObject<T>> blockMap) {
        for (DyeColor color : DyeColor.values()) {
            Block block = blockMap.get(color).get();
            String folderName = blockMap.get(BlockUtils.DEFAULT_COLOR).getId().getPath();

            models.horizontalRSBlock(block, state -> {
                if (!state.get(NetworkNodeBlock.CONNECTED)) {
                    return models.createCubeNorthCutoutModel(
                        "block/" + folderName + "/disconnected",
                        BOTTOM,
                        resourceLocation(folderName, "top"),
                        resourceLocation(folderName, "front"),
                        resourceLocation(folderName, "back"),
                        resourceLocation(folderName, "right"),
                        resourceLocation(folderName, "left"),
                        resourceLocation(folderName, "right"),
                        resourceLocation(folderName, "cutouts/disconnected")
                    );
                } else {
                    ModelFile model = models.createCubeNorthCutoutModel(
                        "block/" + folderName + "/" + color,
                        BOTTOM,
                        resourceLocation(folderName, "top"),
                        resourceLocation(folderName, "front"),
                        resourceLocation(folderName, "back"),
                        resourceLocation(folderName, "right"),
                        resourceLocation(folderName, "left"),
                        resourceLocation(folderName, "right"),
                        resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 180);
        }
    }

    private ResourceLocation resourceLocation(String folderName, String name) {
        return new ResourceLocation(RS.ID, "block/" + folderName + "/" + name);
    }
}
