package dev.gwm.spongeplugin.cosmetics.utils;

import dev.gwm.spongeplugin.cosmetics.Cosmetics;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.CosmeticEffect;
import dev.gwm.spongeplugin.library.utils.Config;
import dev.gwm.spongeplugin.library.utils.SuperObjectsService;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.util.Color;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public class CosmeticsUtils {

    public static final ParticleEffect DEFAULT_PARTICLE_EFFECT = ParticleEffect.builder().
            type(ParticleTypes.REDSTONE_DUST).
            build();

    public static boolean loadCosmeticEffect(File file, boolean force) {
        try {
            Config cosmeticEffectConfig = new Config(Cosmetics.getInstance(), file);
            ConfigurationNode loadNode = cosmeticEffectConfig.getNode("LOAD");
            if (force || loadNode.getBoolean(true)) {
                CosmeticEffect cosmeticEffect = Sponge.getServiceManager().provide(SuperObjectsService.class).get().
                        create(CosmeticsSuperObjectCategories.COSMETIC_EFFECT, cosmeticEffectConfig.getNode());
                if (Cosmetics.getInstance().isLogLoadedEffects()) {
                    Cosmetics.getInstance().getLogger().
                            info("Loaded the Cosmetic Effect from the file \"" + getCosmeticEffectRelativePath(file) + "\" with id \"" + cosmeticEffect.id() + "\"!");
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

    public static Color getRandomColor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Color.ofRgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
