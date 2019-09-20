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

public class BlockHighlightCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "BLOCK-HIGHLIGHT";

    private final Optional<Color> color;
    private final boolean perParticleColor;

    public BlockHighlightCosmeticEffect(ConfigurationNode node) {
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

    public BlockHighlightCosmeticEffect(String id,
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

    private final class EffectRunnable extends AbstractEffectRunnable {

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        @Override
        public void run() {
            Vector3d position = getPosition();
            Color particleColor = color.orElse(CosmeticsUtils.getRandomColor());
            for (double x = 0; x <= 1; x += 0.2) {
                for (double z = 0; z <= 1; z += 0.2) {
                    for (double y = 1; y >= 0; y -= 0.2) {
                        if (x == 0 || x == 1 || z == 0 || z == 1 || y == 1 || y == 0) { //Only borders, not insides
                            if (perParticleColor) {
                                particleColor = CosmeticsUtils.getRandomColor();
                            }
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
    }
}
