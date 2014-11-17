package com.af.experiments.FxCameraApp.camera;

import android.content.Context;

public final class CameraHelperFactory {

    public static CameraHelper newCameraHelper(final Context context) {
            return new CameraHelperICS(context);
    }

    private CameraHelperFactory() {}

}
