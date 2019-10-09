package dev.gwm.spongeplugin.cosmetics.superobject.effect.base;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsUtils;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.util.Optional;

public abstract class BaseCosmeticEffect extends AbstractCosmeticEffect {

    private final ParticleEffect particleEffect;
    private final boolean perAnimationColor;
    private final boolean perParticleColor;


    public BaseCosmeticEffect(ConfigurationNode node) {
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

    public BaseCosmeticEffect(String id,
                              Optional<Long> delay, Optional<Vector3d> offset,
                              ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor) {
        super(id, delay, offset);
        this.particleEffect = particleEffect;
        this.perAnimationColor = perAnimationColor;
        this.perParticleColor = perParticleColor;
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
}
