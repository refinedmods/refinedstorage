package com.refinedmods.refinedstorage.render.model;

import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CubeBuilder {

    public enum UvRotation {
        CLOCKWISE_0,
        CLOCKWISE_90,
        CLOCKWISE_180,
        CLOCKWISE_270
    }

    private static class Uv {
        private float xFrom;
        private float xTo;
        private float yFrom;
        private float yTo;
    }

    public static class Face {
        private Direction face;
        private TextureAtlasSprite sprite;
        private int light;
        private UvRotation uvRotation = UvRotation.CLOCKWISE_0;

        public Face(Direction face, TextureAtlasSprite sprite) {
            this.face = face;
            this.sprite = sprite;
        }

        public Face(Direction face, TextureAtlasSprite sprite, UvRotation uvRotation) {
            this(face, sprite);

            this.uvRotation = uvRotation;
        }

        public Face(Direction face, TextureAtlasSprite sprite, UvRotation uvRotation, int light) {
            this(face, sprite, uvRotation);

            this.light = light;
        }
    }

    private Vector3f from;
    private Vector3f to;
    private VertexFormat format = DefaultVertexFormats.BLOCK; //Changed from Item
    private Map<Direction, Face> faces = new HashMap<>();
    private int color = 0xFFFFFFFF;

    public CubeBuilder from(float x, float y, float z) {
        this.from = new Vector3f(x / 16, y / 16, z / 16);

        return this;
    }

    public CubeBuilder to(float x, float y, float z) {
        this.to = new Vector3f(x / 16, y / 16, z / 16);

        return this;
    }

    public CubeBuilder color(int color) {
        this.color = color;

        return this;
    }

    public CubeBuilder lightmap() {
        this.format = RenderUtils.getFormatWithLightMap(format);

        return this;
    }

    public CubeBuilder addFaces(Function<Direction, Face> faceSupplier) {
        for (Direction facing : Direction.values()) {
            addFace(faceSupplier.apply(facing));
        }

        return this;
    }

    public CubeBuilder addFace(Face face) {
        faces.put(face.face, face);

        return this;
    }

    public List<BakedQuad> bake() {
        List<BakedQuad> quads = new ArrayList<>();

        for (Map.Entry<Direction, Face> entry : faces.entrySet()) {
            quads.add(bakeFace(entry.getKey(), entry.getValue()));
        }

        return quads;
    }

    private BakedQuad bakeFace(Direction facing, Face cubeFace) {
        BakedQuadBuilder builder = new BakedQuadBuilder(cubeFace.sprite);

        builder.setQuadOrientation(facing);
        builder.setQuadTint(-1);
        builder.setApplyDiffuseLighting(true);

        Uv uv = getDefaultUv(facing, cubeFace.sprite, from.x(), from.y(), from.z(), to.x(), to.y(), to.z());

        switch (facing) {
            case DOWN:
                addVertexTopRight(builder, cubeFace, to.x(), from.y(), from.z(), uv);
                addVertexBottomRight(builder, cubeFace, to.x(), from.y(), to.z(), uv);
                addVertexBottomLeft(builder, cubeFace, from.x(), from.y(), to.z(), uv);
                addVertexTopLeft(builder, cubeFace, from.x(), from.y(), from.z(), uv);
                break;
            case UP:
                addVertexTopLeft(builder, cubeFace, from.x(), to.y(), from.z(), uv);
                addVertexBottomLeft(builder, cubeFace, from.x(), to.y(), to.z(), uv);
                addVertexBottomRight(builder, cubeFace, to.x(), to.y(), to.z(), uv);
                addVertexTopRight(builder, cubeFace, to.x(), to.y(), from.z(), uv);
                break;
            case NORTH:
                addVertexBottomRight(builder, cubeFace, to.x(), to.y(), from.z(), uv);
                addVertexTopRight(builder, cubeFace, to.x(), from.y(), from.z(), uv);
                addVertexTopLeft(builder, cubeFace, from.x(), from.y(), from.z(), uv);
                addVertexBottomLeft(builder, cubeFace, from.x(), to.y(), from.z(), uv);
                break;
            case SOUTH:
                addVertexBottomLeft(builder, cubeFace, from.x(), to.y(), to.z(), uv);
                addVertexTopLeft(builder, cubeFace, from.x(), from.y(), to.z(), uv);
                addVertexTopRight(builder, cubeFace, to.x(), from.y(), to.z(), uv);
                addVertexBottomRight(builder, cubeFace, to.x(), to.y(), to.z(), uv);
                break;
            case WEST:
                addVertexTopLeft(builder, cubeFace, from.x(), from.y(), from.z(), uv);
                addVertexTopRight(builder, cubeFace, from.x(), from.y(), to.z(), uv);
                addVertexBottomRight(builder, cubeFace, from.x(), to.y(), to.z(), uv);
                addVertexBottomLeft(builder, cubeFace, from.x(), to.y(), from.z(), uv);
                break;
            case EAST:
                addVertexBottomRight(builder, cubeFace, to.x(), to.y(), from.z(), uv);
                addVertexBottomLeft(builder, cubeFace, to.x(), to.y(), to.z(), uv);
                addVertexTopLeft(builder, cubeFace, to.x(), from.y(), to.z(), uv);
                addVertexTopRight(builder, cubeFace, to.x(), from.y(), from.z(), uv);
                break;
        }

        return builder.build();
    }

    private Uv getDefaultUv(Direction face, TextureAtlasSprite texture, float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        Uv uv = new Uv();

        switch (face) {
            case DOWN:
                uv.xFrom = texture.getU(fromX * 16);
                uv.yFrom = texture.getV(16 - fromZ * 16);
                uv.xTo = texture.getU(toX * 16);
                uv.yTo = texture.getV(16 - toZ * 16);
                break;
            case UP:
                uv.xFrom = texture.getU(fromX * 16);
                uv.yFrom = texture.getV(fromZ * 16);
                uv.xTo = texture.getU(toX * 16);
                uv.yTo = texture.getV(toZ * 16);
                break;
            case NORTH:
                uv.xFrom = texture.getU(16 - fromX * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(16 - toX * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
            case SOUTH:
                uv.xFrom = texture.getU(fromX * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(toX * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
            case WEST:
                uv.xFrom = texture.getU(fromZ * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(toZ * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
            case EAST:
                uv.xFrom = texture.getU(16 - toZ * 16);
                uv.yFrom = texture.getV(16 - fromY * 16);
                uv.xTo = texture.getU(16 - fromZ * 16);
                uv.yTo = texture.getV(16 - toY * 16);
                break;
        }

        return uv;
    }

    private void addVertexTopLeft(BakedQuadBuilder builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
            case CLOCKWISE_90:
                u = uv.xFrom;
                v = uv.yTo;
                break;
            case CLOCKWISE_180:
                u = uv.xTo;
                v = uv.yTo;
                break;
            case CLOCKWISE_270:
                u = uv.xTo;
                v = uv.yFrom;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertexTopRight(BakedQuadBuilder builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xTo;
                v = uv.yFrom;
                break;
            case CLOCKWISE_90:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
            case CLOCKWISE_180:
                u = uv.xFrom;
                v = uv.yTo;
                break;
            case CLOCKWISE_270:
                u = uv.xTo;
                v = uv.yTo;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertexBottomRight(BakedQuadBuilder builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xTo;
                v = uv.yTo;
                break;
            case CLOCKWISE_90:
                u = uv.xTo;
                v = uv.yFrom;
                break;
            case CLOCKWISE_180:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
            case CLOCKWISE_270:
                u = uv.xFrom;
                v = uv.yTo;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertexBottomLeft(BakedQuadBuilder builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.xFrom;
                v = uv.yTo;
                break;
            case CLOCKWISE_90:
                u = uv.xTo;
                v = uv.yTo;
                break;
            case CLOCKWISE_180:
                u = uv.xTo;
                v = uv.yFrom;
                break;
            case CLOCKWISE_270:
                u = uv.xFrom;
                v = uv.yFrom;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertex(BakedQuadBuilder builder, Face face, float x, float y, float z, float u, float v) {
        VertexFormat format = builder.getVertexFormat();

        for (int i = 0; i < format.getElements().size(); i++) {
            VertexFormatElement e = format.getElements().get(i);

            switch (e.getUsage()) {
                case POSITION:
                    builder.put(i, x, y, z);
                    break;
                case NORMAL:
                    builder.put(i, face.face.getStepX(), face.face.getStepY(), face.face.getStepZ());
                    break;
                case COLOR:
                    float r = (color >> 16 & 0xFF) / 255F;
                    float g = (color >> 8 & 0xFF) / 255F;
                    float b = (color & 0xFF) / 255F;
                    float a = (color >> 24 & 0xFF) / 255F;

                    builder.put(i, r, g, b, a);
                    break;
                case UV:
                    if (e.getIndex() == 0) {
                        builder.put(i, u, v);
                    } else {
                        builder.put(i, (float) (face.light * 0x20) / 0xFFFF, (float) (face.light * 0x20) / 0xFFFF);
                    }

                    break;
                default:
                    builder.put(i);
                    break;
            }
        }
    }
    
}
