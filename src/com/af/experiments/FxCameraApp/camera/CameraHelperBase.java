package com.af.experiments.FxCameraApp.camera;


import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

class CameraHelperBase implements CameraHelper, Camera.PictureCallback {

    private final Context mContext;

    private int mCameraId;

    private Camera mCamera;

    private Camera.PictureCallback mPictureCallback;
    public CameraHelperBase(final Context context) {
        mContext = context;
    }

    protected final Context getContext() {
        return mContext;
    }

    protected final Camera getCamera() {
        return mCamera;
    }

    protected final void setCamera(final Camera camera) {
        mCamera = camera;
    }

    @Override
    public int getNumberOfCameras() {
        return 1;
    }

    protected final void setCameraId(final int cameraId) {
        mCameraId = cameraId;
    }

    @Override
    public final int getCameraId() {
        return mCameraId;
    }

    @Override
    public CameraInfoCompat getCameraInfo() {
        final CameraInfoCompat result = new CameraInfoCompat();
        result.facing = CameraInfoCompat.CAMERA_FACING_BACK;
        result.orientation = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 90 : 0;
        return result;
    }

    @Override
    public final boolean isFaceCamera() {
        return getCameraInfo().facing == CameraInfoCompat.CAMERA_FACING_FRONT;
    }

    @Override
    public final boolean isOpened() {
        return mCamera != null;
    }

    @Override
    public void openCamera(final int cameraId) {
        releaseCamera();

        if (cameraId != DEFAULT_CAMERA_ID) {
            throw new RuntimeException();
        }
        mCamera = Camera.open();
        setCameraId(cameraId);
        initializeFocusMode();
    }

    @Override
    public void nextCamera() {
        openCamera((mCameraId + 1) % getNumberOfCameras());
    }

    @Override
    public void initializeFocusMode() {
    }

    @Override
    public final void releaseCamera() {
//		synchronized (this) {
        if (mCamera != null) {
            stopPreview();
            mCamera.release();
            mCamera = null;
        }
//		}
    }

    @Override
    public void setErrorCallback(final Camera.ErrorCallback cb) {
        mCamera.setErrorCallback(cb);
    }

    @Override
    public final void setupOptimalPreviewSizeAndPictureSize(final int measureWidth, final int measureHeight, final int maxSize) {
        final List<Camera.Size> supportedPreviewSizes = getSupportedPreviewSizes();
        final List<Camera.Size> supportedPictureSizes = getSupportedPictureSizes();

        if (supportedPreviewSizes != null && supportedPictureSizes != null) {
            int width;
            int height;
            if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                width = measureHeight;
                height = measureWidth;
            } else {
                width = measureWidth;
                height = measureHeight;
            }

            final Camera.Size pictureSize = supportedPictureSizes.get(0); //getOptimalSize(supportedPictureSizes, width, height, maxSize);
            if (pictureSize != null) {
                width = pictureSize.width;
                height = pictureSize.height;
            }
            final Camera.Size previewSize = getOptimalSize(supportedPreviewSizes, measureWidth, measureHeight, maxSize);

            if (previewSize != null && pictureSize != null) {
                final Camera.Parameters parameters = getCamera().getParameters();
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
                try {
                    getCamera().setParameters(parameters);
                } catch (final RuntimeException e) {}
            }
        }
    }

    private static final double ASPECT_TOLERANCE = 0.1D;

    private static Camera.Size getOptimalSize(final List<Camera.Size> sizes, final int width, final int height, final int maxSize) {
        if (sizes == null) {
            return null;
        }

        Camera.Size result = null;
        double minDiff = Double.MAX_VALUE;

        final double targetRatio = (double) width / (double) height;
        for (final Camera.Size size : sizes) {
            if (maxSize > 0 && (size.width > maxSize || size.height > maxSize)) {
                continue;
            }
            final double ratio = (double) size.width / (double) size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - height) < minDiff) {
                result = size;
                minDiff = Math.abs(size.height - height);
            }
        }

        if (result == null) {
            minDiff = Double.MAX_VALUE;
            for (final Camera.Size size : sizes) {
                if (maxSize > 0 && (size.width > maxSize || size.height > maxSize)) {
                    continue;
                }
                if (Math.abs(size.height - height) < minDiff) {
                    result = size;
                    minDiff = Math.abs(size.height - height);
                }
            }
        }

        return result;
    }

    @Override
    public int getOrientation() {
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 90;
        }
        return 0;
    }

    @Override
    public int getOptimalOrientation() {
        return getOrientation();
    }

    @Override
    public void setDisplayOrientation(final int degrees) {
        final Camera.Parameters params = getCamera().getParameters();
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.set("orientation", "portrait");
        } else {
            params.set("orientation", "landscape");
        }
        params.set("rotation", degrees);
        try {
            getCamera().setParameters(params);
        } catch (final RuntimeException e) {
            // 無視する
        }
    }


    @Override
    public void setPreviewCallback(final Camera.PreviewCallback cb) {
        mCamera.setPreviewCallback(cb);
    }

    @Override
    public void setPreviewDisplay(final SurfaceHolder holder) throws IOException {
        mCamera.setPreviewDisplay(holder);
    }

   @Override
    public void setPreviewTexture(final Object surfaceTexture) throws IOException {
        throw new IOException("setPreviewTexture not supported");
    }


    @Override
    public void startPreview() {
        mCamera.startPreview();
    }


    @Override
    public void onPreviewFrame(final Camera.PreviewCallback cb) {
    }

    @Override
    public final void stopPreview() {
        synchronized (this) {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                // stop preview before making changes
                try {
                    mCamera.stopPreview();
                } catch (final Exception e) {}	// ignore: tried to stop a non-existent preview
            }
        }
    }


    protected final void setPictureCallback(final Camera.PictureCallback callback) {
        mPictureCallback = callback;
    }

    @Override
    public final void takePicture(final Camera.PictureCallback callback) {
        takePicture(callback, true);
    }

    protected final Camera.ShutterCallback mNoopShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // NOP
        }
    };

    @Override
    public void takePicture(final Camera.PictureCallback callback, final boolean autoFocus) {
        setPictureCallback(callback);

        mCamera.setPreviewCallback(null);

          System.gc();

        mCamera.takePicture(mEnableShutterSound ? mNoopShutterCallback : null, null, this);
    }
  @Override public void cancelAutoFocus() {}

    protected boolean mEnableShutterSound = true;

    @Override
    public boolean enableShutterSound(final boolean enabled) {
        mEnableShutterSound = enabled;
        return true;
    }


    @Override
    public final void onPictureTaken(final byte[] data, final Camera camera) {
        mPictureCallback.onPictureTaken(data, camera);
        mPictureCallback = null;
    }

    //////////////////////////////////////////////////////////////////////////

    protected final Camera.Parameters getParameters() {
        return mCamera.getParameters();
    }

    public static final class CameraSizeComparator implements Comparator<Camera.Size> {

        private static final int LOW = 1;
        private static final int HIGH = -1;
        private static final int EQUAL = 0;

        @Override
        public int compare(final Camera.Size lhs, final Camera.Size rhs) {
            if (lhs == null && rhs == null) {
                return EQUAL;
            }
            if (lhs == null) {
                return LOW;
            }
            if (rhs == null) {
                return HIGH;
            }

            final int lhsSize = lhs.width * lhs.height;
            final int rhsSize = rhs.width * rhs.height;
            if (lhsSize < rhsSize) {
                return LOW;
            } else if (lhsSize > rhsSize) {
                return HIGH;
            }
            return EQUAL;
        }

    }

    @Override
    public LinkedHashMap<Camera.Size, Camera.Size> getSupportedPreviewSizeAndSupportedPictureSizeMap() {
        final List<Camera.Size> previewSizes = getSupportedPreviewSizes();
        final List<Camera.Size> pictureSizes = getSupportedPictureSizes();
        if (previewSizes == null || pictureSizes == null) {
            return null;
        }

        final LinkedHashMap<Camera.Size, Camera.Size> results = new LinkedHashMap<Camera.Size, Camera.Size>();

        for (final Camera.Size previewSize : previewSizes) {
            final double previewRatio = (double) previewSize.width / (double) previewSize.height;
            for (final Camera.Size pictureSize : pictureSizes) {
                final double pictureRatio = (double) pictureSize.width / (double) pictureSize.height;
                if (Math.abs(previewRatio - pictureRatio) == 0D) {
                    results.put(previewSize, pictureSize);
                    break;
                }

            }
        }

        if (results.isEmpty()) {
            return null;
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Camera.Size> getSupportedPreviewSizes() {
        final Method method;
        try {
            method = Camera.Parameters.class.getMethod("getSupportedPreviewSizes", new Class[]{});
        } catch (final NoSuchMethodException e) {
            return null;
        }
        try {
            final List<Camera.Size> results = (List<Camera.Size>) method.invoke(mCamera.getParameters());
            Collections.sort(results, new CameraSizeComparator());
            return results;
        } catch (final InvocationTargetException e) {
        } catch (final IllegalAccessException e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Camera.Size> getSupportedPictureSizes() {
        final Method method;
        try {
            method = Camera.Parameters.class.getMethod("getSupportedPictureSizes", new Class[]{});
        } catch (final NoSuchMethodException e) {
            return null;
        }
        try {
            final List<Camera.Size> results = (List<Camera.Size>) method.invoke(mCamera.getParameters());
            Collections.sort(results, new CameraSizeComparator());
            return results;
        } catch (final InvocationTargetException e) {
        } catch (final IllegalAccessException e) {
        }
        return null;
    }

    @Override
    public final Size getPreviewSize() {
        return mCamera.getParameters().getPreviewSize();
    }

    @Override
    public final Size getPictureSize() {
        return mCamera.getParameters().getPictureSize();
    }

    @Override
    public final void setPictureFormat(final int format) {
        final Camera.Parameters params = mCamera.getParameters();
        params.setPictureFormat(format);
        try {
            mCamera.setParameters(params);
        } catch (final RuntimeException e) {
        }
    }

    @Override public String getAntibanding() { return null; }
    @Override public String getColorEffect() { return null; }
    @Override public String getFlashMode() { return null; }
    @Override public String getFocusMode() { return null; }
    @Override public String getSceneMode() { return null; }
    @Override public String getWhiteBalance() { return null; }

    @Override public List<String> getSupportedAntibanding() { return null; }
    @Override public List<String> getSupportedColorEffects() { return null; }
    @Override public List<String> getSupportedFlashModes() { return null; }
    @Override public List<String> getSupportedFocusModes() { return null; }
    @Override public List<String> getSupportedSceneModes() { return null; }
    @Override public List<String> getSupportedWhiteBalance() { return null; }
    @Override public List<String> getSupportedAntibanding(final String... values) { return null; }
    @Override public List<String> getSupportedColorEffects(final String... values) { return null; }
    @Override public List<String> getSupportedFlashModes(final String... values) { return null; }
    @Override public List<String> getSupportedFocusModes(final String... values) { return null; }
    @Override public List<String> getSupportedSceneModes(final String... values) { return null; }
    @Override public List<String> getSupportedWhiteBalance(final String... values) { return null; }

    @Override public void setAntibanding(final String antibanding) {}
    @Override public void setColorEffect(final String value) {}
    @Override public void setFlashMode(final String value) {}
    @Override public void setFocusMode(final String value) {}
    @Override public void setSceneMode(final String value) {}
    @Override public void setWhiteBalance(final String value) {}
    @Override public String switchAntibanding() { return null; }
    @Override public String switchColorEffect() { return null; }
    @Override public String switchFlashMode() { return null; }
    @Override public String switchFocusMode() { return null; }
    @Override public String switchSceneMode() { return null; }
    @Override public String switchWhiteBalance() { return null; }
    @Override public String switchAntibanding(final String... values) { return null; }
    @Override public String switchColorEffect(final String... values) { return null; }
    @Override public String switchFlashMode(final String... values) { return null; }
    @Override public String switchFocusMode(final String... values) { return null; }
    @Override public String switchSceneMode(final String... values) { return null; }
    @Override public String switchWhiteBalance(final String... values) { return null; }
    @Override
    public final boolean isExposureCompensationSupported() {
        return getMinExposureCompensation() != 0 && getMaxExposureCompensation() != 0;
    }

    @Override public int getMaxExposureCompensation() { return 0; }
    @Override public int getMinExposureCompensation() { return 0; }
    @Override public float getExposureCompensationStep() { return 0; }
    @Override public int getExposureCompensation() { return 0; }
    @Override public void setExposureCompensation(final int value) {}

    @Override public boolean isZoomSupported() { return false; }
    @Override public int getMaxZoom() { return 0; }
    @Override public List<Integer> getZoomRatios() { return null; }
    @Override public int getZoom() { return 0; }
    @Override public void setZoom(final int value) {}
    @Override public void setZoomChangeListener(final OnZoomChangeListener listener) {}
    @Override public void startSmoothZoom(final int value) {}
    @Override public void stopSmoothZoom() {}

    @Override public int getMaxNumFocusAreas() { return 0; }
    @Override public List<AreaCompat> getFocusAreas() { return null; }
    @Override public void setFocusAreas(final AreaCompat... focusAreas) {}
    @Override public void setFocusAreas(final List<AreaCompat> focusAreas) {}
    @Override public int getMaxNumMeteringAreas() { return 0; }
    @Override public List<AreaCompat> getMeteringAreas() { return null; }
    @Override public void setMeteringAreas(final AreaCompat... meteringAreas) {}
    @Override public void setMeteringAreas(final List<AreaCompat> meteringAreas) {}
    @Override public boolean isAutoExposureLockSupported() { return false; }
    @Override public void setAutoExposureLock(final boolean toggle) {};
    @Override public boolean getAutoExposureLock() { return false; }

    @Override public boolean isAutoWhiteBalanceLockSupported() { return false; }
    @Override public void setAutoWhiteBalanceLock(final boolean toggle) {}
    @Override public boolean getAutoWhiteBalanceLock() { return false; }

    @Override public boolean isVideoSnapshotSupported() { return false; }

    @Override public boolean isVideoStabilizationSupported() { return false; }
    @Override public void setVideoStabilization(final boolean toggle) {}
    @Override public boolean getVideoStabilization() { return false; }

}
