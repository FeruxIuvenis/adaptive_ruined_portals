package com.feruxiuvenis.adaptive_ruined_portals.utils.color;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Map;

public final class PortalBiomeColors {

    private PortalBiomeColors() {
    }

    private static final Map<ResourceKey<Biome>, PortalBiomeColor> COLORS = Map.of(
            Biomes.NETHER_WASTES, PortalBiomeColor.NETHER_WASTES,
            Biomes.CRIMSON_FOREST, PortalBiomeColor.CRIMSON_FOREST,
            Biomes.WARPED_FOREST, PortalBiomeColor.WARPED_FOREST,
            Biomes.SOUL_SAND_VALLEY, PortalBiomeColor.SOUL_SAND_VALLEY,
            Biomes.BASALT_DELTAS, PortalBiomeColor.BASALT_DELTAS
    );

    public static PortalBiomeColor getColor(ResourceKey<Biome> biome) {
        return COLORS.getOrDefault(biome, PortalBiomeColor.NONE);
    }
}