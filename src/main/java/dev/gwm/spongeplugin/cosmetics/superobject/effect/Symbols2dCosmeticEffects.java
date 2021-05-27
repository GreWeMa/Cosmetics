package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.Abstract2dCosmeticEffect;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public final class Symbols2dCosmeticEffects extends Abstract2dCosmeticEffect {

    public static final String TYPE = "SYMBOLS2D";

    private final Vector3d rotation;
    private final List<String> symbols;
    private final double horizontalParticleDistance;
    private final double verticalParticleDistance;

    private final Map<Vector3d, ParticleEffect> dots;

    public Symbols2dCosmeticEffects(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode rotationNode = node.getNode("ROTATION");
            ConfigurationNode symbolsNode = node.getNode("SYMBOLS");
            ConfigurationNode symbolsFileNode = node.getNode("SYMBOLS_FILE");
            ConfigurationNode horizontalParticleDistanceNode = node.getNode("HORIZONTAL_PARTICLE_DISTANCE");
            ConfigurationNode verticalParticleDistanceNode = node.getNode("VERTICAL_PARTICLE_DISTANCE");
            if (!rotationNode.isVirtual()) {
                rotation = GWMLibraryUtils.parseVector3d(rotationNode);
            } else {
                rotation = Vector3d.ZERO;
            }
            if (!symbolsNode.isVirtual()) {
                symbols = symbolsNode.getList(TypeToken.of(String.class));
            } else if (!symbolsFileNode.isVirtual()) {
                try (BufferedReader source = new BufferedReader(new FileReader(symbolsFileNode.getString()))) {
                    symbols = source.lines().collect(Collectors.toList());
                }
            } else {
                throw new IllegalArgumentException("Both DOTS and DOTS_FILES nodes do not exist!");
            }
            horizontalParticleDistance = horizontalParticleDistanceNode.getDouble(0.2);
            if (horizontalParticleDistance < 0 || horizontalParticleDistance >= 360) {
                throw new IllegalArgumentException("Horizontal Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            verticalParticleDistance = verticalParticleDistanceNode.getDouble(0.2);
            if (verticalParticleDistance < 0 || verticalParticleDistance >= 360) {
                throw new IllegalArgumentException("Vertical Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            dots = convertToDots(symbols);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public Symbols2dCosmeticEffects(String id,
                                    Optional<Long> delay, Optional<Vector3d> offset,
                                    ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor,
                                    Vector3d rotation,
                                    List<String> symbols, double horizontalParticleDistance, double verticalParticleDistance) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
        this.rotation = rotation;
        this.symbols = symbols;
        if (horizontalParticleDistance <= 0) {
            throw new IllegalArgumentException("Horizontal Particle Distance is equal to or less than 0!");
        }
        this.horizontalParticleDistance = horizontalParticleDistance;
        if (verticalParticleDistance <= 0 ) {
            throw new IllegalArgumentException("Vertical Particle Distance is equal to or less than 0!");
        }
        this.verticalParticleDistance = verticalParticleDistance;
        dots = convertToDots(symbols);
    }

    private Map<Vector3d, ParticleEffect> convertToDots(List<String> symbols) {
        try {
            Map<Vector3d, ParticleEffect> dots = new HashMap<>();
            for (int y = 0; y < symbols.size(); y++) {
                String string = symbols.get(y);
                for (int x = 0; x < string.length(); x++) {
                    if (string.charAt(x) != ' ') {
                        dots.put(new Vector3d(x * getVerticalParticleDistance(), -y * getHorizontalParticleDistance(), 0), getParticleEffect());
                    }
                }
            }
            return dots;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert symbols", e);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Vector3d getRotation() {
        return rotation;
    }

    @Override
    public Map<Vector3d, ParticleEffect> getDots() {
        return colorDots(Collections.unmodifiableMap(dots));
    }

    public List<String> getSymbols() {
        return symbols;
    }

    public double getHorizontalParticleDistance() {
        return horizontalParticleDistance;
    }

    public double getVerticalParticleDistance() {
        return verticalParticleDistance;
    }
}
