package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.Abstract2dCosmeticEffect;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.util.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Image2dCosmeticEffect extends Abstract2dCosmeticEffect {

    public static final String TYPE = "IMAGE2D";

    private final Vector3d rotation;
    private final BufferedImage image;
    private final double horizontalParticleDistance;
    private final double verticalParticleDistance;

    private final Map<Vector3d, ParticleEffect> dots;

    public Image2dCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode rotationNode = node.getNode("ROTATION");
            ConfigurationNode imageFileNode = node.getNode("IMAGE_FILE");
            ConfigurationNode horizontalParticleDistanceNode = node.getNode("HORIZONTAL_PARTICLE_DISTANCE");
            ConfigurationNode verticalParticleDistanceNode = node.getNode("VERTICAL_PARTICLE_DISTANCE");
            if (imageFileNode.isVirtual()) {
                throw new IllegalArgumentException("IMAGE node does not exist!");
            }
            if (!rotationNode.isVirtual()) {
                rotation = GWMLibraryUtils.parseVector3d(rotationNode);
            } else {
                rotation = Vector3d.ZERO;
            }
            image = ImageIO.read(new File(imageFileNode.getString()));
            horizontalParticleDistance = horizontalParticleDistanceNode.getDouble(0.2);
            if (horizontalParticleDistance < 0 || horizontalParticleDistance >= 360) {
                throw new IllegalArgumentException("Horizontal Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            verticalParticleDistance = verticalParticleDistanceNode.getDouble(0.4);
            if (verticalParticleDistance < 0 || verticalParticleDistance >= 360) {
                throw new IllegalArgumentException("Vertical Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            dots = convertToDots(image);
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public Image2dCosmeticEffect(String id,
                                 Optional<Long> delay, Optional<Vector3d> offset,
                                 ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor,
                                 Vector3d rotation,
                                 BufferedImage image,
                                 double horizontalParticleDistance, double verticalParticleDistance) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
        this.rotation = rotation;
        this.image = image;
        if (horizontalParticleDistance <= 0) {
            throw new IllegalArgumentException("Horizontal Particle Distance is equal to or less than 0!");
        }
        this.horizontalParticleDistance = horizontalParticleDistance;
        if (verticalParticleDistance <= 0 ) {
            throw new IllegalArgumentException("Vertical Particle Distance is equal to or less than 0!");
        }
        this.verticalParticleDistance = verticalParticleDistance;
        dots = convertToDots(image);
    }

    private Map<Vector3d, ParticleEffect> convertToDots(BufferedImage image) {
        try {
            Map<Vector3d, ParticleEffect> dots = new HashMap<>();
            int height = image.getHeight();
            int width = image.getWidth();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    java.awt.Color pixelColor = new java.awt.Color(image.getRGB(x, y), true);
                    if (pixelColor.getAlpha() != 0) {
                        dots.put(new Vector3d(x * getVerticalParticleDistance(), -y * getHorizontalParticleDistance(), 0),
                                ParticleEffect.builder().
                                        from(getParticleEffect()).
                                        option(ParticleOptions.COLOR, Color.of(pixelColor)).
                                        build());
                    }
                }
            }
            return dots;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert image", e);
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
        return dots;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getHorizontalParticleDistance() {
        return horizontalParticleDistance;
    }

    public double getVerticalParticleDistance() {
        return verticalParticleDistance;
    }
}
