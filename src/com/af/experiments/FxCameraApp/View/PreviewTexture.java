package com.af.experiments.FxCameraApp.View;

import com.af.experiments.FxCameraApp.camera.CameraHelper;

import java.io.IOException;

public interface PreviewTexture {

    public interface OnFrameAvailableListener {
        void onFrameAvailable(PreviewTexture previewTexture);
    }

    void setOnFrameAvailableListener(final OnFrameAvailableListener l);

    int getTextureTarget();

    void setup(CameraHelper camera) throws IOException;

    void updateTexImage();

    void getTransformMatrix(float[] mtx);

}