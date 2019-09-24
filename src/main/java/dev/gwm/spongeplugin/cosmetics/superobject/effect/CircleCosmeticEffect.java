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

public class CircleCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "CIRCLE";

    private final ParticleEffect particleEffect;
    private final boolean perAnimationColor;
    private final boolean perParticleColor;
    private final double radius;
    private final double step;
    private final int particlesPerAnimation;

    public CircleCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode particleEffectNode = node.getNode("PARTICLE_EFFECT");
            ConfigurationNode perAnimationColorNode = node.getNode("PER_ANIMATION_COLOR");
            ConfigurationNode perParticleColorNode = node.getNode("PER_PARTICLE_COLOR");
            ConfigurationNode radiusNode = node.getNode("RADIUS");
            ConfigurationNode stepNode = node.getNode("STEP");
            ConfigurationNode particlesPerAnimationNode = node.getNode("PARTICLES_PER_ANIMATION");
            if (!particleEffectNode.isVirtual()) {
                particleEffect = GWMLibraryUtils.parseParticleEffect(particleEffectNode);
            } else {
                particleEffect = CosmeticsUtils.DEFAULT_PARTICLE_EFFECT;
            }
            perAnimationColor = perAnimationColorNode.getBoolean(false);
            perParticleColor = perParticleColorNode.getBoolean(false);
            if (!radiusNode.isVirtual()) {
                radius = radiusNode.getDouble();
            } else {
                radius = 1;
            }
            if (radius <= 0) {
                throw new IllegalArgumentException("Radius is equal to or less than 0!");
            }
            if (!stepNode.isVirtual()) {
                step = stepNode.getDouble();
            } else {
                step = 0.2;
            }
            if (step <= 0) {
                throw new IllegalArgumentException("Step is equal to or less than 0!");
            }
            if (!particlesPerAnimationNode.isVirtual()) {
                particlesPerAnimation = particlesPerAnimationNode.getInt();
            } else {
                particlesPerAnimation = 1;
            }
            if (particlesPerAnimation <= 0) {
                throw new IllegalArgumentException("Steps Per Animation is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public CircleCosmeticEffect(String id,
                                Optional<Long> delay, Optional<Vector3d> defaultOffset,
                                ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor,
                                double radius, double step, int particlesPerAnimation) {
        super(id, delay, defaultOffset);
        this.particleEffect = particleEffect;
        this.perAnimationColor = perAnimationColor;
        this.perParticleColor = perParticleColor;
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius is equal to or less than 0!");
        }
        this.radius = radius;
        if (step <= 0) {
            throw new IllegalArgumentException("Step is equal to or less than 0!");
        }
        this.step = step;
        if (particlesPerAnimation <= 0) {
            throw new IllegalArgumentException("Steps Per Animation is equal to or less than 0!");
        }
        this.particlesPerAnimation = particlesPerAnimation;
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

    public double getRadius() {
        return radius;
    }

    public double getStep() {
        return step;
    }

    public int getParticlesPerAnimation() {
        return particlesPerAnimation;
    }

    private final class EffectRunnable extends AbstractEffectRunnable {

        private final double d = Math.PI - 2 * Math.acos(step / (2 * radius));
        private final double l = (2 * Math.PI) / d;
        private int i = 0;

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        @Override
        public void run() {
            Vector3d position = getPosition();
            ParticleEffect effect = perAnimationColor ?
                    ParticleEffect.builder().
                            from(particleEffect).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build() :
                    particleEffect;
            for (int j = 0; j < particlesPerAnimation; j++) {
                if (perParticleColor) {
                    effect = ParticleEffect.builder().
                            from(particleEffect).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build();
                }
                if (i > l) {
                    i = 0;
                }
                final double x = position.getX();
                final double z = position.getZ();
                double xOffset = radius * Math.cos(i * d);
                double zOffset = radius * Math.sin(i * d);
                getViewer().spawnParticles(effect, position.add(new Vector3d(xOffset, 0, zOffset)));
                i++;
            }
        }
    }
}
