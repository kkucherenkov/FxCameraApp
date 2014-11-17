package com.af.experiments.FxCameraApp.ogles;

import com.af.experiments.FxCameraApp.View.PreviewTexture;

public class GlPreviewTextureFactory {

    private static final int HONYCOMB = 11;

    public static PreviewTexture newPreviewTexture(final int texName) {
            return new GlSurfaceTexture(texName);
    }

    private GlPreviewTextureFactory() {}

}