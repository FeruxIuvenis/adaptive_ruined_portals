package com.feruxiuvenis.adaptive_ruined_portals.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.function.Supplier;

public class ModProcessors {

    public static Supplier<StructureProcessorType<PortalSurroundingProcessor>> PORTAL_SURROUNDING;

    public static void register() {
        StructureProcessorType<PortalSurroundingProcessor> type = () -> PortalSurroundingProcessor.CODEC;
        
        Registry.register(
                BuiltInRegistries.STRUCTURE_PROCESSOR,
                ResourceLocation.fromNamespaceAndPath("adaptive_ruined_portals", "portal_surrounding"),
                type
        );

        PORTAL_SURROUNDING = () -> type;
    }
} // THIS FILE IS NOT USED, AND CAN BE DELETED