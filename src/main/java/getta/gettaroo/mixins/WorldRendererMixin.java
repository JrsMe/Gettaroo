package getta.gettaroo.mixins;

import getta.gettaroo.commands.PortalPositionCommand;
import getta.gettaroo.config.FeatureToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    // Método para evitar la renderización del clima
    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    public void removeWeatherRender(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (FeatureToggle.WEATHER_RENDERER.getBooleanValue()) {
            ci.cancel();  // Cancelar la renderización si la opción está activada
        }
    }

    // Método principal que realiza el renderizado de la caja del portal
    @Inject(method = "render", at = @At("RETURN"))
    public void doBox(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (FeatureToggle.PORTAL_OUTSIDE_RENDER.getBooleanValue() && PortalPositionCommand.activated) {
            // Usamos el Consumer de Outline para obtener el buffer adecuado
            OutlineVertexConsumerProvider vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();

            BlockPos pos = PortalPositionCommand.pos1;  // Obtener la primera posición del portal
            BlockPos pos1 = PortalPositionCommand.pos2;  // Obtener la segunda posición del portal
            Vec3d cam = client.cameraEntity.getPos();  // Obtener la posición de la cámara

            matrices.push();  // Guardar el estado actual de la matriz de transformaciones

            // Aplicamos una transformación de escala para mejorar la visualización
            matrices.translate(((double) pos.getX() - cam.getX()) * 2, ((double) pos.getY() - cam.getY()) * 2, ((double) pos.getZ() - cam.getZ()) * 2);
            matrices.translate(0, -1.5, 0);  // Ajuste de altura para mejorar la visualización

            // Dibujamos el borde del portal en color rojo
            WorldRenderer.drawBox(matrices, vertexConsumers.getBuffer(RenderLayer.LINES), pos.getX(), pos.getY(), pos.getZ(), pos1.getX(), pos1.getY(), pos1.getZ(), 255, 0, 0, 1);

            matrices.pop();  // Restauramos el estado anterior de la matriz
        }
    }
}