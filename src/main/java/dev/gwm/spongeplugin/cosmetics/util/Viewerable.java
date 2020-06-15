package dev.gwm.spongeplugin.cosmetics.util;

import org.spongepowered.api.effect.Viewer;

@FunctionalInterface
public interface Viewerable {

    Viewer getViewer();
}
