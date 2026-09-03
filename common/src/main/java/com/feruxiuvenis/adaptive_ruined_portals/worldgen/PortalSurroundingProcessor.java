package com.feruxiuvenis.adaptive_ruined_portals.worldgen;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherGenerationProvider;
import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortalSurroundingProcessor implements StructureProcessor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    public static final MapCodec<PortalSurroundingProcessor> MAP_CODEC =
            MapCodec.unit(PortalSurroundingProcessor::new);

    /**
     * Supplied by the individual mod-loader implementations.
     */
    public static NetherGenerationProvider NETHER_GENERATION_PROVIDER;

    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos position,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlaceSettings settings
    ) {
        LOGGER.debug("[PROCESSOR] processBlock called, pivot={}, pos={}", pivot.toShortString(), position.toShortString());

        if (NETHER_GENERATION_PROVIDER == null) {
            LOGGER.debug(
                    "[PROCESSOR] NetherGenerationProvider has not been initialized!"
            );

            return currentBlockInfo;
        }

        NetherPortalDestinationHandler.NetherTargetResult target =
                NETHER_GENERATION_PROVIDER.getNetherTarget(pivot);

        BlockState currentState = currentBlockInfo.state();

        LOGGER.info("[DEBUG] block={} nbt={}", currentState.getBlock(), currentBlockInfo.nbt());

        if (target.isBiome(Biomes.BASALT_DELTAS)) {
            if (currentState.is(Blocks.NETHERRACK)) {
                return new StructureTemplate.StructureBlockInfo(
                        currentBlockInfo.pos(),
                        Blocks.BASALT.defaultBlockState(),
                        currentBlockInfo.nbt()
                );
            }

            if (currentState.is(Blocks.MAGMA_BLOCK)) {
                return new StructureTemplate.StructureBlockInfo(
                        currentBlockInfo.pos(),
                        Blocks.BLACKSTONE.defaultBlockState(),
                        currentBlockInfo.nbt()
                );
            }
        }

        return currentBlockInfo;
    }

    @Override
    public MapCodec<PortalSurroundingProcessor> codec() {
        return MAP_CODEC;
    }
}