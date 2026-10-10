package com.stellarexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Sun-facing observation gallery. Adds a glass-enclosed corridor without
 * replacing existing storage or player modifications to the station.
 */
public final class OrbitalObservationDeck {
    private OrbitalObservationDeck() {}
    private static final int Y = 128;
    private static final BlockPos MARKER = new BlockPos(0, Y, -16);

    public static void ensure(ServerLevel level) {
        if (level.getBlockState(MARKER).is(Blocks.LODESTONE)) return;
        // Only retrofit a generated station; never create an exposed deck in empty space.
        if (!level.getBlockState(new BlockPos(-10, Y, -7)).is(Blocks.LODESTONE)) return;

        // Require empty expansion area; leave existing world structures untouched.
        for (int x = -4; x <= 4; x++) {
            for (int z = -16; z <= -8; z++) {
                for (int y = Y; y <= Y + 5; y++) {
                    if (!level.isEmptyBlock(new BlockPos(x, y, z))) return;
                }
            }
        }
        // Open a doorway only when the original generated wall is unchanged.
        for (int x = -1; x <= 1; x++) {
            for (int y = Y + 1; y <= Y + 3; y++) {
                BlockState state = level.getBlockState(new BlockPos(x, y, -7));
                if (!state.is(Blocks.POLISHED_DEEPSLATE) && !state.is(Blocks.TINTED_GLASS) && !state.is(Blocks.LIGHT_BLUE_STAINED_GLASS)) return;
            }
        }

        BlockState glass = Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState();
        BlockState floor = Blocks.SMOOTH_QUARTZ.defaultBlockState();
        BlockState frame = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState light = Blocks.SEA_LANTERN.defaultBlockState();

        for (int x = -4; x <= 4; x++) {
            for (int z = -16; z <= -8; z++) {
                boolean edge = x == -4 || x == 4 || z == -16;
                level.setBlockAndUpdate(new BlockPos(x, Y, z), (x % 4 == 0 && z % 4 == 0) ? light : floor);
                level.setBlockAndUpdate(new BlockPos(x, Y + 5, z), glass);
                for (int y = Y + 1; y <= Y + 4; y++) {
                    if (edge) level.setBlockAndUpdate(new BlockPos(x, y, z), glass);
                }
                if ((x == -4 || x == 4) && (z == -8 || z == -12 || z == -16)) {
                    level.setBlockAndUpdate(new BlockPos(x, Y + 1, z), frame);
                }
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int y = Y + 1; y <= Y + 3; y++) {
                level.removeBlock(new BlockPos(x, y, -7), false);
            }
        }
        level.setBlockAndUpdate(MARKER, Blocks.LODESTONE.defaultBlockState());
    }
}
