package com.af.experiments.FxCameraApp.ogles;

import android.graphics.SurfaceTexture;
import com.af.experiments.FxCameraApp.camera.CameraHelper;
import com.af.experiments.FxCameraApp.View.PreviewTexture;

import java.io.IOException;

import static com.af.experiments.FxCameraApp.Utils.OpenGlUtils.GL_TEXTURE_EXTERNAL_OES;

final class GlSurfaceTexture implements PreviewTexture, SurfaceTexture.OnFrameAvailableListener {

    private SurfaceTexture mSurfaceTexture;
    private OnFrameAvailableListener mOnFrameAvailableListener;

    public GlSurfaceTexture(final int texName) {
        mSurfaceTexture = new SurfaceTexture(texName);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void setOnFrameAvailableListener(final OnFrameAvailableListener l) {
        mOnFrameAvailableListener = l;
    }

    @Override
    public int getTextureTarget() {
        return GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    public void setup(final CameraHelper camera) throws IOException {
        camera.setPreviewTexture(mSurfaceTexture);
    }

    @Override
    public void updateTexImage() {
        mSurfaceTexture.updateTexImage();
    }

    @Override
    public void getTransformMatrix(final float[] mtx) {
        mSurfaceTexture.getTransformMatrix(mtx);
    }

    @Override
    public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
        if (mOnFrameAvailableListener != null) {
            mOnFrameAvailableListener.onFrameAvailable(this);
        }
    }

}