package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.Abstract2dCosmeticEffect;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import io.leego.banana.BananaUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public final class Figlet2dCosmeticEffect extends Abstract2dCosmeticEffect {

    public static final String TYPE = "FIGLET2D";

    private final Vector3d rotation;
    private final List<String> text;
    private final String font;
    private final double horizontalParticleDistance;
    private final double verticalParticleDistance;

    private final Map<Vector3d, ParticleEffect> dots;

    public Figlet2dCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode rotationNode = node.getNode("ROTATION");
            ConfigurationNode textNode = node.getNode("TEXT");
            ConfigurationNode textFileNode = node.getNode("TEXT_FILE");
            ConfigurationNode fontNode = node.getNode("FONT");
            ConfigurationNode horizontalParticleDistanceNode = node.getNode("HORIZONTAL_PARTICLE_DISTANCE");
            ConfigurationNode verticalParticleDistanceNode = node.getNode("VERTICAL_PARTICLE_DISTANCE");
            if (!rotationNode.isVirtual()) {
                rotation = GWMLibraryUtils.parseVector3d(rotationNode);
            } else {
                rotation = Vector3d.ZERO;
            }
            if (!textNode.isVirtual()) {
                text = textNode.getList(TypeToken.of(String.class));
            } else if (!textFileNode.isVirtual()) {
                try (BufferedReader source = Files.newBufferedReader(Paths.get(textFileNode.getString()), StandardCharsets.UTF_8)) {
                    text = source.lines().collect(Collectors.toList());
                }
            } else {
                throw new IllegalArgumentException("Both TEXT and TEXT_FILE nodes do not exist!");
            }
            font = fontNode.getString("Banner");
            horizontalParticleDistance = horizontalParticleDistanceNode.getDouble(0.2);
            if (horizontalParticleDistance < 0 || horizontalParticleDistance >= 360) {
                throw new IllegalArgumentException("Horizontal Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            verticalParticleDistance = verticalParticleDistanceNode.getDouble(0.2);
            if (verticalParticleDistance < 0 || verticalParticleDistance >= 360) {
                throw new IllegalArgumentException("Vertical Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            dots = convertToDots(text, font);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public Figlet2dCosmeticEffect(String id,
                                  Optional<Long> delay, Optional<Vector3d> offset,
                                  ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor,
                                  Vector3d rotation,
                                  List<String> text, String font,
                                  double horizontalParticleDistance, double verticalParticleDistance) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
        this.rotation = rotation;
        this.text = text;
        this.font = font;
        if (horizontalParticleDistance <= 0) {
            throw new IllegalArgumentException("Horizontal Particle Distance is equal to or less than 0!");
        }
        this.horizontalParticleDistance = horizontalParticleDistance;
        if (verticalParticleDistance <= 0 ) {
            throw new IllegalArgumentException("Vertical Particle Distance is equal to or less than 0!");
        }
        this.verticalParticleDistance = verticalParticleDistance;
        dots = convertToDots(text, font);
    }

    private Map<Vector3d, ParticleEffect> convertToDots(List<String> text, String font) {
        try {
            String[] splited = BananaUtils.bananaify(GWMLibraryUtils.joinString(text), font).split("\n");
            Map<Vector3d, ParticleEffect> dots = new HashMap<>();
            for (int y = 0; y < splited.length; y++) {
                String string = splited[y];
                for (int x = 0; x < string.length(); x++) {
                    if (string.charAt(x) != ' ') {
                        dots.put(new Vector3d(x * getVerticalParticleDistance(), -y * getHorizontalParticleDistance(), 0), getParticleEffect());
                    }
                }
            }
            return dots;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert text", e);
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

    public List<String> getText() {
        return text;
    }

    public String getFont() {
        return font;
    }

    public double getHorizontalParticleDistance() {
        return horizontalParticleDistance;
    }

    public double getVerticalParticleDistance() {
        return verticalParticleDistance;
    }
}
