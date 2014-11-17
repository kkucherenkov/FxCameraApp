package com.af.experiments.FxCameraApp.camera;

import android.content.Context;
import android.hardware.Camera;

public class CameraHelperCupcake extends CameraHelperBase {
    public CameraHelperCupcake(final Context context) {
        super(context);
    }

    @Override
    public void setPreviewCallback(final Camera.PreviewCallback cb) {
        getCamera().setOneShotPreviewCallback(cb);
    }

    @Override
    public void onPreviewFrame(final Camera.PreviewCallback cb) {
        getCamera().setOneShotPreviewCallback(cb);
    }
}
