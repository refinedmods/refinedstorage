package com.refinedmods.refinedstorage.render.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CubeBuilder {
    private Vector3f from;
    private Vector3f to;
    private final Map<Direction, Face> faces = new HashMap<>();
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
        List<BakedQuad> quad = new ArrayList<>();
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer(quad::add);

        builder.setSprite(cubeFace.sprite);
        builder.setDirection(facing);
        builder.setTintIndex(-1);
        builder.setShade(true);

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

        return quad.get(0);
    }

    private Uv getDefaultUv(Direction face, TextureAtlasSprite texture, float x1, float y1, float z1, float x2, float y2, float z2) {
        Uv uv = new Uv();

        if (face.getAxis() != Direction.Axis.Y) {
            uv.v1 = texture.getV(1 - y1);
            uv.v2 = texture.getV(1 - y2);
        } else {
            uv.v1 = texture.getV(z1);
            uv.v2 = texture.getV(z2);
        }

        switch (face) {
            case DOWN, UP, SOUTH -> {
                uv.u1 = texture.getU(x1);
                uv.u2 = texture.getU(x2);
            }
            case NORTH -> {
                uv.u1 = texture.getU(1 - x2);
                uv.u2 = texture.getU(1 - x1);
            }
            case WEST -> {
                uv.u1 = texture.getU(z1);
                uv.u2 = texture.getU(z2);
            }
            case EAST -> {
                uv.u1 = texture.getU(1 - z2);
                uv.u2 = texture.getU(1 - z1);
            }
        }

        return uv;
    }

    private void addVertexTopLeft(QuadBakingVertexConsumer builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.u1;
                v = uv.v1;
                break;
            case CLOCKWISE_90:
                u = uv.u1;
                v = uv.v2;
                break;
            case CLOCKWISE_180:
                u = uv.u2;
                v = uv.v2;
                break;
            case CLOCKWISE_270:
                u = uv.u2;
                v = uv.v1;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertexTopRight(QuadBakingVertexConsumer builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.u2;
                v = uv.v1;
                break;
            case CLOCKWISE_90:
                u = uv.u1;
                v = uv.v1;
                break;
            case CLOCKWISE_180:
                u = uv.u1;
                v = uv.v2;
                break;
            case CLOCKWISE_270:
                u = uv.u2;
                v = uv.v2;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertexBottomRight(QuadBakingVertexConsumer builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.u2;
                v = uv.v2;
                break;
            case CLOCKWISE_90:
                u = uv.u2;
                v = uv.v1;
                break;
            case CLOCKWISE_180:
                u = uv.u1;
                v = uv.v1;
                break;
            case CLOCKWISE_270:
                u = uv.u1;
                v = uv.v2;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertexBottomLeft(QuadBakingVertexConsumer builder, Face face, float x, float y, float z, Uv uv) {
        float u;
        float v;

        switch (face.uvRotation) {
            default:
            case CLOCKWISE_0:
                u = uv.u1;
                v = uv.v2;
                break;
            case CLOCKWISE_90:
                u = uv.u2;
                v = uv.v2;
                break;
            case CLOCKWISE_180:
                u = uv.u2;
                v = uv.v1;
                break;
            case CLOCKWISE_270:
                u = uv.u1;
                v = uv.v1;
                break;
        }

        addVertex(builder, face, x, y, z, u, v);
    }

    private void addVertex(QuadBakingVertexConsumer builder, Face face, float x, float y, float z, float u, float v) {
        builder.vertex(x, y, z);
        builder.normal(face.face.getStepX(), face.face.getStepY(), face.face.getStepZ());
        float r = (color >> 16 & 0xFF) / 255F;
        float g = (color >> 8 & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;
        float a = (color >> 24 & 0xFF) / 255F;
        builder.uv(u, v);
        builder.color(r, g, b, a);
        builder.endVertex();
    }

    public enum UvRotation {
        CLOCKWISE_0,
        CLOCKWISE_90,
        CLOCKWISE_180,
        CLOCKWISE_270
    }

    private static class Uv {
        private float u1;
        private float u2;
        private float v1;
        private float v2;
    }

    public static class Face {
        private final Direction face;
        private final TextureAtlasSprite sprite;
        private final UvRotation uvRotation = UvRotation.CLOCKWISE_0;

        public Face(Direction face, TextureAtlasSprite sprite) {
            this.face = face;
            this.sprite = sprite;
        }
    }
}
