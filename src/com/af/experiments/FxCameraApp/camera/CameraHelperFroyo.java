package com.af.experiments.FxCameraApp.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

public class CameraHelperFroyo extends CameraHelperEclair implements Camera.OnZoomChangeListener {
    private byte[] mBuffer;

    public CameraHelperFroyo(final Context context) {
        super(context);
    }

    @Override
    public void setDisplayOrientation(final int degrees) {
        getCamera().setDisplayOrientation(degrees);
    }

    @Override
    public int getOrientation() {
        final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final int degrees;
        switch (display.getRotation()) {
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_0:
            default:
                degrees = 0;
                break;
        }

        int result;
        final CameraHelper.CameraInfoCompat info = getCameraInfo();
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
//			result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    @Override
    public int getOptimalOrientation() {
        final WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final int degrees;
        switch (display.getRotation()) {
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_0:
            default:
                degrees = 0;
                break;
        }

        int result;
        final CameraHelper.CameraInfoCompat info = getCameraInfo();
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

   @Override
    public void setPreviewCallback(final Camera.PreviewCallback cb) {
        final Camera camera = getCamera();
        if (cb != null) {
            try {
                final Camera.Size previewSize = getPreviewSize();
                final Camera.Size pictureSize = getPictureSize();
                final Camera.Parameters parameters = camera.getParameters();
                mBuffer = new byte[Math.max(
                        previewSize.width * previewSize.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8,
                        pictureSize.width * pictureSize.height * ImageFormat.getBitsPerPixel(ImageFormat.RGB_565) / 8
                )];
                camera.setPreviewCallbackWithBuffer(cb);
                camera.addCallbackBuffer(mBuffer);
            } catch (final OutOfMemoryError e) {
                mBuffer = null;
                camera.setPreviewCallbackWithBuffer(null);
                super.setPreviewCallback(cb);
            }
        } else {
            mBuffer = null;
            camera.setPreviewCallbackWithBuffer(null);
            camera.setPreviewCallback(null);
        }
    }

    @Override
    public void onPreviewFrame(final Camera.PreviewCallback cb) {
        if (mBuffer != null) {
            getCamera().addCallbackBuffer(mBuffer);
        } else {
            super.onPreviewFrame(cb);
        }
    }

    @Override
    public int getMaxExposureCompensation() {
        return getCamera().getParameters().getMaxExposureCompensation();
    }

    @Override
    public int getMinExposureCompensation() {
        return getCamera().getParameters().getMinExposureCompensation();
    }

    @Override
    public float getExposureCompensationStep() {
        return getCamera().getParameters().getExposureCompensationStep();
    }

    @Override
    public int getExposureCompensation() {
        return getCamera().getParameters().getExposureCompensation();
    }

    @Override
    public void setExposureCompensation(final int value) {
        final Camera.Parameters params = getCamera().getParameters();
        params.setExposureCompensation(value);
        try {
            getCamera().setParameters(params);
        } catch (final RuntimeException  e) {}	// 無視する
    }

    @Override
    public boolean isZoomSupported() {
        return getParameters().isZoomSupported();
    }

    @Override
    public int getMaxZoom() {
        return getParameters().getMaxZoom();
    }

    @Override
    public List<Integer> getZoomRatios() {
        return getParameters().getZoomRatios();
    }
    @Override
    public int getZoom() {
        return getParameters().getZoom();
    }

    @Override
    public void setZoom(final int value) {
        final Camera.Parameters params = getParameters();
        params.setZoom(value);
        getCamera().setParameters(params);
    }


    private Camera.OnZoomChangeListener mOnZoomChangeListener;

    public void setZoomChangeListener(final Camera.OnZoomChangeListener listener) {
        mOnZoomChangeListener = listener;
        getCamera().setZoomChangeListener(this);
    }

    @Override
    public void onZoomChange(final int zoomValue, final boolean stopped, final Camera camera) {
        if (mOnZoomChangeListener != null) {
            mOnZoomChangeListener.onZoomChange(zoomValue, stopped, getCamera());
        }
    }

    @Override
    public void startSmoothZoom(final int value) {
        getCamera().startSmoothZoom(value);
    }

    @Override
    public void stopSmoothZoom() {
        getCamera().stopSmoothZoom();
    }

}
