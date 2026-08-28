package com.feruxiuvenis.adaptive_ruined_portals.utils.mixin_helpers;

import com.feruxiuvenis.adaptive_ruined_portals.utils.theme.PortalBlockTheme;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class PortalThemeApplier {

    private static final int SPREAD_HORIZONTAL_RADIUS = 14;
    private static final int SPREAD_VERTICAL_PADDING = 15;

    private PortalThemeApplier() {
    }

    public static void applyTheme(
            WorldGenLevel level,
            BoundingBox pieceBox,
            PortalBlockTheme theme,
            RandomSource random
    ) {
        BoundingBox region = expandedSpreadRegion(pieceBox);

        // Replace surrounding blocks
        BlockPos.betweenClosedStream(region).forEach(blockPos -> {
            BlockState current = level.getBlockState(blockPos);
            BlockState themed = theme.resolveReplacement(current, random);

            if (!themed.equals(current)) {
                level.setBlock(blockPos, themed, 2);
            }
        });

        // Add surface decorations
        BlockPos.betweenClosedStream(region).forEach(blockPos -> {
            BlockState surfaceState = level.getBlockState(blockPos);
            BlockPos abovePos = blockPos.above();

            if (!level.getBlockState(abovePos).isAir()) {
                return;
            }

            for (PortalBlockTheme.SurfaceDecoration decoration : theme.decorations()) {
                if (decoration.qualifies(surfaceState)
                        && random.nextFloat() < decoration.probability()) {

                    level.setBlock(
                            abovePos,
                            decoration.pickRandom(random),
                            2
                    );

                    break;
                }
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
}