package getta.gettaroo.commands;

import com.mojang.brigadier.CommandDispatcher;
import getta.gettaroo.config.FeatureToggle;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class PortalPositionCommand {

    public static BlockPos pos1;
    public static BlockPos pos2;
    public static int current = 1; // Inicializa current para evitar comportamientos inesperados
    public static boolean activated = false;

    public static void register() {
        // Registra el comando usando ClientCommandRegistrationCallback
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("portal")
                    .then(ClientCommandManager.literal("select")
                            .executes(context -> select(context.getSource()))) // Pasa el source para enviar feedback
                    .then(ClientCommandManager.literal("add")
                            .executes(context -> add(context.getSource()))) // Pasa el source para enviar feedback
                    .then(ClientCommandManager.literal("show")
                            .executes(context -> showPortal(context.getSource()))) // Pasa el source para enviar feedback
                    .then(ClientCommandManager.literal("remove")
                            .executes(context -> remove(context.getSource())))); // Pasa el source para enviar feedback
        });
    }

    public static int showPortal(FabricClientCommandSource source) {
        pos1 = new BlockPos(50, 50, 50);
        pos2 = new BlockPos(54, 55, 51);
        activated = true;
        source.sendFeedback(Text.of("Portal mostrado en las coordenadas predeterminadas."));
        return 1;
    }

    public static int select(FabricClientCommandSource source) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult hit = mc.crosshairTarget;

        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos selectedPos = new BlockPos((int) hit.getPos().getX(), (int) hit.getPos().getY(), (int) hit.getPos().getZ());

            if (current == 1) {
                pos1 = selectedPos;
                source.sendFeedback(Text.of("Posición 1 seleccionada: " + pos1.toShortString()));
            } else if (current == 2) {
                pos2 = selectedPos;
                source.sendFeedback(Text.of("Posición 2 seleccionada: " + pos2.toShortString()));
            }

            changeCurrent();
            return 1;
        } else {
            source.sendError(Text.of("¡No estás mirando un bloque!"));
            return 0;
        }
    }

    public static int add(FabricClientCommandSource source) {
        if (FeatureToggle.PORTAL_OUTSIDE_RENDER.getBooleanValue()) {
            activated = true;
            source.sendFeedback(Text.of("Portal activado."));
            return 1;
        } else {
            source.sendError(Text.of("La función PORTAL_OUTSIDE_RENDER no está activada."));
            return 0;
        }
    }

    public static int remove(FabricClientCommandSource source) {
        if (FeatureToggle.PORTAL_OUTSIDE_RENDER.getBooleanValue()) {
            activated = false;
            source.sendFeedback(Text.of("Portal desactivado."));
            return 1;
        } else {
            source.sendError(Text.of("La función PORTAL_OUTSIDE_RENDER no está activada."));
            return 0;
        }
    }

    public static void changeCurrent() {
        current = (current == 1) ? 2 : 1;
    }
}