package com.stellarexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Sparse, reproducible orbital scenery. No per-tick effects or entities. */
public final class OrbitalOrbitDecor {
    private OrbitalOrbitDecor() {}
    // Only one marker per generation area; on an existing world never replace builds.
    private static final BlockPos MARKER = new BlockPos(2, 124, 2);

    public static void ensure(ServerLevel level) {
        if (level.getBlockState(MARKER).is(Blocks.CRYING_OBSIDIAN)) return;
        if (!level.isEmptyBlock(MARKER)) return;

        // Distant pinprick stars: tiny light-emitting points scattered beyond the station.
        // Fixed deterministic positions; no random ticking particles or renderers.
        BlockState[] stars = {
            Blocks.SEA_LANTERN.defaultBlockState(),
            Blocks.END_ROD.defaultBlockState(),
            Blocks.OCHRE_FROGLIGHT.defaultBlockState()
        };
        for (int i = 0; i < 42; i++) {
            int x = Math.floorMod(i * 97 + 31, 109) - 54;
            int y = 115 + Math.floorMod(i * 53 + 13, 42);
            int z = Math.floorMod(i * 73 + 17, 121) - 60;
            // Keep clear of station, northern sun, and observation deck.
            if (Math.abs(x) < 17 && z > -19 && z < 18) continue;
            if (Math.abs(x) < 18 && z < -29 && z > -62) continue;
            BlockPos pos = new BlockPos(x, y, z);
            if (level.isEmptyBlock(pos)) level.setBlockAndUpdate(pos, stars[i % stars.length]);
        }

        // Small asteroid debris patches, separated from all walkable structures.
        for (int asteroid = 0; asteroid < 7; asteroid++) {
            int ax = (asteroid % 2 == 0 ? 1 : -1) * (30 + asteroid * 4);
            int ay = 124 + Math.floorMod(asteroid * 11, 23);
            int az = -15 + asteroid * 9;
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        if (dx * dx + dy * dy + dz * dz > 4 + asteroid % 3) continue;
                        BlockPos pos = new BlockPos(ax + dx, ay + dy, az + dz);
                        if (level.isEmptyBlock(pos)) {
                            level.setBlockAndUpdate(pos, (asteroid + dx + dz) % 4 == 0
                                ? Blocks.ANDESITE.defaultBlockState() : Blocks.TUFF.defaultBlockState());
                        }
                    }
                }
            }
        }
        level.setBlockAndUpdate(MARKER, Blocks.CRYING_OBSIDIAN.defaultBlockState());
    }
}
