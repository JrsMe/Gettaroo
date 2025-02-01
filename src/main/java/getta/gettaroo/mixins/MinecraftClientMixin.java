package getta.gettaroo.mixins;

import getta.gettaroo.config.Configs;
import getta.gettaroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export=true)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    public void changeWindowTitle(CallbackInfoReturnable<String> cir) {
        if (FeatureToggle.WINDOW_NAME.getBooleanValue()) {
            String windowTitle = Configs.Utils.WINDOW_NAME.getStringValue();
            if (windowTitle != null && !windowTitle.isEmpty()) {
                cir.setReturnValue(windowTitle);
            }
        }
    }
}
