package com.af.experiments.FxCameraApp.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;

import java.io.IOException;

public class CameraHelperHonycomb extends CameraHelperGingerbread {
    public CameraHelperHonycomb(final Context context) {
        super(context);
    }


    @Override
    public void setPreviewTexture(final Object surfaceTexture) throws IOException {
        getCamera().setPreviewTexture((SurfaceTexture) surfaceTexture);
    }

}
