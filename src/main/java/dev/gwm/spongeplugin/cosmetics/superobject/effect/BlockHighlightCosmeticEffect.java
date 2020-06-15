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

public final class BlockHighlightCosmeticEffect extends BaseCosmeticEffect {

    public static final String TYPE = "BLOCK-HIGHLIGHT";

    public BlockHighlightCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public BlockHighlightCosmeticEffect(String id,
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

        private EffectRunnable(Viewerable viewerable, Locatable locatable, Vector3dable offset) {
            super(viewerable, locatable, offset);
        }

        @Override
        public void run() {
            Viewer viewer = getViewerable().getViewer();
            Vector3d position = getPosition();
            ParticleEffect particleEffect = isPerAnimationColor() ?
                    ParticleEffect.builder().
                            from(getParticleEffect()).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build() :
                    getParticleEffect();
            for (double x = 0; x <= 1; x += 0.2) {
                for (double z = 0; z <= 1; z += 0.2) {
                    for (double y = 1; y >= 0; y -= 0.2) {
                        if (x == 0 || x == 1 || z == 0 || z == 1 || y == 1 || y == 0) { //Only borders, not insides
                            if (isPerParticleColor()) {
                                particleEffect = ParticleEffect.builder().
                                        from(getParticleEffect()).
                                        option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                                        build();
                            }
                            viewer.spawnParticles(particleEffect, position.add(x, y, z));
                        }
                    }
                }
            }
        }
    }
}
