package dev.gwm.spongeplugin.cosmetics.util;

import org.spongepowered.api.effect.Viewer;

public final class ViewerViewerable implements Viewerable {

    private final Viewer viewer;

    public ViewerViewerable(Viewer viewer) {
        this.viewer = viewer;
    }

    @Override
    public Viewer getViewer() {
        return viewer;
    }
}
