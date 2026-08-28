package com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.utils.theme.PortalChestLootTables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PortalChestLootApplier {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    private PortalChestLootApplier() {
    }

    public static void applyChestLoot(
            WorldGenLevel level,
            BoundingBox pieceBox,
            NetherPortalDestinationHandler.NetherTargetResult target,
            RandomSource random
    ) {
        var biomeKey = target.getBiomeKey().orElse(null);

        if (biomeKey == null) {
            LOGGER.info(
                    "[LOOT] Destination biome has no resource key — skipping loot override."
            );
            return;
        }

        var newLootTable = PortalChestLootTables.getLootTable(biomeKey);

        if (newLootTable == null) {
            LOGGER.info(
                    "[LOOT] No custom loot table registered for biome {} — leaving vanilla loot.",
                    target.getBiomePath()
            );
            return;
        }

        BlockPos.betweenClosedStream(pieceBox).forEach(blockPos -> {
            BlockState state = level.getBlockState(blockPos);

            if (!(state.getBlock() instanceof ChestBlock)) {
                return;
            }

            LOGGER.info(
                    "[LOOT] Chest found at {} (immutable copy, must re-fetch)",
                    blockPos.toShortString()
            );

            if (level.getBlockEntity(blockPos)
                    instanceof RandomizableContainerBlockEntity chest) {

                chest.setLootTable(
                        ResourceKey.create(
                                Registries.LOOT_TABLE,
                                newLootTable
                        ),
                        random.nextLong()
                );

                LOGGER.info(
                        "[LOOT] Overrode chest at {} -> destination biome {} -> loot table {}",
                        blockPos.toShortString(),
                        target.getBiomePath(),
                        newLootTable
                );

            } else {
                LOGGER.warn(
                        "[LOOT] Block at {} looked like a chest but had no RandomizableContainerBlockEntity.",
                        blockPos.toShortString()
                );
            }
        });
    }
}