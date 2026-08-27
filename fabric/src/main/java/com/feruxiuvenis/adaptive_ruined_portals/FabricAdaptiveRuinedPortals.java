package com.feruxiuvenis.adaptive_ruined_portals;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.worldgen.PortalSurroundingProcessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class FabricAdaptiveRuinedPortals implements ModInitializer {
    private static MinecraftServer server;

    @Override
    public void onInitialize() {

        StructureProcessorType<PortalSurroundingProcessor> type =
                Registry.register(
                        BuiltInRegistries.STRUCTURE_PROCESSOR,
                        ResourceLocation.fromNamespaceAndPath(
                                "adaptive_ruined_portals",
                                "portal_surrounding"
                        ),
                        () -> PortalSurroundingProcessor.CODEC
                );

        PortalSurroundingProcessor.TYPE = () -> type;

        ServerLifecycleEvents.SERVER_STARTED.register(
                startedServer -> server = startedServer
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(
                stoppingServer -> server = null
        );

        PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER =
                FabricAdaptiveRuinedPortals::getNetherTarget;
    }

    private static ServerLevel getNetherLevel() {
        if (server == null) {
            throw new IllegalStateException(
                    "Cannot access Nether generation state without an active server."
            );
        }

        ServerLevel nether = server.getLevel(Level.NETHER);

        if (nether == null) {
            throw new IllegalStateException(
                    "The Nether ServerLevel is not available."
            );
        }

        return nether;
    }

    private static BiomeSource getNetherBiomeSource() {
        ServerLevel nether = getNetherLevel();

        ChunkGenerator generator =
                nether.getChunkSource().getGenerator();

        return generator.getBiomeSource();
    }

    private static Climate.Sampler getNetherClimateSampler() {
        ServerLevel nether = getNetherLevel();

        RandomState randomState =
                nether.getChunkSource().randomState();

        return randomState.sampler();
    }

    private static NetherPortalDestinationHandler.NetherTargetResult getNetherTarget(
            net.minecraft.core.BlockPos overworldPos
    ) {
        return NetherPortalDestinationHandler.getNetherTarget(
                overworldPos,
                getNetherBiomeSource(),
                getNetherClimateSampler()
        );
    }
}