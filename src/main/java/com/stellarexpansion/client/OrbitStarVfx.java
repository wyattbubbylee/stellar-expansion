package com.stellarexpansion.client;

import com.stellarexpansion.SpaceBeaconItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3f;

import java.util.Random;

/**
 * Client-only, world-block-free red giant VFX prototype.
 * The star is a pulsing cloud of red / orange / gold emissive-looking particles.
 * No block placing, chunks, or per-tick work on the server.
 */
@EventBusSubscriber(modid = "stellar_expansion", value = Dist.CLIENT)
public final class OrbitStarVfx {
    private static final Random RANDOM = new Random();
    private static final double X = 0;
    private static final double Y = 137;
    private static final double Z = -46;
    private static final Vector3f RED = new Vector3f(1f, 0.08f, 0.025f);
    private static final Vector3f ORANGE = new Vector3f(1f, 0.29f, 0.03f);
    private static final Vector3f GOLD = new Vector3f(1f, 0.66f, 0.15f);
    private static long tick;

    private OrbitStarVfx() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null || mc.isPaused()
                || !level.dimension().equals(SpaceBeaconItem.ORBIT)) return;
        if (mc.player.distanceToSqr(X, Y, Z) > 125 * 125) return;

        tick++;
        // Density is capped for lower end devices. The visual is intentionally
        // entirely cosmetic; turning particles off never impacts gameplay.
        int count = mc.options.particles().get().getId() == 2 ? 5 : 17;
        for (int i = 0; i < count; i++) {
            double theta = RANDOM.nextDouble() * Math.PI * 2;
            double u = RANDOM.nextDouble() * 2 - 1;
            double planar = Math.sqrt(1 - u * u);
            double radius = 6.8 + RANDOM.nextDouble() * 1.8
                    + Math.sin(theta * 7 + tick * 0.06) * 0.5;
            double px = X + radius * planar * Math.cos(theta);
            double py = Y + radius * u;
            double pz = Z + radius * planar * Math.sin(theta);
            int color = RANDOM.nextInt(9);
            Vector3f tint = color < 3 ? RED : color < 7 ? ORANGE : GOLD;
            level.addParticle(new DustParticleOptions(tint, 1.4f + RANDOM.nextFloat() * 2f),
                    true, px, py, pz, 0, 0.004, 0);
        }

        // Short-lived solar prominences streaming from the outer edge.
        for (int i = 0; i < 2; i++) {
            double theta = RANDOM.nextDouble() * Math.PI * 2;
            double radius = 8 + RANDOM.nextDouble() * 3;
            double px = X + Math.cos(theta) * radius;
            double py = Y + Math.sin(theta) * radius;
            level.addParticle(ParticleTypes.FLAME, true, px, py, Z + RANDOM.nextDouble() * 3 - 1.5,
                    Math.cos(theta) * 0.02, Math.sin(theta) * 0.02, 0);
        }
    }
}
