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

public final class HelixCosmeticEffect extends BaseCosmeticEffect {

    public static final String TYPE = "HELIX";

    public HelixCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public HelixCosmeticEffect(String id,
                               Optional<Long> delay, Optional<Vector3d> defaultOffset,
                               ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor) {
        super(id, delay, defaultOffset, particleEffect, perAnimationColor, perParticleColor);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Runnable createTask(Viewerable viewerable, Locatable locatable, Vector3dable customOffset) {
        return new EffectRunnable(viewerable, locatable, getOffset(), customOffset);
    }

    private final class EffectRunnable extends AbstractEffectRunnable {

        private double phi = 0;

        private EffectRunnable(Viewerable viewerable, Locatable locatable, Vector3d offset, Vector3dable customOffset) {
            super(viewerable, locatable, offset, customOffset);
        }

        /* Original code have been copied from "CrazyFeet-Sponge" developed by runescapejon */
        @Override
        public void run() {
            Viewer viewer = getViewerable().getViewer();
            Vector3d position = getPosition();
            phi += Math.PI / 16;
            if (phi >= Math.PI) {
                phi = 0;
            }
            double x, y, z;
            ParticleEffect effect = isPerAnimationColor() ?
                    ParticleEffect.builder().
                            from(getParticleEffect()).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build() :
                    getParticleEffect();
            for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                for (double i = 0; i <= 1; i = i + 1) {
                    if (isPerParticleColor()) {
                        effect = ParticleEffect.builder().
                                from(getParticleEffect()).
                                option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                                build();
                    }
                    x = 0.15 * (2 * Math.PI - t) * Math.cos(t + phi + i * Math.PI);
                    y = 0.5 * t;
                    z = 0.15 * (2 * Math.PI - t) * Math.sin(t + phi + i * Math.PI);
                    viewer.spawnParticles(effect, position.add(x, y, z));
                }
            }
        }
    }
}
