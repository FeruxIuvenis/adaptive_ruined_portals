package com.feruxiuvenis.adaptive_ruined_portals.utils.theme;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Describes how to re-theme the blocks around a Ruined Portal to match
 * a particular Nether destination biome.
 *
 * Two independent passes, applied in order:
 *
 *   1. Replacement rules  - swap an existing block for something else
 *      (e.g. netherrack -> crimson nylium), optionally probabilistic
 *      (e.g. a small chance of bone block instead of soul sand).
 *
 *   2. Surface decorations - after replacement, optionally place a
 *      decorative block in the empty space directly above a qualifying
 *      surface block (e.g. crimson fungus growing out of nylium, soul
 *      fire burning on soul sand).
 */
public record PortalBlockTheme(
        Map<Block, List<ReplacementRule>> replacements,
        List<SurfaceDecoration> decorations
) {

    /**
     * Resolves what the given block should become, rolling through this
     * theme's replacement rules for that block type in order. The first
     * rule whose probability check succeeds wins. If no rule triggers
     * (or none exist for this block), the original state is returned
     * unchanged.
     */
    public BlockState resolveReplacement(BlockState original, RandomSource random) {
        List<ReplacementRule> rules = replacements.get(original.getBlock());

        if (rules == null) {
            return original;
        }

        for (ReplacementRule rule : rules) {
            if (rule.probability() >= 1.0F || random.nextFloat() < rule.probability()) {
                return rule.result();
            }
        }

        return original;
    }

    /**
     * A single possible outcome for a source block. Rules for the same
     * source block are tried in order; probability 1.0 always triggers.
     */
    public record ReplacementRule(float probability, BlockState result) {

        public static ReplacementRule always(BlockState result) {
            return new ReplacementRule(1.0F, result);
        }

        public static ReplacementRule chance(float probability, BlockState result) {
            return new ReplacementRule(probability, result);
        }
    }

    /**
     * Describes a decorative block that may be placed directly above a
     * qualifying surface block, but only into empty (air) space so we
     * never destroy anything vanilla or the replacement pass placed.
     */
    public record SurfaceDecoration(
            Set<Block> surfaceBlocks,
            float probability,
            List<BlockState> options
    ) {

        public boolean qualifies(BlockState surfaceState) {
            return surfaceBlocks.contains(surfaceState.getBlock());
        }

        public BlockState pickRandom(RandomSource random) {
            return options.get(random.nextInt(options.size()));
        }
    }
}