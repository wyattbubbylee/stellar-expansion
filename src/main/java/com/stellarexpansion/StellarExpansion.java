package com.stellarexpansion;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

@Mod(StellarExpansion.MOD_ID)
public class StellarExpansion {
    public static final String MOD_ID = "stellar_expansion";

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredBlock<Block> MINI_SUN = BLOCKS.register("mini_sun", () ->
        new MiniSunBlock(BlockBehaviour.Properties.of()
            .strength(3.0f, 6.0f)
            .lightLevel(state -> 15)
            .sound(SoundType.AMETHYST)
            .noOcclusion()));

    public static final DeferredItem<BlockItem> MINI_SUN_ITEM = ITEMS.register("mini_sun", () ->
        new BlockItem(MINI_SUN.get(), new Item.Properties()));

    public StellarExpansion(IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }
}
