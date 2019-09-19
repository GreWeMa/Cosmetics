package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.AbstractCosmeticEffect;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public class CircleCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "CIRCLE";

    private final double radius;
    private final double step;
    private final int stepsPerAnimation;

    public CircleCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode radiusNode = node.getNode("RADIUS");
            ConfigurationNode stepNode = node.getNode("STEP");
            ConfigurationNode stepsPerAnimationNode = node.getNode("STEPS_PER_ANIMATION");
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

    public CircleCosmeticEffect(Optional<String> id,
                                Optional<Long> delay, Optional<Vector3d> defaultOffset,
                                double radius, double step, int stepsPerAnimation) {
        super(id, delay, defaultOffset);
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

    private final class EffectRunnable extends AbstractEffectRunnable {

        private final ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).build();

        private final double d = Math.PI - 2 * Math.acos(step / (2 * radius));
        private final double l = (2 * Math.PI) / d;
        private int i = 0;

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        @Override
        public void run() {
            for (int j = 0; j < stepsPerAnimation; j++) {
                if (i > l) {
                    i = 0;
                }
                final double x = getPosition().getX();
                final double z = getPosition().getZ();
                double xOffset = radius * Math.cos(i * d);
                double zOffset = radius * Math.sin(i * d);
                getViewer().spawnParticles(effect, getPosition().add(new Vector3d(xOffset, 0, zOffset)));
                i++;
            }
        }
    }
}
