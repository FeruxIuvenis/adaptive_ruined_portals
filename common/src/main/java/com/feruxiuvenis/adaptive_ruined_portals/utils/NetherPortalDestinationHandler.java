package com.feruxiuvenis.adaptive_ruined_portals.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class NetherPortalDestinationHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    private static final int[] SAMPLE_OFFSETS_QUART = {-2, -1, 0, 1, 2};

    private NetherPortalDestinationHandler() {
    }

    public static NetherTargetResult getNetherTarget(
            BlockPos overworldPos,
            BiomeSource netherBiomeSource,
            Climate.Sampler netherSampler
    ) {
        int netherX = overworldPos.getX() >> 3;
        int netherY = overworldPos.getY();
        int netherZ = overworldPos.getZ() >> 3;

        BlockPos netherPos = new BlockPos(netherX, netherY, netherZ);

        Holder<Biome> targetBiome =
                sampleMajorityBiome(netherPos, netherBiomeSource, netherSampler);

        NetherTargetResult result =
                new NetherTargetResult(netherPos, targetBiome);

        LOGGER.info(
                "[DESTINATION HANDLER] Overworld Pos: {} -> Nether Pos: {} | Sampled Biome: {}",
                overworldPos.toShortString(),
                netherPos.toShortString(),
                result.getBiomePath()
        );

        return result;
    }

    private static Holder<Biome> sampleMajorityBiome(
            BlockPos netherPos,
            BiomeSource netherBiomeSource,
            Climate.Sampler netherSampler
    ) {
        int centerQuartX = QuartPos.fromBlock(netherPos.getX());
        int centerQuartY = QuartPos.fromBlock(netherPos.getY());
        int centerQuartZ = QuartPos.fromBlock(netherPos.getZ());

        Map<Holder<Biome>, Integer> counts = new HashMap<>();

        for (int dx : SAMPLE_OFFSETS_QUART) {
            for (int dz : SAMPLE_OFFSETS_QUART) {
                Holder<Biome> biome = netherBiomeSource.getNoiseBiome(
                        centerQuartX + dx,
                        centerQuartY,
                        centerQuartZ + dz,
                        netherSampler
                );

                counts.merge(biome, 1, Integer::sum);
            }
        }

        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseGet(() -> netherBiomeSource.getNoiseBiome(
                        centerQuartX, centerQuartY, centerQuartZ, netherSampler
                ));
    }

    public record NetherTargetResult(
            BlockPos netherPos,
            Holder<Biome> biomeHolder
    ) {

        public boolean isBiome(ResourceKey<Biome> biomeKey) {
            return biomeHolder.is(biomeKey);
        }

        public java.util.Optional<ResourceKey<Biome>> getBiomeKey() {
            return biomeHolder.unwrapKey();
        }

        public String getBiomePath() {
            return biomeHolder
                    .unwrapKey()
                    .map(key -> key.identifier().toString())
                    .orElse("unknown");
        }
    }
}