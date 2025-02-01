package getta.gettaroo;

import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.KeybindCategory;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class InitHandler implements IKeybindProvider {

    @Override
    public void addKeysToMap(List<KeybindMulti> list) {
        // Aquí puedes registrar tus keybinds
    }

    @Override
    public void addHotkeys(List<KeybindMulti> list) {
        // Aquí puedes registrar tus hotkeys
    }

    @Override
    public void addKeybindCategories(List<KeybindCategory> list) {
        // Aquí puedes registrar categorías de keybinds
    }

    public static void init() {
        // Registra el manejador de keybinds
        InputEventHandler.getKeybindManager().registerKeybindProvider(new InitHandler());

        // Configura el ícono predeterminado (opcional)
        GuiBase.setDefaultIcon("gettaroo:textures/icon.png");
    }
}