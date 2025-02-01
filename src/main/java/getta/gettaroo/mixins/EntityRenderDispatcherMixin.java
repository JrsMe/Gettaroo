package getta.gettaroo.mixins;

import getta.gettaroo.Gettaroo;
import getta.gettaroo.config.Configs;
import getta.gettaroo.config.FeatureToggle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Shadow @Final private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Shadow
    protected abstract void registerRenderers(ItemRenderer itemRenderer, ResourceManager resourceManager);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void cancelRenderIfSelected(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (FeatureToggle.DISABLE_RENDERING_ENTITIES.getBooleanValue() && Configs.Lists.DISABLED_ENTITIES.getStrings().size() > 0) {
            for (String entityId : Configs.Lists.DISABLED_ENTITIES.getStrings()) {
                Identifier identifier = new Identifier(entityId);
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(identifier);
                if (entityType != null && entityType.equals(entity.getType())) {
                    ci.cancel();
                    return;
                }
            }
        }

        if (Gettaroo.shouldUpdate) {
            Gettaroo.shouldUpdate = false;
            this.renderers.clear();

            try {
                if (Gettaroo.itemRenderer != null && Gettaroo.reloadableResourceManager != null) {
                    registerRenderers(Gettaroo.itemRenderer, Gettaroo.reloadableResourceManager);
                }
            } catch (NullPointerException e) {
                System.out.println("Error al registrar renderizadores: " + e.getMessage());
            }
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void getData(TextureManager textureManager, ItemRenderer itemRenderer, ResourceManager resourceManager, TextRenderer textRenderer, GameOptions gameOptions, CallbackInfo ci) {
        Gettaroo.itemRenderer = itemRenderer;
        Gettaroo.reloadableResourceManager = resourceManager;
    }
}
