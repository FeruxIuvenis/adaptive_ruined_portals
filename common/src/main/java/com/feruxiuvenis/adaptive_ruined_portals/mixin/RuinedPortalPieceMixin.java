package com.feruxiuvenis.adaptive_ruined_portals.mixin;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.utils.theme.PortalBlockThemes;
import com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers.PortalChestLootApplier;
import com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers.PortalColorApplier;
import com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers.PortalThemeApplier;
import com.feruxiuvenis.adaptive_ruined_portals.worldgen.PortalSurroundingProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
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
            PortalThemeApplier.applyTheme(
                    level,
                    self.getBoundingBox(),
                    theme,
                    random
            );
        } else {
            LOGGER.info("[MIXIN] No theme registered for biome {} — skipping retheme.", target.getBiomePath());
        }

        PortalChestLootApplier.applyChestLoot(
                level,
                self.getBoundingBox(),
                target,
                random
        );

        PortalColorApplier.applyPortalColor(
                level,
                self.getBoundingBox(),
                target
        );
    }
}