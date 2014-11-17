package com.af.experiments.FxCameraApp.camera;

import android.content.Context;
import android.hardware.Camera;

import java.util.List;

public class CameraHelperGingerbread extends CameraHelperFroyo {

    public CameraHelperGingerbread(final Context context) {
        super(context);
    }

    @Override
    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public CameraHelper.CameraInfoCompat getCameraInfo() {
        final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(getCameraId(), cameraInfo);

        final CameraHelper.CameraInfoCompat result = new CameraHelper.CameraInfoCompat();
        result.facing = cameraInfo.facing;
        result.orientation = cameraInfo.orientation;
        return result;
    }

    @Override
    public void openCamera(final int cameraId) {
        releaseCamera();

        if (getNumberOfCameras() > 1) {
            setCamera(Camera.open(cameraId));
        } else if (cameraId != DEFAULT_CAMERA_ID) {
            throw new RuntimeException();
        } else {
            setCamera(Camera.open());
        }

        setCameraId(cameraId);
        initializeFocusMode();
    }

    @Override
    public void initializeFocusMode() {
        final List<String> supportedFocusModes = getSupportedFocusModes();
        if (supportedFocusModes != null) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                final Camera.Parameters parameters = getCamera().getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                try {
                    getCamera().setParameters(parameters);
                } catch (final RuntimeException e) {}	// 無視する
            } else {
                super.initializeFocusMode();
            }
        }
    }
}
