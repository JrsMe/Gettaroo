package getta.gettaroo.mixins;

import getta.gettaroo.Constants;
import getta.gettaroo.Gettaroo;
import getta.gettaroo.config.Cheats;
import getta.gettaroo.config.Configs;
import getta.gettaroo.config.FeatureToggle;
import getta.gettaroo.features.MovementSpeed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow @Final protected MinecraftClient client;

    @Shadow private boolean showsDeathScreen;

    @Shadow public Input input;

    @Shadow public abstract boolean isSubmergedInWater();

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void cancelDrop(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
        for (String item : Configs.Lists.PREVENT_DROPPING_LIST.getStrings()) {
            if (Registry.ITEM.getId(client.player.getInventory().getStack(client.player.getInventory().selectedSlot).getItem()).equals(new Identifier(item))) {
                cir.cancel();
            }
        }
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    public void shouldSlowDown(CallbackInfoReturnable<Boolean> cir) {
        if (FeatureToggle.FAST_SHIFT.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "showsDeathScreen", at = @At("HEAD"), cancellable = true)
    public void showsDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        if (FeatureToggle.SHOW_DEATH_SCREEN.getBooleanValue()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        // Jetpack
        if (Cheats.JETPACK.getBooleanValue() && client.player.input.jumping) {
            int amount = (int) Configs.Utils.JETPACK_SPEED.getDoubleValue();
            for (int i = 0; i < amount; i++) {
                client.player.jump();
            }
        }

        // Giga Jump
        if (Cheats.GIGAJUMP.getBooleanValue() && client.player.isFallFlying()) {
            Vec3d velocity = client.player.getVelocity();
            client.player.setVelocity(velocity.x, MovementSpeed.checkMovement(client.player).y, velocity.z);
        }

        // Spider
        if (Cheats.SPIDER.getBooleanValue() && client.player.horizontalCollision) {
            Vec3d velocity = client.player.getVelocity();
            if (velocity.y < 0.2) {
                client.player.setVelocity(velocity.x, 0.2, velocity.z);
            }
        }

        // Fall Damage
        if (Cheats.FALL_DAMAGE.getBooleanValue() && client.player.fallDistance > (client.player.isFallFlying() ? 1 : 2)) {
            if (!client.player.isFallFlying() || !client.player.isSneaking() || isFallingFastEnoughToCauseDamage(client.player)) {
                client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket(true));
            }
        }

        // Pig and Hoglin Texture Changes
        if (FeatureToggle.PIGS_ARE_FAT_CARPINCHOS.getBooleanValue() && !pig) {
            Gettaroo.shouldUpdate = true;
            Identifier texture = new Identifier(Constants.MOD_ID, "textures/entity/carpincho.png");
            PigEntityRendererInterfaceMixin.setTexture(texture);
            pig = true;
            didPig = false;
        } else if (!FeatureToggle.PIGS_ARE_FAT_CARPINCHOS.getBooleanValue() && !didPig) {
            pig = false;
            didPig = true;
            Gettaroo.shouldUpdate = true;
            Identifier texture = new Identifier("textures/entity/pig/pig.png");
            PigEntityRendererInterfaceMixin.setTexture(texture);
        }

        if (FeatureToggle.HOGLINS_ARE_FAT_CAPINCHOS.getBooleanValue() && !hoglin) {
            Gettaroo.shouldUpdate = true;
            Identifier texture = new Identifier(Constants.MOD_ID, "textures/entity/carpincho.png");
            HoglinEntityRendererInterface.setTexture(texture);
            hoglin = true;
            didHoglin = false;
        } else if (!FeatureToggle.HOGLINS_ARE_FAT_CAPINCHOS.getBooleanValue() && !didHoglin) {
            hoglin = false;
            didHoglin = true;
            Gettaroo.shouldUpdate = true;
            Identifier texture = new Identifier("textures/entity/hoglin/hoglin.png");
            HoglinEntityRendererInterface.setTexture(texture);
        }
    }

    private boolean pig = false;
    private boolean didPig = false;
    private boolean hoglin = false;
    private boolean didHoglin = false;

    private boolean isFallingFastEnoughToCauseDamage(ClientPlayerEntity player) {
        return player.getVelocity().y < -0.5;
    }
}