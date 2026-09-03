package com.feruxiuvenis.adaptive_ruined_portals.client;

import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalColorPalette;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FabricAdaptiveRuinedPortalsClient implements ClientModInitializer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[COLOR-DEBUG] Registering nether_portal block color provider (Fabric)");

        BlockColorRegistry.register(List.of(new BlockTintSource() {
            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return PortalColorPalette.toArgb(
                        state.getValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR)
                );
            }

            @Override
            public int color(BlockState state) {
                // Fallback color when no world context is available (e.g. inventory rendering)
                return PortalColorPalette.toArgb(
                        state.getValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR)
                );
            }
        }), Blocks.NETHER_PORTAL);
    }
}