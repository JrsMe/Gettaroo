package getta.gettaroo.features;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeyCallbackToggleBoolean;
import getta.gettaroo.Constants;
import getta.gettaroo.Gettaroo;
import getta.gettaroo.config.FeatureToggle;
import getta.gettaroo.mixins.HoglinEntityRendererInterface;
import getta.gettaroo.mixins.PigEntityRendererInterfaceMixin;
import net.minecraft.util.Identifier;

public class CarpinchoCallbackToggle extends KeyCallbackToggleBoolean {

    public CarpinchoCallbackToggle(IConfigBoolean config) {
        super(config);
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        super.onKeyAction(action, key);

        Gettaroo.shouldUpdate = true;

        // Usamos el método adecuado para crear un identificador
        Identifier texture = Identifier.of("minecraft:textures/entity/pig/pig.png");

        if (FeatureToggle.PIGS_ARE_FAT_CARPINCHOS.getBooleanValue()) {
            texture = Identifier.of(Constants.MOD_ID + ":textures/entity/carpincho.png");
        }

        // Configurando la textura para el cerdo
        PigEntityRendererInterfaceMixin.setTexture(texture);

        // Para los hoglins, cambiamos también la forma de crear el identificador
        texture = Identifier.of("minecraft:textures/entity/hoglin/hoglin.png");

        if (FeatureToggle.HOGLINS_ARE_FAT_CAPINCHOS.getBooleanValue()) {
            texture = Identifier.of(Constants.MOD_ID + ":textures/entity/carpincho.png");
        }

        // Configurando la textura para el hoglin
        HoglinEntityRendererInterface.setTexture(texture);

        return true;
    }
}
