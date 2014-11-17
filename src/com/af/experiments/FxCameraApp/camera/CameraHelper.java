package com.af.experiments.FxCameraApp.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public interface CameraHelper {
    static final int DEFAULT_CAMERA_ID = 0;

    int getNumberOfCameras();

    int getCameraId();

    public static class CameraInfoCompat {
        public static final int CAMERA_FACING_BACK = 0;
        public static final int CAMERA_FACING_FRONT = 1;

        public int facing;

        public int orientation;

    }

    CameraInfoCompat getCameraInfo();

    boolean isFaceCamera();

    boolean isOpened();

    void openCamera(int cameraId);

    void nextCamera();

    void initializeFocusMode();

    void releaseCamera();

    void setErrorCallback(Camera.ErrorCallback cb);

    void setupOptimalPreviewSizeAndPictureSize(int measureWidth, int measureHeight, int maxSize);

    int getOptimalOrientation();

    int getOrientation();

    void setDisplayOrientation(int degrees);

    void setPreviewCallback(Camera.PreviewCallback cb);

    void setPreviewDisplay(SurfaceHolder holder) throws IOException;

    void setPreviewTexture(Object surfaceTexture) throws IOException;

    void startPreview();

    void onPreviewFrame(Camera.PreviewCallback cb);

    void stopPreview();

    void takePicture(Camera.PictureCallback callback);

    void takePicture(Camera.PictureCallback callback, boolean autoFocus);

    void cancelAutoFocus();

    boolean enableShutterSound(boolean enabled);

    LinkedHashMap<Camera.Size, Camera.Size> getSupportedPreviewSizeAndSupportedPictureSizeMap();

    List<Camera.Size> getSupportedPreviewSizes();

    List<Camera.Size> getSupportedPictureSizes();

    Camera.Size getPreviewSize();

    Camera.Size getPictureSize();

    void setPictureFormat(int format);

    String getAntibanding();

    String getColorEffect();

    String getFlashMode();

    String getFocusMode();

    String getSceneMode();

    String getWhiteBalance();

    List<String> getSupportedAntibanding();

    List<String> getSupportedColorEffects();

    List<String> getSupportedFlashModes();

    List<String> getSupportedFocusModes();

    List<String> getSupportedSceneModes();

    List<String> getSupportedWhiteBalance();

    List<String> getSupportedAntibanding(String... values);

    List<String> getSupportedColorEffects(String... values);

    List<String> getSupportedFlashModes(String... values);

    List<String> getSupportedFocusModes(String... values);

    List<String> getSupportedSceneModes(String... values);

    List<String> getSupportedWhiteBalance(String... values);

    void setAntibanding(String antibanding);

    void setColorEffect(String value);

    void setFlashMode(String value);

    void setFocusMode(String value);

    void setSceneMode(String value);

    void setWhiteBalance(String value);

    String switchAntibanding();

    String switchColorEffect();

    String switchFlashMode();

    String switchFocusMode();

    String switchSceneMode();

    String switchWhiteBalance();

    String switchAntibanding(String... values);

    String switchColorEffect(String... values);

    String switchFlashMode(String... values);

    String switchFocusMode(String... values);

    String switchSceneMode(String... values);

    String switchWhiteBalance(String... values);

    public boolean isExposureCompensationSupported();

    public int getMaxExposureCompensation();

    public int getMinExposureCompensation();

    public float getExposureCompensationStep();

    public int getExposureCompensation();

    public void setExposureCompensation(int value);

    public interface OnZoomChangeListener {
        void onZoomChange(int zoomValue, boolean stopped, CameraHelper camera);
    }

    boolean isZoomSupported();

    int getMaxZoom();

    List<Integer> getZoomRatios();

    int getZoom();

    void setZoom(int value);

    void setZoomChangeListener(OnZoomChangeListener listener);

    void startSmoothZoom(int value);

    void stopSmoothZoom();

    public interface FaceDetectionListener {
        void onFaceDetection(FaceCompat[] faces, CameraHelper camera);
    }

    public static class FaceCompat {
        public FaceCompat() {
        }


        public Rect rect;


        public int score;

        public int id = -1;
        public Point leftEye = null;

        public Point rightEye = null;

        public Point mouth = null;
    }


    public static class AreaCompat {

        public Rect rect;

        public int weight;

        public AreaCompat(final Rect rect, final int weight) {
            this.rect = rect;
            this.weight = weight;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof AreaCompat)) {
                return false;
            }
            AreaCompat a = (AreaCompat) obj;
            if (rect == null) {
                if (a.rect != null) return false;
            } else {
                if (!rect.equals(a.rect)) return false;
            }
            return weight == a.weight;
        }
    }

    int getMaxNumFocusAreas();

    List<AreaCompat> getFocusAreas();

    void setFocusAreas(AreaCompat... focusAreas);

    void setFocusAreas(List<AreaCompat> focusAreas);

    int getMaxNumMeteringAreas();

    List<AreaCompat> getMeteringAreas();

    void setMeteringAreas(AreaCompat... meteringAreas);

    void setMeteringAreas(List<AreaCompat> meteringAreas);

    boolean isAutoWhiteBalanceLockSupported();

    void setAutoWhiteBalanceLock(boolean toggle);

    boolean getAutoWhiteBalanceLock();

    boolean isAutoExposureLockSupported();

    void setAutoExposureLock(boolean toggle);

    boolean getAutoExposureLock();

    boolean isVideoSnapshotSupported();

    //////////////////////////////////////////////////////////////////////////
    // Video Stabilization

    boolean isVideoStabilizationSupported();

    void setVideoStabilization(boolean toggle);

    boolean getVideoStabilization();

}
