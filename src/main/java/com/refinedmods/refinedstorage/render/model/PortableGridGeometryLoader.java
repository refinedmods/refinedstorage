package com.refinedmods.refinedstorage.render.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class PortableGridGeometryLoader implements IGeometryLoader<PortableGridUnbakedGeometry> {
    @Override
    public PortableGridUnbakedGeometry read(final JsonObject jsonObject,
                                            final JsonDeserializationContext deserializationContext) {
        return new PortableGridUnbakedGeometry();
    }
}
