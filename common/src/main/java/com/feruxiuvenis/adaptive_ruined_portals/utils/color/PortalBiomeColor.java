package com.feruxiuvenis.adaptive_ruined_portals.utils.color;

import net.minecraft.util.StringRepresentable;

public enum PortalBiomeColor implements StringRepresentable {
    NONE("none"),
    NETHER_WASTES("nether_wastes"),
    CRIMSON_FOREST("crimson_forest"),
    WARPED_FOREST("warped_forest"),
    SOUL_SAND_VALLEY("soul_sand_valley"),
    BASALT_DELTAS("basalt_deltas");

    private final String name;

    PortalBiomeColor(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}