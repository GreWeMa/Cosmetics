package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.AbstractCosmeticEffect;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public final class GlobeCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "GLOBE";

    public GlobeCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public GlobeCosmeticEffect(Optional<String> id,
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

        private final ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).build();

        private double phi = 0;

        private EffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        /* Original code have been copied from "CrazyFeet-Sponge" developed by runescapejon */
        @Override
        public void run() {
            phi += Math.PI / 10;
            if (phi >= Math.PI) {
                phi = 0;
            }
            Vector3d position = getPosition();
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
                double r = 1.5;
                double x = r * Math.cos(theta) * Math.sin(phi);
                double y = r * Math.cos(phi) + 1.5;
                double z = r * Math.sin(theta) * Math.sin(phi);
                getViewer().spawnParticles(effect, position.add(x, y, z));
            }
        }
    }
}
