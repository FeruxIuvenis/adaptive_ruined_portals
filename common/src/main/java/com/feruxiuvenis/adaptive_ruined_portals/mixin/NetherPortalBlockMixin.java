package com.feruxiuvenis.adaptive_ruined_portals.mixin;

import com.feruxiuvenis.adaptive_ruined_portals.utils.color.PortalBiomeColorProperty;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    private static final Logger LOGGER =
            LoggerFactory.getLogger("AdaptiveRuinedPortals");

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void adaptiveRuinedPortals$addColorProperty(
            StateDefinition.Builder<Block, BlockState> builder,
            CallbackInfo ci
    ) {
        builder.add(PortalBiomeColorProperty.PORTAL_BIOME_COLOR);
        LOGGER.info("[COLOR-DEBUG] portal_biome_color property added to NetherPortalBlock state definition");
    }
}