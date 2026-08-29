package com.feruxiuvenis.adaptive_ruined_portals.utils.color;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class PortalBiomeColorProperty {

    private PortalBiomeColorProperty() {
    }

    public static final EnumProperty<PortalBiomeColor> PORTAL_BIOME_COLOR =
            EnumProperty.create("portal_biome_color", PortalBiomeColor.class);
}