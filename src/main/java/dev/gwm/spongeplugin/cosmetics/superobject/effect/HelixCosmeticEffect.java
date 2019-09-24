package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.AbstractCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public class HelixCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "HELIX";

    private final ParticleEffect particleEffect;
    private final boolean perAnimationColor;
    private final boolean perParticleColor;

    public HelixCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode particleEffectNode = node.getNode("PARTICLE_EFFECT");
            ConfigurationNode perAnimationColorNode = node.getNode("PER_ANIMATION_COLOR");
            ConfigurationNode perParticleColorNode = node.getNode("PER_PARTICLE_COLOR");
            if (!particleEffectNode.isVirtual()) {
                particleEffect = GWMLibraryUtils.parseParticleEffect(particleEffectNode);
            } else {
                particleEffect = CosmeticsUtils.DEFAULT_PARTICLE_EFFECT;
            }
            perAnimationColor = perAnimationColorNode.getBoolean(false);
            perParticleColor = perParticleColorNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public HelixCosmeticEffect(String id,
                               Optional<Long> delay, Optional<Vector3d> defaultOffset,
                               ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor) {
        super(id, delay, defaultOffset);
        this.particleEffect = particleEffect;
        this.perAnimationColor = perAnimationColor;
        this.perParticleColor = perParticleColor;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Runnable createTask(Viewer viewer, Locatable locatable, Vector3d offset) {
        return new EffectRunnable(viewer, locatable, offset);
    }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }

    public boolean isPerAnimationColor() {
        return perAnimationColor;
    }

    public boolean isPerParticleColor() {
        return perParticleColor;
    }

    private final class EffectRunnable extends AbstractEffectRunnable {

        private double phi = 0;

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        /* Original code have been copied from "CrazyFeet-Sponge" developed by runescapejon */
        @Override
        public void run() {
            phi = phi + Math.PI / 16;
            if (phi >= Math.PI) {
                phi = 0;
            }
            Vector3d position = getPosition();
            double x, y, z;
            ParticleEffect effect = perAnimationColor ?
                    ParticleEffect.builder().
                            from(particleEffect).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build() :
                    particleEffect;
            for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                for (double i = 0; i <= 1; i = i + 1) {
                    if (perParticleColor) {
                        effect = ParticleEffect.builder().
                                from(particleEffect).
                                option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                                build();
                    }
                    x = 0.15 * (2 * Math.PI - t) * Math.cos(t + phi + i * Math.PI);
                    y = 0.5 * t;
                    z = 0.15 * (2 * Math.PI - t) * Math.sin(t + phi + i * Math.PI);
                    getViewer().spawnParticles(effect, position.add(x, y, z));
                }
            }
        }
    }
}
