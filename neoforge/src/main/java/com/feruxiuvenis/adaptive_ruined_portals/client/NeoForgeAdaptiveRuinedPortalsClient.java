package com.feruxiuvenis.adaptive_ruined_portals.client;

import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalColorPalette;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public final class NeoForgeAdaptiveRuinedPortalsClient {

    private NeoForgeAdaptiveRuinedPortalsClient() {
    }

    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, tintIndex) ->
                        PortalColorPalette.toArgb(
                                state.getValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR)
                        ),
                Blocks.NETHER_PORTAL
        );
    }
}