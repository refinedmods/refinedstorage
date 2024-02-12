package com.refinedmods.refinedstorage.integration.curios;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

public class CuriosIntegration {
    private static final String ID = "curios";

    public CuriosIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ID);
    }

    @SubscribeEvent
    public void registerSlots(InterModEnqueueEvent event) {
        InterModComms.sendTo(ID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo(ID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
    }
}
