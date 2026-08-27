package com.feruxiuvenis.adaptive_ruined_portals.utils;

import net.minecraft.core.BlockPos;

@FunctionalInterface
public interface NetherGenerationProvider {

    NetherPortalDestinationHandler.NetherTargetResult getNetherTarget(
            BlockPos overworldPos
    );
}