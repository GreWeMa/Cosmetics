package dev.gwm.spongeplugin.cosmetics.utils;

import dev.gwm.spongeplugin.cosmetics.Cosmetics;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.CosmeticEffect;
import dev.gwm.spongeplugin.library.utils.Config;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.nio.file.Path;

public class CosmeticsUtils {

    public static boolean loadCosmeticEffect(File file, boolean force) {
        try {
            Config cpeConfig = new Config(Cosmetics.getInstance(), file);
            ConfigurationNode loadNode = cpeConfig.getNode("LOAD");
            if (force || loadNode.getBoolean(true)) {
                CosmeticEffect cpe = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                        create(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, cpeConfig.getNode());
                if (Cosmetics.getInstance().isLogLoadedEffects()) {
                    Cosmetics.getInstance().getLogger().
                            info("Loaded the Cosmetic Effect from the file \"" + getCosmeticEffectRelativePath(file) + "\" with id \"" + cpe.getId() + "\"!");
                }
                return true;
            } else {
                Cosmetics.getInstance().getLogger().
                        info("Skipping the Cosmetic Effect from the file \"" + getCosmeticEffectRelativePath(file) + "\"!");
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load the Cosmetic Effect from the file \"" + getCosmeticEffectRelativePath(file) + "\"!", e);
        }
    }

    public static Path getCosmeticEffectRelativePath(File cosmeticEffectFile) {
        return Cosmetics.getInstance().getEffectsDirectory().toPath().relativize(cosmeticEffectFile.toPath());
    }
}
