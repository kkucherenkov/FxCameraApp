package com.af.experiments.FxCameraApp.ogles;

import com.af.experiments.FxCameraApp.camera.CameraHelper;

public final class PreviewSurfaceHelperFactory {

    public static PreviewSurfaceHelper newPreviewSurfaceHelper(final CameraHelper camera) {
            return new PreviewSurfaceHelperBase(camera);
    }

    private PreviewSurfaceHelperFactory() {}

}