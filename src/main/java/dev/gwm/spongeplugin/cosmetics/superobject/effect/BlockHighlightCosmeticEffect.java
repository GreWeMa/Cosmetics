package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.AbstractCosmeticEffect;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public class BlockHighlightCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "BLOCK-HIGHLIGHT";

    public BlockHighlightCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public BlockHighlightCosmeticEffect(Optional<String> id,
                                        Optional<Long> delay, Optional<Vector3d> defaultOffset) {
        super(id, delay, defaultOffset);
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

        private ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).build();

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        @Override
        public void run() {
            for (double x = 0; x <= 1; x += 0.2) {
                for (double z = 0; z <= 1; z += 0.2) {
                    for (double y = 1; y >= 0; y -= 0.2) {
                        if (x == 0 || x == 1 || z == 0 || z == 1 || y == 1 || y == 0) { //Only borders, not insides
                            getViewer().spawnParticles(effect, getPosition().add(x, y, z));
                        }
                    }
                }
            }
        }
    }
}
