package com.feruxiuvenis.adaptive_ruined_portals;

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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.event.server.ServerStartingEvent;

import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("adaptive_ruined_portals")
@Mod.EventBusSubscriber(
        modid = "adaptive_ruined_portals",
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ForgeAdaptiveRuinedPortals {

    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS =
            DeferredRegister.create(
                    Registries.STRUCTURE_PROCESSOR,
                    "adaptive_ruined_portals"
            );

    public static final RegistryObject<StructureProcessorType<PortalSurroundingProcessor>> PORTAL_PROCESSOR =
            PROCESSORS.register(
                    "portal_surrounding",
                    () -> () -> PortalSurroundingProcessor.CODEC
            );

    public ForgeAdaptiveRuinedPortals() {
        IEventBus modEventBus =
                FMLJavaModLoadingContext.get().getModEventBus();

        PROCESSORS.register(modEventBus);

        PortalSurroundingProcessor.TYPE =
                PORTAL_PROCESSOR::get;

        System.out.println(
                "[FORGE] AdaptiveRuinedPortals Forge constructor initialized"
        );
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        System.out.println(
                "[FORGE] ServerStartingEvent fired"
        );

        PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER =
                ForgeAdaptiveRuinedPortals::getNetherTarget;

        System.out.println(
                "[FORGE] NetherGenerationProvider initialized: "
                        + PortalSurroundingProcessor.NETHER_GENERATION_PROVIDER
        );
    }

    private static ServerLevel getNetherLevel() {
        MinecraftServer server =
                ServerLifecycleHooks.getCurrentServer();

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
            BlockPos overworldPos
    ) {
        System.out.println(
                "[FORGE] Calculating Nether target for "
                        + overworldPos.toShortString()
        );

        NetherPortalDestinationHandler.NetherTargetResult result =
                NetherPortalDestinationHandler.getNetherTarget(
                        overworldPos,
                        getNetherBiomeSource(),
                        getNetherClimateSampler()
                );

        System.out.println(
                "[FORGE] Nether target resolved: "
                        + overworldPos.toShortString()
                        + " -> "
                        + result.getBiomePath()
        );

        return result;
    }
}