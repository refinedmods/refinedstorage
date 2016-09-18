package refinedstorage.gui.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import refinedstorage.RefinedStorage;

public class ModGuiConfig extends GuiConfig {

    public ModGuiConfig(GuiScreen guiScreen) {
        super(guiScreen,
                RefinedStorage.INSTANCE.config.getConfigElements(),
                RefinedStorage.ID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(RefinedStorage.INSTANCE.config.getConfig().toString()));
    }
}
