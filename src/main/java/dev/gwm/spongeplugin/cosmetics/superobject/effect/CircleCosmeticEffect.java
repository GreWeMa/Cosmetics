package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.AbstractCosmeticEffect;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public class CircleCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "CIRCLE";

    private final Optional<Color> color;
    private final boolean perParticleColor;
    private final double radius;
    private final double step;
    private final int stepsPerAnimation;

    public CircleCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode colorNode = node.getNode("COLOR");
            ConfigurationNode perParticleColorNode = node.getNode("PER_PARTICLE_COLOR");
            ConfigurationNode radiusNode = node.getNode("RADIUS");
            ConfigurationNode stepNode = node.getNode("STEP");
            ConfigurationNode stepsPerAnimationNode = node.getNode("STEPS_PER_ANIMATION");
            if (colorNode.isVirtual()) {
                color = Optional.empty();
            } else {
                color = Optional.of(colorNode.getValue(TypeToken.of(Color.class)));
            }
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
            if (!stepsPerAnimationNode.isVirtual()) {
                stepsPerAnimation = stepsPerAnimationNode.getInt();
            } else {
                stepsPerAnimation = 1;
            }
            if (stepsPerAnimation <= 0) {
                throw new IllegalArgumentException("Steps Per Animation is equal to or less than 0!");
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public CircleCosmeticEffect(String id,
                                Optional<Long> delay, Optional<Vector3d> defaultOffset,
                                Optional<Color> color, boolean perParticleColor,
                                double radius, double step, int stepsPerAnimation) {
        super(id, delay, defaultOffset);
        this.color = color;
        this.perParticleColor = perParticleColor;
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius is equal to or less than 0!");
        }
        this.radius = radius;
        if (step <= 0) {
            throw new IllegalArgumentException("Step is equal to or less than 0!");
        }
        this.step = step;
        if (stepsPerAnimation <= 0) {
            throw new IllegalArgumentException("Steps Per Animation is equal to or less than 0!");
        }
        this.stepsPerAnimation = stepsPerAnimation;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Runnable createTask(Viewer viewer, Locatable locatable, Vector3d offset) {
        return new EffectRunnable(viewer, locatable, offset);
    }

    public Optional<Color> getColor() {
        return color;
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

    public int getStepsPerAnimation() {
        return stepsPerAnimation;
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
            Color particleColor = color.orElse(CosmeticsUtils.getRandomColor());
            for (int j = 0; j < stepsPerAnimation; j++) {
                if (perParticleColor) {
                    particleColor = CosmeticsUtils.getRandomColor();
                }
                if (i > l) {
                    i = 0;
                }
                final double x = position.getX();
                final double z = position.getZ();
                double xOffset = radius * Math.cos(i * d);
                double zOffset = radius * Math.sin(i * d);
                getViewer().spawnParticles(
                        ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).
                                option(ParticleOptions.COLOR, particleColor).
                                build(),
                        position.add(new Vector3d(xOffset, 0, zOffset)));
                i++;
            }
        }
    }
}
