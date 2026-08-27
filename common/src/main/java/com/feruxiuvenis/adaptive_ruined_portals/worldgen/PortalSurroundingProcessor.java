package com.feruxiuvenis.adaptive_ruined_portals.worldgen;

import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherGenerationProvider;
import com.feruxiuvenis.adaptive_ruined_portals.utils.NetherPortalDestinationHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

public class PortalSurroundingProcessor extends StructureProcessor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    public static final MapCodec<PortalSurroundingProcessor> CODEC =
            MapCodec.unit(PortalSurroundingProcessor::new);

    public static Supplier<StructureProcessorType<PortalSurroundingProcessor>> TYPE;

    /**
     * Supplied by the individual mod-loader implementations.
     */
    public static NetherGenerationProvider NETHER_GENERATION_PROVIDER;

    /**
     * The vanilla loot table that Ruined Portal chests ship with.
     * Used to confirm we're only touching the intended chest, not any
     * other chest that might end up inside this structure's bounding box.
     */
    private static final ResourceLocation VANILLA_RUINED_PORTAL_LOOT_TABLE =
            ResourceLocation.withDefaultNamespace("chests/ruined_portal");

    /**
     * Maps a destination Nether biome to this mod's biome-specific loot
     * table. Only the biomes present here get a swapped table; anything
     * absent (or unresolved) falls back to leaving the chest untouched.
     */
    private static final Map<net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome>, ResourceLocation> LOOT_TABLES =
            Map.of(
                    Biomes.NETHER_WASTES, ResourceLocation.fromNamespaceAndPath(
                            "adaptive_ruined_portals", "chests/ruined_portal_nether_wastes"),
                    Biomes.CRIMSON_FOREST, ResourceLocation.fromNamespaceAndPath(
                            "adaptive_ruined_portals", "chests/ruined_portal_crimson_forest"),
                    Biomes.WARPED_FOREST, ResourceLocation.fromNamespaceAndPath(
                            "adaptive_ruined_portals", "chests/ruined_portal_warped_forest"),
                    Biomes.SOUL_SAND_VALLEY, ResourceLocation.fromNamespaceAndPath(
                            "adaptive_ruined_portals", "chests/ruined_portal_soul_sand_valley"),
                    Biomes.BASALT_DELTAS, ResourceLocation.fromNamespaceAndPath(
                            "adaptive_ruined_portals", "chests/ruined_portal_basalt_deltas")
            );

    @Override
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

        /*
         * pivot represents the origin of this ruined portal.
         * Every block processed for this structure uses the same
         * destination calculation.
         */
        NetherPortalDestinationHandler.NetherTargetResult target =
                NETHER_GENERATION_PROVIDER.getNetherTarget(pivot);

        BlockState currentState = currentBlockInfo.state();

        // --- existing block-swap logic (unchanged) ---
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

        // --- new: chest loot-table swap ---
        StructureTemplate.StructureBlockInfo lootResult =
                tryApplyChestLoot(currentBlockInfo, target);

        if (lootResult != null) {
            return lootResult;
        }

        return currentBlockInfo;
    }

    /**
     * If this block is the vanilla Ruined Portal chest and we have a
     * biome-specific loot table registered for the resolved destination,
     * returns a new StructureBlockInfo with the LootTable NBT swapped.
     * Otherwise returns null (caller falls back to currentBlockInfo).
     */
    private static StructureTemplate.StructureBlockInfo tryApplyChestLoot(
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            NetherPortalDestinationHandler.NetherTargetResult target
    ) {
        BlockState currentState = currentBlockInfo.state();

        if (!(currentState.getBlock() instanceof ChestBlock)) {
            return null;
        }

        CompoundTag existingNbt = currentBlockInfo.nbt();

        if (existingNbt == null) {
            LOGGER.debug("[LOOT] Chest at {} has no NBT — skipping.", currentBlockInfo.pos());
            return null;
        }

        String existingLootTable = existingNbt.getString("LootTable");

        if (!VANILLA_RUINED_PORTAL_LOOT_TABLE.toString().equals(existingLootTable)) {
            LOGGER.debug(
                    "[LOOT] Chest at {} has loot table '{}', not the vanilla ruined_portal table — skipping.",
                    currentBlockInfo.pos(), existingLootTable
            );
            return null;
        }

        var biomeKey = target.getBiomeKey().orElse(null);

        if (biomeKey == null) {
            LOGGER.debug("[LOOT] Destination biome has no resource key — skipping loot swap.");
            return null;
        }

        ResourceLocation newLootTable = LOOT_TABLES.get(biomeKey);

        if (newLootTable == null) {
            LOGGER.info("[LOOT] No custom loot table registered for biome {} — leaving vanilla loot.", target.getBiomePath());
            return null;
        }

        LOGGER.info(
                "[LOOT] Portal chest at {} -> destination biome {} -> loot table {}",
                currentBlockInfo.pos(), target.getBiomePath(), newLootTable
        );

        CompoundTag newNbt = existingNbt.copy();
        newNbt.putString("LootTable", newLootTable.toString());

        return new StructureTemplate.StructureBlockInfo(
                currentBlockInfo.pos(),
                currentState,
                newNbt
        );
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE.get();
    }
}