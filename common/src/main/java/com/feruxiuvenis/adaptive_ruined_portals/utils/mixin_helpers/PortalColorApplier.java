package com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColor;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PortalColorApplier {
    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    private PortalColorApplier() {
    }

    public static void applyPortalColor(
            WorldGenLevel level,
            BoundingBox pieceBox,
            NetherPortalDestinationHandler.NetherTargetResult target
    ) {
        var biomeKey = target.getBiomeKey().orElse(null);
        if (biomeKey == null) return;

        PortalBiomeColor color = PortalBiomeColors.getColor(biomeKey);
        if (color == PortalBiomeColor.NONE) return;

        BlockPos.betweenClosedStream(pieceBox).forEach(blockPos -> {
            BlockState state = level.getBlockState(blockPos);
            if (!state.is(Blocks.NETHER_PORTAL)) return;

            level.setBlock(blockPos, state.setValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR, color), 2);
        });

        LOGGER.info("[COLOR] Portal blocks recolored -> {} for biome {}", color, target.getBiomePath());
    }
}