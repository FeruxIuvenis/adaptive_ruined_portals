package com.feruxiuvenis.adaptive_ruined_portals.mixin;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColor;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColors;
import com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers.PortalColorApplier;
import com.feruxiuvenis.adaptive_ruined_portals.worldgen.PortalSurroundingProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.portal.PortalShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PortalShape.class)
public abstract class PortalShapeMixin {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    @Shadow private BlockPos bottomLeft;
    @Shadow private int height;
    @Shadow @Final private int width;
    @Shadow @Final private Direction rightDir;

    @Inject(method = "createPortalBlocks", at = @At("TAIL"))
    private void adaptiveRuinedPortals$onCreatePortalBlocks(
            LevelAccessor level,
            CallbackInfo ci
    ) {
        if (bottomLeft == null) return;

        if (!(level instanceof ServerLevel serverLevel) || serverLevel.dimension() != Level.OVERWORLD) {
            return;
        }

        if (PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER == null) {
            LOGGER.warn("[COLOR] NetherGenerationProvider not initialized — skipping portal recolor.");
            return;
        }

        NetherPortalDestinationHandler.NetherTargetResult target;
        try {
            target = PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER.getNetherTarget(bottomLeft);
        } catch (Exception e) {
            LOGGER.warn("[COLOR] Failed to resolve Nether target for portal at {}: {}",
                    bottomLeft.toShortString(), e.toString());
            return;
        }

        var biomeKey = target.getBiomeKey().orElse(null);
        if (biomeKey == null) return;

        PortalBiomeColor color = PortalBiomeColors.getColor(biomeKey);
        if (color == PortalBiomeColor.NONE) return;

        BlockPos corner = bottomLeft.relative(Direction.UP, height - 1).relative(rightDir, width - 1);
        BoundingBox box = BoundingBox.fromCorners(bottomLeft, corner);

        PortalColorApplier.applyPortalColor(serverLevel, box, target);

        LOGGER.info("[COLOR] Portal at {} recolored via createPortalBlocks -> {} for destination biome {}",
                bottomLeft.toShortString(), color, target.getBiomePath());
    }
}