package com.feruxiuvenis.adaptive_ruined_portals.client;

import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalColorPalette;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

public final class NeoForgeAdaptiveRuinedPortalsClient {

    private NeoForgeAdaptiveRuinedPortalsClient() {
    }

    public static void onRegisterBlockColors(
            RegisterColorHandlersEvent.BlockTintSources event
    ) {
        System.out.println(
                "[COLOR-DEBUG] Registering nether_portal block color provider (NeoForge)"
        );

        event.register(
                List.of(
                        new BlockTintSource() {
                            @Override
                            public int color(BlockState state) {
                                return PortalColorPalette.toArgb(
                                        state.getValue(PortalBiomeColorProperty.PORTAL_BIOME_COLOR)
                                );
                            }
                        }
                ),
                Blocks.NETHER_PORTAL
        );
    }
}