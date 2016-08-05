package refinedstorage.tile.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import refinedstorage.gui.GuiStorage;
import refinedstorage.tile.data.ITileDataConsumer;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.TileDataManager;
import refinedstorage.tile.data.TileDataParameter;

public interface IPrioritizable {
    static <T extends TileEntity> TileDataParameter<Integer> createParameter() {
        return TileDataManager.createParameter(DataSerializers.VARINT, new ITileDataProducer<Integer, T>() {
            @Override
            public Integer getValue(T tile) {
                return ((IPrioritizable) tile).getPriority();
            }
        }, new ITileDataConsumer<Integer, T>() {
            @Override
            public void setValue(T tile, Integer value) {
                ((IPrioritizable) tile).setPriority(value);
            }
        }, parameter -> {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                GuiScreen gui = Minecraft.getMinecraft().currentScreen;

                if (gui instanceof GuiStorage) {
                    ((GuiStorage) gui).PRIORITY.setText(String.valueOf(parameter.getValue()));
                }
            }
        });
    }

    int getPriority();

    void setPriority(int priority);
}
