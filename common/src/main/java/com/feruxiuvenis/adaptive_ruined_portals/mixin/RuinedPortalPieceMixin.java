package com.feruxiuvenis.adaptive_ruined_portals.mixin;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.utils.PortalBlockTheme;
import com.feruxiuvenis.adaptive_ruined_portals.utils.PortalBlockThemes;
import com.feruxiuvenis.adaptive_ruined_portals.utils.PortalChestLootTables;
import com.feruxiuvenis.adaptive_ruined_portals.worldgen.PortalSurroundingProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RuinedPortalPiece.class)
public abstract class RuinedPortalPieceMixin {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    private static final int SPREAD_HORIZONTAL_RADIUS = 14;
    private static final int SPREAD_VERTICAL_PADDING = 15;

    @Inject(method = "postProcess", at = @At("TAIL"))
    private void adaptiveRuinedPortals$onPostProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox box,
            ChunkPos chunkPos,
            BlockPos pos,
            CallbackInfo ci
    ) {
        RuinedPortalPiece self = (RuinedPortalPiece) (Object) this;

        BlockPos overworldPos = self.getBoundingBox().getCenter();

        LOGGER.info("[MIXIN] RuinedPortalPiece.postProcess triggered at {}", overworldPos.toShortString());

        if (PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER == null) {
            LOGGER.warn("[MIXIN] NetherGenerationProvider has not been initialized yet.");
            return;
        }

        NetherPortalDestinationHandler.NetherTargetResult target;
        try {
            target = PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER
                    .getNetherTarget(overworldPos);
        } catch (Exception e) {
            LOGGER.warn("[MIXIN] Failed to compute Nether target for {}: {}",
                    overworldPos.toShortString(), e.toString());
            return;
        }

        LOGGER.info("[MIXIN] Target biome resolved: {}", target.getBiomePath());

        var theme = target.getBiomeKey().map(PortalBlockThemes::getTheme).orElse(null);

        if (theme != null) {
            LOGGER.info("[MIXIN] Applying theme for biome {}", target.getBiomePath());
            applyTheme(level, self.getBoundingBox(), theme, random);
        } else {
            LOGGER.info("[MIXIN] No theme registered for biome {} — skipping retheme.", target.getBiomePath());
        }

        // --- chest loot override, using the same proven injection point ---
        applyChestLoot(level, self.getBoundingBox(), target, random);
    }

    /**
     * Scans the portal piece's own bounding box (not the expanded theme
     * region) for the chest that vanilla has already placed by this
     * point, and overwrites its loot table if a mapping exists for the
     * resolved destination biome.
     */
    private static void applyChestLoot(
            WorldGenLevel level,
            BoundingBox pieceBox,
            NetherPortalDestinationHandler.NetherTargetResult target,
            RandomSource random
    ) {
        var biomeKey = target.getBiomeKey().orElse(null);

        if (biomeKey == null) {
            LOGGER.info("[LOOT] Destination biome has no resource key — skipping loot override.");
            return;
        }

        var newLootTable = PortalChestLootTables.getLootTable(biomeKey);

        if (newLootTable == null) {
            LOGGER.info("[LOOT] No custom loot table registered for biome {} — leaving vanilla loot.", target.getBiomePath());
            return;
        }

        BlockPos.betweenClosedStream(pieceBox).forEach(blockPos -> {
            BlockState state = level.getBlockState(blockPos);

            if (!(state.getBlock() instanceof ChestBlock)) {
                return;
            }

            LOGGER.info("[LOOT] Chest found at {} (immutable copy, must re-fetch)", blockPos.toShortString());

            if (level.getBlockEntity(blockPos) instanceof RandomizableContainerBlockEntity chest) {
                chest.setLootTable(
                        ResourceKey.create(Registries.LOOT_TABLE, newLootTable),
                        random.nextLong()
                );
                LOGGER.info("[LOOT] Overrode chest at {} -> destination biome {} -> loot table {}",
                        blockPos.toShortString(), target.getBiomePath(), newLootTable);
            } else {
                LOGGER.warn("[LOOT] Block at {} looked like a chest but had no RandomizableContainerBlockEntity.",
                        blockPos.toShortString());
            }
        });
    }

    private static BoundingBox expandedSpreadRegion(BoundingBox pieceBox) {
        BlockPos center = pieceBox.getCenter();

        return new BoundingBox(
                center.getX() - SPREAD_HORIZONTAL_RADIUS,
                pieceBox.minY() - SPREAD_VERTICAL_PADDING,
                center.getZ() - SPREAD_HORIZONTAL_RADIUS,
                center.getX() + SPREAD_HORIZONTAL_RADIUS,
                pieceBox.maxY(),
                center.getZ() + SPREAD_HORIZONTAL_RADIUS
        );
    }

    private static void applyTheme(
            WorldGenLevel level,
            BoundingBox pieceBox,
            PortalBlockTheme theme,
            RandomSource random
    ) {
        BoundingBox region = expandedSpreadRegion(pieceBox);

        BlockPos.betweenClosedStream(region).forEach(blockPos -> {
            BlockState current = level.getBlockState(blockPos);
            BlockState themed = theme.resolveReplacement(current, random);

            if (!themed.equals(current)) {
                level.setBlock(blockPos, themed, 2);
            }
        });

        BlockPos.betweenClosedStream(region).forEach(blockPos -> {
            BlockState surfaceState = level.getBlockState(blockPos);
            BlockPos abovePos = blockPos.above();

            if (!level.getBlockState(abovePos).isAir()) {
                return;
            }

            for (PortalBlockTheme.SurfaceDecoration decoration : theme.decorations()) {
                if (decoration.qualifies(surfaceState) && random.nextFloat() < decoration.probability()) {
                    level.setBlock(abovePos, decoration.pickRandom(random), 2);
                    break;
                }
            }
        });
    }
}