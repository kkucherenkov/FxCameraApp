package com.af.experiments.FxCameraApp.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class CameraHelperDonut extends CameraHelperCupcake {

    protected static final Class<? extends Camera> sSharpCameraClass = getSharpCameraClass();

    @SuppressWarnings("unchecked")
    private static Class<? extends Camera> getSharpCameraClass() {
        try {
            return (Class<? extends Camera>) Class.forName("jp.co.sharp.android.hardware.CameraEx");
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

    public CameraHelperDonut(final Context context) {
        super(context);
    }

    @Override
    public int getNumberOfCameras() {
        if (sSharpCameraClass != null) {
            return 2;
        }
        return super.getNumberOfCameras();
    }

    @Override
    public CameraHelper.CameraInfoCompat getCameraInfo() {
        if (sSharpCameraClass != null) {
            final CameraHelper.CameraInfoCompat info = new CameraHelper.CameraInfoCompat();
            if (getCameraId() == DEFAULT_CAMERA_ID) {
                info.facing = CameraHelper.CameraInfoCompat.CAMERA_FACING_BACK;
                info.orientation = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 90 : 0;
            } else {
                info.facing = CameraHelper.CameraInfoCompat.CAMERA_FACING_FRONT;
                info.orientation = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 270 : 180;
            }
            return info;
        }
        return super.getCameraInfo();
    }


    @Override
    public void openCamera(final int cameraId) {
        releaseCamera();

        if (sSharpCameraClass != null) {
            final Method openMethod;
            try {
                openMethod = sSharpCameraClass.getMethod("open", int.class);
            } catch (final NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            try {
                setCamera((Camera) openMethod.invoke(null, cameraId));
            } catch (final IllegalArgumentException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (final InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else if (cameraId != DEFAULT_CAMERA_ID) {
            throw new RuntimeException();
        } else {
            setCamera(Camera.open());
        }

        setCameraId(cameraId);
        initializeFocusMode();
    }

}