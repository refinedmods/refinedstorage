package com.jaquadro.minecraft.storagedrawers.api;

import com.jaquadro.minecraft.storagedrawers.api.registry.IRenderRegistry;
import com.jaquadro.minecraft.storagedrawers.api.registry.IWailaRegistry;

public interface IStorageDrawersApi {
    IRenderRegistry renderRegistry();

    IWailaRegistry wailaRegistry();
}
