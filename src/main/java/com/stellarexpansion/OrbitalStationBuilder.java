package com.stellarexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * First walkable station prototype, assembled once when the access beacon first reaches Orbit.
 * Vanilla blocks only. No ticking machinery or client rendering required.
 */
public final class OrbitalStationBuilder {
    private OrbitalStationBuilder() {}

    // Center of arrival room at x/z=0, deck at y=128.
    private static final int DECK = 128;
    private static final int HALF_X = 10;
    private static final int HALF_Z = 7;
    private static final BlockPos MARKER = new BlockPos(-10, 128, -7);

    public static boolean ensureStation(ServerLevel level) {
        // Marker is a unique corner block placed last, after construction succeeds.
        if (level.getBlockState(MARKER).is(Blocks.LODESTONE)) return true;

        // Do not overwrite structures players have built around the old 7x7 landing pad.
        // The previous landing pad may occupy x/z [-3,3] on deck y=128.
        for (int x = -HALF_X; x <= HALF_X; x++) {
            for (int z = -HALF_Z; z <= HALF_Z; z++) {
                for (int y = DECK; y <= DECK + 6; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (x >= -3 && x <= 3 && z >= -3 && z <= 3 && y == DECK
                            && level.getBlockState(pos).is(Blocks.SMOOTH_STONE)) continue;
                    if (!level.isEmptyBlock(pos)) return false;
                }
            }
        }

        BlockState hull = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        BlockState deck = Blocks.SMOOTH_QUARTZ.defaultBlockState();
        BlockState window = Blocks.TINTED_GLASS.defaultBlockState();
        BlockState light = Blocks.SEA_LANTERN.defaultBlockState();
        BlockState trim = Blocks.IRON_BLOCK.defaultBlockState();

        // Three-room enclosed hull, with broad panoramic window bands.
        for (int x = -HALF_X; x <= HALF_X; x++) {
            for (int z = -HALF_Z; z <= HALF_Z; z++) {
                level.setBlockAndUpdate(new BlockPos(x, DECK, z), deck);
                level.setBlockAndUpdate(new BlockPos(x, DECK + 6, z), hull);
                for (int y = DECK + 1; y <= DECK + 5; y++) {
                    boolean edge = Math.abs(x) == HALF_X || Math.abs(z) == HALF_Z;
                    if (!edge) continue;
                    boolean panoramic = y >= DECK + 2 && y <= DECK + 4
                            && Math.abs(x) < HALF_X - 2 && Math.abs(z) < HALF_Z - 2;
                    level.setBlockAndUpdate(new BlockPos(x, y, z), panoramic ? window : hull);
                }
            }
        }

        // Frame + ceiling lighting. Interior corridor stays open for walking.
        for (int x = -8; x <= 8; x += 4) {
            level.setBlockAndUpdate(new BlockPos(x, DECK + 6, 0), light);
        }
        for (int z = -5; z <= 5; z += 5) {
            level.setBlockAndUpdate(new BlockPos(0, DECK + 6, z), light);
        }

        // Docking approach and cargo staging squares, with no dangerous teleport traps.
        for (int x = -8; x <= -5; x++) {
            for (int z = -5; z <= 5; z++) {
                level.setBlockAndUpdate(new BlockPos(x, DECK, z), trim);
            }
        }
        for (int x = 5; x <= 8; x++) {
            for (int z = -5; z <= 5; z++) {
                level.setBlockAndUpdate(new BlockPos(x, DECK, z), trim);
            }
        }

        // Workbenches and storage give players something useful in the first station.
        level.setBlockAndUpdate(new BlockPos(-7, DECK + 1, -4), Blocks.CRAFTING_TABLE.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(7, DECK + 1, -4), Blocks.CHEST.defaultBlockState());
        level.setBlockAndUpdate(new BlockPos(7, DECK + 1, 4), Blocks.FURNACE.defaultBlockState());
        level.setBlockAndUpdate(MARKER, Blocks.LODESTONE.defaultBlockState());
        return true;
    }
}
