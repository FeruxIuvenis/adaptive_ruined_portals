package com.feruxiuvenis.adaptive_ruined_portals.utils.theme;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.Map;

public final class PortalChestLootTables {

    private PortalChestLootTables() {
    }

    private static final Map<ResourceKey<Biome>, Identifier> LOOT_TABLES =
            Map.of(
                    Biomes.NETHER_WASTES,
                    Identifier.fromNamespaceAndPath(
                            "adaptive_ruined_portals",
                            "chests/ruined_portal_nether_wastes"
                    ),

                    Biomes.CRIMSON_FOREST,
                    Identifier.fromNamespaceAndPath(
                            "adaptive_ruined_portals",
                            "chests/ruined_portal_crimson_forest"
                    ),

                    Biomes.WARPED_FOREST,
                    Identifier.fromNamespaceAndPath(
                            "adaptive_ruined_portals",
                            "chests/ruined_portal_warped_forest"
                    ),

                    Biomes.SOUL_SAND_VALLEY,
                    Identifier.fromNamespaceAndPath(
                            "adaptive_ruined_portals",
                            "chests/ruined_portal_soul_sand_valley"
                    ),

                    Biomes.BASALT_DELTAS,
                    Identifier.fromNamespaceAndPath(
                            "adaptive_ruined_portals",
                            "chests/ruined_portal_basalt_deltas"
                    )
            );

    public static Identifier getLootTable(ResourceKey<Biome> biome) {
        return LOOT_TABLES.get(biome);
    }
}