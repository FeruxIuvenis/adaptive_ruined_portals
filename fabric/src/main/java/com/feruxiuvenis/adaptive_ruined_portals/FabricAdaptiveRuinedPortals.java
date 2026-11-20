package com.feruxiuvenis.adaptive_ruined_portals;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.worldgen.NetherGenerationRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public class FabricAdaptiveRuinedPortals implements ModInitializer {
    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(
                startedServer -> server = startedServer
        );

        ServerLifecycleEvents.SERVER_STOPPING.register(
                stoppingServer -> server = null
        );

        NetherGenerationRegistry.NETHER_GENERATION_PROVIDER =
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
        ChunkGenerator generator = getNetherLevel().getChunkSource().getGenerator();
        return generator.getBiomeSource();
    }

    private static Climate.Sampler getNetherClimateSampler() {
        RandomState randomState = getNetherLevel().getChunkSource().randomState();
        return randomState.createClimateSampler(SamplerContext.EMPTY_UNCACHED);
    }

    private static NetherPortalDestinationHandler.NetherTargetResult getNetherTarget(
            BlockPos overworldPos
    ) {
        return NetherPortalDestinationHandler.getNetherTarget(
                overworldPos,
                getNetherBiomeSource(),
                getNetherClimateSampler()
        );
    }
}