package com.stellarexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

/** A two-way, craftable transport item for the Orbital dimension. */
public final class SpaceBeaconItem extends Item {
    public static final ResourceKey<Level> ORBIT = ResourceKey.create(
        Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(StellarExpansion.MOD_ID, "orbit")
    );

    public SpaceBeaconItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        ServerLevel destination;
        BlockPos arrival;
        if (level.dimension().equals(ORBIT)) {
            destination = serverPlayer.server.getLevel(Level.OVERWORLD);
            if (destination == null) return InteractionResultHolder.fail(stack);
            BlockPos spawn = destination.getSharedSpawnPos();
            arrival = destination.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawn).above();
        } else {
            destination = serverPlayer.server.getLevel(ORBIT);
            if (destination == null) {
                serverPlayer.sendSystemMessage(Component.literal("Orbit dimension not loaded. Check server datapacks."));
                return InteractionResultHolder.fail(stack);
            }
            arrival = new BlockPos(0, 130, 0);
            // The world is intentionally empty. Generate a simple, persistent arrival platform.
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos floor = new BlockPos(x, 128, z);
                    if (destination.isEmptyBlock(floor)) {
                        destination.setBlockAndUpdate(floor, Blocks.SMOOTH_STONE.defaultBlockState());
                    }
                }
            }
        }
        serverPlayer.teleportTo(destination, arrival.getX() + 0.5, arrival.getY(), arrival.getZ() + 0.5,
            serverPlayer.getYRot(), serverPlayer.getXRot());
        serverPlayer.getCooldowns().addCooldown(this, 60);
        return InteractionResultHolder.success(stack);
    }
}
