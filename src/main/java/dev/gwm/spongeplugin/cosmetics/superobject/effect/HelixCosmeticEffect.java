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

public class HelixCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "HELIX";

    private final Optional<Color> color;
    private final boolean perParticleColor;

    public HelixCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode colorNode = node.getNode("COLOR");
            ConfigurationNode perParticleColorNode = node.getNode("PER_PARTICLE_COLOR");
            if (colorNode.isVirtual()) {
                color = Optional.empty();
            } else {
                color = Optional.of(colorNode.getValue(TypeToken.of(Color.class)));
            }
            perParticleColor = perParticleColorNode.getBoolean(false);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public HelixCosmeticEffect(String id,
                               Optional<Long> delay, Optional<Vector3d> defaultOffset,
                               Optional<Color> color, boolean perParticleColor) {
        super(id, delay, defaultOffset);
        this.color = color;
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

    public Optional<Color> getColor() {
        return color;
    }

    public boolean isPerParticleColor() {
        return perParticleColor;
    }

    private final class EffectRunnable extends AbstractEffectRunnable {

        private double phi = 0;

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        @Override
        public void run() {
            phi = phi + Math.PI / 16;
            if (phi >= Math.PI) {
                phi = 0;
            }
            Vector3d position = getPosition();
            double x, y, z;
            Color particleColor = color.orElse(CosmeticsUtils.getRandomColor());
            for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 16) {
                for (double i = 0; i <= 1; i = i + 1) {
                    if (perParticleColor) {
                        particleColor = CosmeticsUtils.getRandomColor();
                    }
                    x = 0.15 * (2 * Math.PI - t) * Math.cos(t + phi + i * Math.PI);
                    y = 0.5 * t;
                    z = 0.15 * (2 * Math.PI - t) * Math.sin(t + phi + i * Math.PI);
                    getViewer().spawnParticles(
                            ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).
                                    option(ParticleOptions.COLOR, particleColor).
                                    build(),
                            position.add(x, y, z));
                }
            }
        }
    }
}
