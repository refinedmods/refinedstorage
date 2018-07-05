package com.raoulvdberge.refinedstorage.render;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CubeBuilder {
    private class CubeFace {
        private TextureAtlasSprite sprite;
        private float xFrom;
        private float xTo;
        private float yFrom;
        private float yTo;

        CubeFace(TextureAtlasSprite sprite, float xFrom, float xTo, float yFrom, float yTo) {
            this.sprite = sprite;
            this.xFrom = xFrom;
            this.xTo = xTo;
            this.yFrom = yFrom;
            this.yTo = yTo;
        }
    }

    private static final FaceBakery BAKERY = new FaceBakery();

    private Vector3f from;
    private Vector3f to;
    private Map<EnumFacing, CubeFace> faces = new HashMap<>();
    private ModelRotation rotation = ModelRotation.X0_Y0;
    private boolean uvLocked = true;

    public CubeBuilder from(float x, float y, float z) {
        this.from = new Vector3f(x, y, z);

        return this;
    }

    public CubeBuilder to(float x, float y, float z) {
        this.to = new Vector3f(x, y, z);

        return this;
    }

    public CubeBuilder face(EnumFacing face, float xFrom, float xTo, float yFrom, float yTo, TextureAtlasSprite sprite) {
        faces.put(face, new CubeFace(
            sprite,
            xFrom,
            xTo,
            yFrom,
            yTo
        ));

        return this;
    }

    public CubeBuilder allFaces(float xFrom, float xTo, float yFrom, float yTo, TextureAtlasSprite sprite) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            face(facing, xFrom, xTo, yFrom, yTo, sprite);
        }

        return this;
    }

    public CubeBuilder rotate(ModelRotation rotation) {
        this.rotation = rotation;

        return this;
    }

    public List<BakedQuad> bake() {
        List<BakedQuad> quads = new ArrayList<>();

        for (Map.Entry<EnumFacing, CubeFace> entry : faces.entrySet()) {
            EnumFacing face = entry.getKey();

            CubeFace faceData = entry.getValue();

            BlockFaceUV uv = new BlockFaceUV(new float[]{
                faceData.xFrom, faceData.yFrom, faceData.xTo, faceData.yTo
            }, 0);

            BlockPartFace part = new BlockPartFace(face, -1, null, uv);

            quads.add(BAKERY.makeBakedQuad(
                from,
                to,
                part,
                faceData.sprite,
                face,
                rotation,
                null,
                uvLocked,
                true
            ));
        }

        return quads;
    }

    public CubeBuilder setUvLocked(boolean uvLocked) {
        this.uvLocked = uvLocked;

        return this;
    }
}
