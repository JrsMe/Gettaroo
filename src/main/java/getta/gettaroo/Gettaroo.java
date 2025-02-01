package getta.gettaroo;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.resource.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Gettaroo implements ClientModInitializer {

    // Logger para registrar mensajes en la consola
    public static final Logger logger = LogManager.getLogger(Constants.MOD_ID);

    // Instancias de Minecraft que podrías necesitar
    public static ItemRenderer itemRenderer;
    public static ResourceManager reloadableResourceManager;

    // Bandera para controlar actualizaciones
    public static boolean shouldUpdate = false;

    @Override
    public void onInitializeClient() {
        // Inicialización de Malilib
        InitializationHandler.getInstance().registerInitializationHandler((IInitializationHandler) new InitHandler());

        // Obtener instancias de Minecraft
        MinecraftClient client = MinecraftClient.getInstance();
        itemRenderer = client.getItemRenderer();
        reloadableResourceManager = client.getResourceManager();

        // Mensaje de confirmación de inicialización
        logger.info("Mod {} inicializado correctamente.", Constants.MOD_NAME);
    }
}