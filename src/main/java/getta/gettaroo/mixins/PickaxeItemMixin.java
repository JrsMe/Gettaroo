package getta.gettaroo.mixins;

import getta.gettaroo.config.Configs;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PickaxeItem.class, priority = 1000)
public class PickaxeItemMixin {

    public void miningSpeedHandler(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        for (String block : Configs.Server.INSTA_MINE_BLOCKS.getStrings()) {
            if (state.isOf(Registries.BLOCK.get(new Identifier(block)))) {
                cir.setReturnValue(38.28572F);
                return;
            }
        }
    }
}
