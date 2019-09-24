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

public class BlockHighlightCosmeticEffect extends AbstractCosmeticEffect {

    public static final String TYPE = "BLOCK-HIGHLIGHT";

    private final ParticleEffect particleEffect;
    private final boolean perAnimationColor;
    private final boolean perParticleColor;

    public BlockHighlightCosmeticEffect(ConfigurationNode node) {
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

    public BlockHighlightCosmeticEffect(String id,
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
            for (double x = 0; x <= 1; x += 0.2) {
                for (double z = 0; z <= 1; z += 0.2) {
                    for (double y = 1; y >= 0; y -= 0.2) {
                        if (x == 0 || x == 1 || z == 0 || z == 1 || y == 1 || y == 0) { //Only borders, not insides
                            if (perParticleColor) {
                                effect = ParticleEffect.builder().
                                        from(particleEffect).
                                        option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                                        build();
                            }
                            getViewer().spawnParticles(effect, position.add(x, y, z));
                        }
                    }
                }
            }
        }
    }
}
