package com.af.experiments.FxCameraApp.ogles;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public interface PreviewSurfaceHelper {

    SurfaceView createPushBufferSurfaceViewIfNeed(Context context);

    void setZOrderMediaOverlay(SurfaceView surface);

    void setPreviewDisplay(SurfaceHolder holder) throws IOException;

}