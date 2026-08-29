package com.feruxiuvenis.adaptive_ruined_portals.utils.color;

public final class PortalColorPalette {

    private PortalColorPalette() {
    }

    public static int toArgb(PortalBiomeColor color) {
        return switch (color) {
            case NETHER_WASTES -> 0xFFB347B3;
            case CRIMSON_FOREST -> 0xFFAA1F1F;
            case WARPED_FOREST -> 0xFF14B283;
            case SOUL_SAND_VALLEY -> 0xFF37E7EC;
            case BASALT_DELTAS -> 0xFF5B5B5B;
            case NONE -> 0xFFB347B3; // vanilla purple fallback
        };
    }
}