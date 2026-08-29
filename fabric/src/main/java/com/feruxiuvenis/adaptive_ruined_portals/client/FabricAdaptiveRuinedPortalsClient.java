package com.feruxiuvenis.adaptive_ruined_portals.client;

import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalColorPalette;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.level.block.Blocks;

public class FabricAdaptiveRuinedPortalsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.BLOCK.register(
                (state, level, pos, tintIndex) ->
                        PortalColorPalette.toArgb(
                                state.getValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR)
                        ),
                Blocks.NETHER_PORTAL
        );
    }
}