package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.BaseCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsUtils;
import dev.gwm.spongeplugin.cosmetics.util.Vector3dable;
import dev.gwm.spongeplugin.cosmetics.util.Viewerable;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public final class GlobeCosmeticEffect extends BaseCosmeticEffect {

    public static final String TYPE = "GLOBE";

    public GlobeCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public GlobeCosmeticEffect(String id,
                               Optional<Long> delay, Optional<Vector3d> defaultOffset,
                               ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor) {
        super(id, delay, defaultOffset, particleEffect, perAnimationColor, perParticleColor);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Runnable createTask(Viewerable viewerable, Locatable locatable, Vector3dable offset) {
        return new EffectRunnable(viewerable, locatable, offset);
    }

    private final class EffectRunnable extends AbstractEffectRunnable {

        private double phi = 0;

        private EffectRunnable(Viewerable viewerable, Locatable locatable, Vector3dable offset) {
            super(viewerable, locatable, offset);
        }

        /* Original code have been copied from "CrazyFeet-Sponge" developed by runescapejon */
        @Override
        public void run() {
            Viewer viewer = getViewerable().getViewer();
            Vector3d position = getPosition();
            phi += Math.PI / 10;
            if (phi >= Math.PI) {
                phi = 0;
            }
            ParticleEffect effect = isPerAnimationColor() ?
                    ParticleEffect.builder().
                            from(getParticleEffect()).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build() :
                    getParticleEffect();
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
                if (isPerParticleColor()) {
                    effect = ParticleEffect.builder().
                            from(getParticleEffect()).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build();
                }
                double r = 1.5;
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi) + 1.5;
                double z = r * Math.sin(theta) * Math.sin(phi);
                viewer.spawnParticles(effect, position.add(x, y, z));
            }
        }
    }
}
