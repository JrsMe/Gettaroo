package getta.gettaroo.config;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import getta.gettaroo.features.InventorySwapRow;
import getta.gettaroo.gui.GuiConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding; // Paquete actualizado
import net.minecraft.client.util.InputUtil;

public class Callbacks implements IClientTickHandler {

    public static boolean shouldAutoRun = false;

    public static void init() {
        IHotkeyCallback callbackGeneric = new KeyCallbackHotkeys();

        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        Hotkeys.FAST_DISCONNECT.getKeybind().setCallback(callbackGeneric);
        Hotkeys.SWAP_INVENTORY_ROW.getKeybind().setCallback(callbackGeneric);
        Hotkeys.SWAP_INVENTORY_ROW_REVERSE.getKeybind().setCallback(callbackGeneric);
        Hotkeys.SWAP_INVENTORY_CUSTOM_ROW.getKeybind().setCallback(callbackGeneric);
    }

    @Override
    public void onClientTick(MinecraftClient mc) {
    }

    public static class FeatureCallbackHold implements IValueChangeCallback<IConfigBoolean> {
        private final KeyBinding keyBind;

        public FeatureCallbackHold(KeyBinding keyBind) {
            this.keyBind = keyBind;
        }

        @Override
        public void onValueChanged(IConfigBoolean config) {
            InputUtil.Key key = InputUtil.fromTranslationKey(this.keyBind.getBoundKeyTranslationKey());
            if (config.getBooleanValue()) {
                KeyBinding.setKeyPressed(key, true);
                KeyBinding.onKeyPressed(key);
            } else {
                KeyBinding.setKeyPressed(key, false);
            }
        }
    }

    private static class KeyCallbackHotkeys implements IHotkeyCallback {

        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            MinecraftClient mc = MinecraftClient.getInstance();

            if (key == Hotkeys.OPEN_CONFIG_GUI.getKeybind()) {
                GuiBase.openGui(new GuiConfig());
                return true;
            } else if (key == Hotkeys.FAST_DISCONNECT.getKeybind()) {
                mc.disconnect(); // Método actualizado
            } else if (key == Hotkeys.SWAP_INVENTORY_ROW.getKeybind()) {
                InventorySwapRow.swapRouletteCurrent(mc, false);
            } else if (key == Hotkeys.SWAP_INVENTORY_ROW_REVERSE.getKeybind()) {
                InventorySwapRow.swapRouletteCurrent(mc, true);
            } else if (key == Hotkeys.SWAP_INVENTORY_CUSTOM_ROW.getKeybind()) {
                InventorySwapRow.swapCustomRow(mc);
            }

            return false;
        }
    }
}