package com.stellarexpansion.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stellarexpansion.SpaceBeaconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/**
 * First shader-free sky-rendered giant for Orbit. All geometry is client-side,
 * camera-relative sky geometry: no physical star blocks and no particle swarm.
 *
 * The color ramps approximate bloom without requiring a post-processing shader.
 * This renderer is deliberately low-cost for RX 580-class hardware.
 */
@EventBusSubscriber(modid = "stellar_expansion", value = Dist.CLIENT)
public final class OrbitSceneRenderer {
    private static final int SEGMENTS = 96;
    private static final float DEPTH = -90f;
    private static final float HEIGHT = 12f;

    private OrbitSceneRenderer() {}

    @SubscribeEvent
    public static void renderSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !mc.level.dimension().equals(SpaceBeaconItem.ORBIT)) return;

        // Very low graphics settings simplify the outer corona.
        int quality = mc.options.particles().get().getId();
        float time = mc.level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Matrix4f matrix = event.getModelViewMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        try {
            // A wide translucent envelope, followed by denser luminous rings.
            float pulse = 1f + (float) Math.sin(time * 0.012f) * 0.025f;
            drawDisc(matrix, 73f * pulse, 0x00AB1004, 0x24B31B08, 0f);
            drawDisc(matrix, 56f * pulse, 0x00E52508, 0x42E83409, 0f);
            drawDisc(matrix, 43f * pulse, 0x04FF5A0D, 0x95ED3D09, 0f);
            drawDisc(matrix, 35f * pulse, 0xA8FF6B12, 0xECFF9B20, 0f);

            if (quality != 2) {
                // Two subtly drifting corona sheets produce slow plasma movement.
                drawCorona(matrix, 42f * pulse, 5f, time * 0.005f, 0x68FF4A0C);
                if (quality == 0) {
                    drawCorona(matrix, 46f * pulse, 7f, -time * 0.0032f, 0x44FF2208);
                }
            }

            drawDisc(matrix, 28f * pulse, 0xEEFF9B22, 0xFFFFC95F, 0f);
            drawDisc(matrix, 17f * pulse, 0xFFFFCB66, 0xFFFFEAB1, 0f);
            drawDisc(matrix, 8.5f * pulse, 0xFFFFE7A0, 0xFFFFFFFF, 0f);
        } finally {
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
    }

    /** Radial vertex-color gradient, avoiding textures and particle-distance culling. */
    private static void drawDisc(Matrix4f matrix, float radius, int edge, int center, float phase) {
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < SEGMENTS; i++) {
            float a = (float) (Math.PI * 2 * i / SEGMENTS) + phase;
            float b = (float) (Math.PI * 2 * (i + 1) / SEGMENTS) + phase;
            buffer.addVertex(matrix, 0f, HEIGHT, DEPTH).setColor(center);
            buffer.addVertex(matrix, (float) Math.cos(a) * radius,
                    HEIGHT + (float) Math.sin(a) * radius, DEPTH).setColor(edge);
            buffer.addVertex(matrix, (float) Math.cos(b) * radius,
                    HEIGHT + (float) Math.sin(b) * radius, DEPTH).setColor(edge);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void drawCorona(Matrix4f matrix, float radius, float amplitude,
                                   float phase, int color) {
        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < SEGMENTS; i++) {
            float a = (float) (Math.PI * 2 * i / SEGMENTS);
            float b = (float) (Math.PI * 2 * (i + 1) / SEGMENTS);
            float ra = radius + amplitude * (float) Math.sin(8 * a + phase);
            float rb = radius + amplitude * (float) Math.sin(8 * b + phase);
            vertexPolar(buffer, matrix, a, radius - 8, color);
            vertexPolar(buffer, matrix, a, ra, 0x00FF340A);
            vertexPolar(buffer, matrix, b, rb, 0x00FF340A);
            vertexPolar(buffer, matrix, a, radius - 8, color);
            vertexPolar(buffer, matrix, b, rb, 0x00FF340A);
            vertexPolar(buffer, matrix, b, radius - 8, color);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    private static void vertexPolar(BufferBuilder buffer, Matrix4f matrix,
                                    float angle, float radius, int color) {
        buffer.addVertex(matrix, (float) Math.cos(angle) * radius,
                HEIGHT + (float) Math.sin(angle) * radius, DEPTH).setColor(color);
    }
}
