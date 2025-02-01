package getta.gettaroo.features;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;

@Environment(EnvType.CLIENT)
public class CarpinchoModel extends AnimalModel<Entity> {

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart earRight;
    private final ModelPart earLeft;
    private final ModelPart hat;
    private final ModelPart hatBrim;

    public CarpinchoModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.rightBackLeg = root.getChild("right_back_leg");
        this.leftBackLeg = root.getChild("left_back_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.earRight = root.getChild("ear_right");
        this.earLeft = root.getChild("ear_left");
        this.hat = root.getChild("hat");
        this.hatBrim = root.getChild("hat_brim");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Cuerpo
        ModelPartData body = root.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-7.0F, -7.0F, -11.0F, 14.0F, 14.0F, 22.0F),
                ModelTransform.pivot(0.0F, 11.0F, 0.0F)
        );

        // Cabeza
        ModelPartData head = root.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 36)
                        .cuboid(-4.0F, -5.5F, -11.0F, 8.0F, 10.0F, 14.0F),
                ModelTransform.pivot(0.0F, 4.5F, -10.0F)
        );

        // Patas
        root.addChild("right_back_leg",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F),
                ModelTransform.pivot(-3.5F, 13.3F, 10.0F)
        );

        root.addChild("left_back_leg",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-2.5F, 0.0F, -2.5F, 5.0F, 11.0F, 5.0F),
                ModelTransform.pivot(3.5F, 13.3F, 10.0F)
        );

        root.addChild("right_front_leg",
                ModelPartBuilder.create()
                        .uv(50, 0)
                        .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F),
                ModelTransform.pivot(-4.0F, 13.3F, -8.0F)
        );

        root.addChild("left_front_leg",
                ModelPartBuilder.create()
                        .uv(50, 0)
                        .cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F),
                ModelTransform.pivot(4.0F, 13.3F, -8.0F)
        );

        // Orejas
        root.addChild("ear_right",
                ModelPartBuilder.create()
                        .uv(0, 16)
                        .cuboid(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 3.0F),
                ModelTransform.pivot(-3.5F, -5.0F, 1.5F)
        );

        root.addChild("ear_left",
                ModelPartBuilder.create()
                        .uv(0, 16)
                        .mirrored()
                        .cuboid(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 3.0F),
                ModelTransform.pivot(3.5F, -5.0F, 1.5F)
        );

        // Sombrero
        root.addChild("hat",
                ModelPartBuilder.create()
                        .uv(30, 36)
                        .cuboid(-3.5F, -4.0F, -1.5F, 7.0F, 4.0F, 7.0F),
                ModelTransform.pivot(0.0F, -5.1F, -1.5F)
        );

        root.addChild("hat_brim",
                ModelPartBuilder.create()
                        .uv(45, 17)
                        .cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 0.0F, 5.0F),
                ModelTransform.pivot(0.0F, 0.0F, -3.5F)
        );

        return TexturedModelData.of(modelData, 80, 74);
    }

    @Override
    public void setAngles(Entity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Configuración de ángulos base
        this.head.pitch = headPitch * (float) (Math.PI / 180);
        this.head.yaw = headYaw * (float) (Math.PI / 180);

        // Animación de las patas
        float speed = 1.0F;
        float degree = 1.0F;

        this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.4F) * 0.8F * limbDistance;
        this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.4F + (float) Math.PI) * 0.8F * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.4F + (float) Math.PI) * 0.8F * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.4F) * 0.8F * limbDistance;

        // Animación en el agua
        if (entity.isTouchingWater()) {
            this.body.pivotY = 11.0F + MathHelper.cos(animationProgress * 0.4F) * 0.5F;
        } else {
            this.body.pivotY = 11.0F;
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return Collections.emptyList(); // La cabeza está en getBodyParts
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(
                body,
                head,
                rightBackLeg,
                leftBackLeg,
                rightFrontLeg,
                leftFrontLeg,
                earRight,
                earLeft,
                hat,
                hatBrim
        );
    }
}