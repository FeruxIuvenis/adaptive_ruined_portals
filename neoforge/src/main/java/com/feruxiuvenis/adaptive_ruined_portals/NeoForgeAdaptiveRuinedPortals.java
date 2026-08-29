package com.feruxiuvenis.adaptive_ruined_portals;

import com.feruxiuvenis.adaptive_ruined_portals.client.NeoForgeAdaptiveRuinedPortalsClient;
import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.feruxiuvenis.adaptive_ruined_portals.worldgen.PortalSurroundingProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Mod("adaptive_ruined_portals")
public class NeoForgeAdaptiveRuinedPortals {

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(
                    Registries.STRUCTURE_PROCESSOR,
                    "adaptive_ruined_portals"
            );

    public static final DeferredHolder<
    StructureProcessorType<?>,
    StructureProcessorType<PortalSurroundingProcessor>
        > PORTAL_PROCESSOR =
            PROCESSORS.register(
            "portal_surrounding",
            () -> () -> PortalSurroundingProcessor.CODEC
        );

    public NeoForgeAdaptiveRuinedPortals(IEventBus modEventBus) {
        PROCESSORS.register(modEventBus);

        PortalSurroundingProcessor.TYPE = PORTAL_PROCESSOR::get;

        PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER =
                NeoForgeAdaptiveRuinedPortals::getNetherTarget;

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(NeoForgeAdaptiveRuinedPortalsClient::onRegisterBlockColors);
        }
    }

    private static ServerLevel getNetherLevel() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) {
            throw new IllegalStateException(
                    "Cannot access Nether generation state without an active server."
            );
        }

        return server.getLevel(Level.NETHER);
    }

    private static BiomeSource getNetherBiomeSource() {
        ServerLevel nether = getNetherLevel();
        ChunkGenerator generator = nether.getChunkSource().getGenerator();
        return generator.getBiomeSource();
    }

    private static Climate.Sampler getNetherClimateSampler() {
        ServerLevel nether = getNetherLevel();
        RandomState randomState = nether.getChunkSource().randomState();
        return randomState.sampler();
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