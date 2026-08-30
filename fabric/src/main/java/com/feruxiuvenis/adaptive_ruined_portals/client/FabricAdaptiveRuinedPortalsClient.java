package com.feruxiuvenis.adaptive_ruined_portals.client;

import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalColorPalette;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricAdaptiveRuinedPortalsClient implements ClientModInitializer {
    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[COLOR-DEBUG] Registering nether_portal block color provider (Fabric)");

        ColorProviderRegistry.BLOCK.register(
                (state, level, pos, tintIndex) ->
                        PortalColorPalette.toArgb(
                                state.getValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR)
                        ),
                Blocks.NETHER_PORTAL
        );
    }
}