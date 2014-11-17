package com.af.experiments.FxCameraApp.ogles;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.af.experiments.FxCameraApp.camera.CameraHelper;

import java.io.IOException;

class PreviewSurfaceHelperBase implements PreviewSurfaceHelper {
    private CameraHelper mCameraHelper;

    public PreviewSurfaceHelperBase(final CameraHelper camera) {
        mCameraHelper = camera;
    }
    @Override
    public SurfaceView createPushBufferSurfaceViewIfNeed(final Context context) {
        final SurfaceView surface = new SurfaceView(context);
        surface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surface.setKeepScreenOn(true);
        surface.setWillNotDraw(true);
        return surface;
    }

    @Override
    public void setZOrderMediaOverlay(final SurfaceView surface) {
        surface.setZOrderMediaOverlay(true);
    }

    @Override
    public void setPreviewDisplay(final SurfaceHolder holder) throws IOException {
        mCameraHelper.setPreviewDisplay(holder);
    }

}