package com.stellarexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A stationary stellar vista facing the starter station's north observation windows.
 * Built once from vanilla blocks. No entities, shaders, or ticking block entities.
 */
public final class OrbitalSunScene {
    private OrbitalSunScene() {}
    private static final int CX = 0;
    private static final int CY = 137;
    private static final int CZ = -46;
    private static final BlockPos MARKER = new BlockPos(CX, CY, CZ);

    public static void ensureScene(ServerLevel level) {
        if (level.getBlockState(MARKER).is(Blocks.BEACON)) return;

        // Refuse to alter a player-built object inside the sun's construction envelope.
        // Revisit is safe: we only build the scene when its center is empty.
        if (!level.isEmptyBlock(MARKER)) return;
        for (int x = -14; x <= 14; x++) {
            for (int y = -14; y <= 14; y++) {
                for (int z = -10; z <= 10; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    if (dist > 14) continue;
                    if (!level.isEmptyBlock(new BlockPos(CX + x, CY + y, CZ + z))) return;
                }
            }
        }

        final BlockState inner = Blocks.SHROOMLIGHT.defaultBlockState();
        final BlockState core = Blocks.OCHRE_FROGLIGHT.defaultBlockState();
        final BlockState hot = Blocks.GLOWSTONE.defaultBlockState();
        final BlockState plasma = Blocks.ORANGE_STAINED_GLASS.defaultBlockState();
        final BlockState flare = Blocks.YELLOW_STAINED_GLASS.defaultBlockState();
        final BlockState corona = Blocks.RED_STAINED_GLASS.defaultBlockState();

        // Irregular emissive star surface with bright interior, plasma and glowing corona.
        for (int x = -9; x <= 9; x++) {
            for (int y = -9; y <= 9; y++) {
                for (int z = -9; z <= 9; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z);
                    double surface = 8.1 + 0.65 * Math.sin(x * 1.71 + y * 0.47)
                            * Math.cos(y * 1.09 - z * 1.37);
                    if (dist > surface) continue;
                    int noise = Math.floorMod(x * 37 + y * 17 + z * 53, 13);
                    BlockState block;
                    if (dist < surface - 2.2) block = inner;
                    else if (noise < 3) block = hot;
                    else if (noise < 8) block = core;
                    else block = plasma;
                    level.setBlockAndUpdate(new BlockPos(CX + x, CY + y, CZ + z), block);
                }
            }
        }

        // Double, thin, irregular glass halo in a plane visible from the station.
        for (int angle = 0; angle < 360; angle += 3) {
            double rad = Math.toRadians(angle);
            double radius = 11.2 + Math.sin(rad * 7) * 1.1;
            for (int depth = -1; depth <= 1; depth += 2) {
                int x = (int) Math.round(Math.cos(rad) * radius);
                int y = (int) Math.round(Math.sin(rad) * radius);
                BlockPos pos = new BlockPos(CX + x, CY + y, CZ + depth);
                if (level.isEmptyBlock(pos)) {
                    level.setBlockAndUpdate(pos, angle % 9 == 0 ? flare : corona);
                }
            }
        }

        // Sparse solar prominences and sparks, held far away from the actual station.
        for (int i = 0; i < 55; i++) {
            double rad = Math.toRadians(i * 137.507764);
            int radius = 11 + Math.floorMod(i * 11, 4);
            int x = (int) Math.round(Math.cos(rad) * radius);
            int y = (int) Math.round(Math.sin(rad) * radius);
            int z = Math.floorMod(i * 7, 7) - 3;
            BlockPos pos = new BlockPos(CX + x, CY + y, CZ + z);
            if (level.isEmptyBlock(pos)) level.setBlockAndUpdate(pos, i % 3 == 0 ? flare : plasma);
        }

        // Center is the persistence marker and a luminous focus.
        level.setBlockAndUpdate(MARKER, Blocks.BEACON.defaultBlockState());
    }
}
