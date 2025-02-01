package getta.gettaroo.mixins;

import getta.gettaroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext; // Nueva importación
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    private static final Identifier FIRE_TEXTURE = new Identifier("gettaroo:textures/fire1.png");

    @Inject(method = "render", at = @At("TAIL"))
    private void renderDurationOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (client.player == null) return;

        int x = 923;
        int y = 9;

        // Renderizar la textura de fuego si el jugador está en llamas
        if (FeatureToggle.FIRE_BETTER_RENDER.getBooleanValue() && client.player.isOnFire()) {
            RenderSystem.setShaderTexture(0, FIRE_TEXTURE);
            context.drawTexture(FIRE_TEXTURE, x + 8, y + 14, 0, 0, 32, 32, 32, 32);
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void hideScoreboard(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
        // Ocultar el scoreboard si la opción está activada
        if (FeatureToggle.HIDE_SCOREBOARD.getBooleanValue()) {
            ci.cancel();
        }
    }
}
