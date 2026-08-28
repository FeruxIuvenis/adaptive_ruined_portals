package com.feruxiuvenis.adaptive_ruined_portals.utils.theme;

import com.feruxiuvenis.adaptive_ruined_portals.utils.theme.PortalBlockTheme.ReplacementRule;
import com.feruxiuvenis.adaptive_ruined_portals.utils.theme.PortalBlockTheme.SurfaceDecoration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PortalBlockThemes {

    private PortalBlockThemes() {
    }

    private static final Map<ResourceKey<Biome>, PortalBlockTheme> THEMES = Map.of(

            Biomes.NETHER_WASTES, new PortalBlockTheme(
                    Map.of(
                            Blocks.NETHERRACK, List.of(
                                    ReplacementRule.chance(0.02F, Blocks.NETHER_QUARTZ_ORE.defaultBlockState())
                            )
                    ),
                    List.of()
            ),

            Biomes.CRIMSON_FOREST, new PortalBlockTheme(
                    Map.of(
                            Blocks.NETHERRACK, List.of(
                                    ReplacementRule.always(Blocks.CRIMSON_NYLIUM.defaultBlockState())
                            )
                    ),
                    List.of(
                            new SurfaceDecoration(
                                    Set.of(Blocks.CRIMSON_NYLIUM),
                                    0.35F,
                                    List.of(
                                            Blocks.CRIMSON_FUNGUS.defaultBlockState(),
                                            Blocks.CRIMSON_ROOTS.defaultBlockState()
                                    )
                            ),
                            new SurfaceDecoration(
                                    Set.of(Blocks.CRIMSON_NYLIUM),
                                    0.06F,
                                    List.of(
                                            Blocks.SHROOMLIGHT.defaultBlockState(),
                                            Blocks.CRIMSON_STEM.defaultBlockState()
                                    )
                            )
                    )
            ),

            Biomes.WARPED_FOREST, new PortalBlockTheme(
                    Map.of(
                            Blocks.NETHERRACK, List.of(
                                    ReplacementRule.always(Blocks.WARPED_NYLIUM.defaultBlockState())
                            )
                    ),
                    List.of(
                            new SurfaceDecoration(
                                    Set.of(Blocks.WARPED_NYLIUM),
                                    0.35F,
                                    List.of(
                                            Blocks.WARPED_FUNGUS.defaultBlockState(),
                                            Blocks.WARPED_ROOTS.defaultBlockState(),
                                            Blocks.NETHER_SPROUTS.defaultBlockState(),
                                            Blocks.TWISTING_VINES.defaultBlockState()
                                    )
                            ),
                            new SurfaceDecoration(
                                    Set.of(Blocks.WARPED_NYLIUM),
                                    0.06F,
                                    List.of(
                                            Blocks.SHROOMLIGHT.defaultBlockState(),
                                            Blocks.WARPED_STEM.defaultBlockState()
                                    )
                            )
                    )
            ),

            Biomes.SOUL_SAND_VALLEY, new PortalBlockTheme(
                    Map.of(
                            Blocks.NETHERRACK, List.of(
                                    ReplacementRule.chance(0.1F, Blocks.BONE_BLOCK.defaultBlockState()),
                                    ReplacementRule.always(Blocks.SOUL_SOIL.defaultBlockState())
                            ),
                            Blocks.MAGMA_BLOCK, List.of(
                                    ReplacementRule.always(Blocks.SOUL_SAND.defaultBlockState())
                            )
                    ),
                    List.of(
                            new SurfaceDecoration(
                                    Set.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL),
                                    0.05F,
                                    List.of(Blocks.SOUL_FIRE.defaultBlockState())
                            )
                    )
            ),

            Biomes.BASALT_DELTAS, new PortalBlockTheme(
                    Map.of(
                            Blocks.NETHERRACK, List.of(
                                    ReplacementRule.always(Blocks.BASALT.defaultBlockState())
                            ),
                            Blocks.MAGMA_BLOCK, List.of(
                                    ReplacementRule.chance(0.15F, Blocks.GILDED_BLACKSTONE.defaultBlockState()),
                                    ReplacementRule.always(Blocks.BLACKSTONE.defaultBlockState())
                            )
                    ),
                    List.of(
                            new SurfaceDecoration(
                                    Set.of(Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.BASALT),
                                    0.01F,
                                    List.of(Blocks.WITHER_SKELETON_SKULL.defaultBlockState())
                            )
                    )
            )
    );

    public static PortalBlockTheme getTheme(ResourceKey<Biome> biome) {
        return THEMES.get(biome);
    }
}