package com.af.experiments.FxCameraApp.View;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.af.experiments.FxCameraApp.camera.CameraHelper;
import com.af.experiments.FxCameraApp.camera.CameraHelperFactory;
import com.af.experiments.FxCameraApp.display.DisplayHelperFactory;
import com.af.experiments.FxCameraApp.ogles.PreviewSurfaceHelper;
import com.af.experiments.FxCameraApp.ogles.PreviewSurfaceHelperFactory;

import java.io.IOException;

public class CameraView extends ViewGroup implements SurfaceHolder.Callback {

    public interface Preview {
        void setCameraHelper(CameraHelper helper);
        boolean isSquareFrameSupported();

        void onOpenCamera();

        void onReleaseCamera();

        void startPreview(int measurePreviewWidth, int measurePreviewHeight, CameraStateListener l);

        void onStopPreview();

        void takePicture(CaptureCallback callback);

        void takePicture(CaptureCallback callback, boolean autoFocus);

    }

    public static enum PreviewSizePolicy {
        DISPLAY,
        VIEW,
        PREVIEW,
        MANUAL
    }

    public interface CameraStateListener {
        void onOpenCamera();
        void onStartPreview();
        void onReleaseCamera();

    }

    public interface OnErrorListener {
        static final int ERROR_UNKNOWN = -1;
        static final int ERROR_CAMERA_INITIAL_OPEN =  0;
        void onError(int error, Exception e, CameraView view);

    }

    public interface CaptureCallback {
        boolean onImageCapture(Bitmap bitmap);

    }

    private static final String TAG = "CameraView";
    private CameraHelper mCameraHelper;
    private PreviewSurfaceHelper mPreviewSurfaceHelper;

    private Preview mPreview;

    private boolean mUsePreviewCallback;

    private SurfaceView mPushBufferSurface;

    private boolean mAutoStart = true;

    private boolean mPreviewAlignCenter;

    private boolean mSquareFrame;

    private View mOverlayView;

    private PreviewSizePolicy mPreviewSizePolicy = PreviewSizePolicy.DISPLAY;

    private CameraStateListener mCameraStateListener;

    private OnErrorListener mOnErrorListener;

    public CameraView(final Context context) {
        super(context);
        initialize(context);
    }

    public CameraView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public CameraView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(final Context context) {
        mCameraHelper = CameraHelperFactory.newCameraHelper(context);
        mPreviewSurfaceHelper = PreviewSurfaceHelperFactory.newPreviewSurfaceHelper(mCameraHelper);

        setPreview(new DefaultPreview(context), false);
        //setPreview(new GlPreview(context));
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {

        final int width = r - l;
        final int height = b - t;
        final int count = getChildCount();

        View preview = null;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null) {
                if (child.equals(mPreview)) {
                    int childWidth = width - getPaddingLeft() - getPaddingRight();
                    int childHeight = height - getPaddingTop() - getPaddingBottom();;

                    if (mPreview.isSquareFrameSupported() && mSquareFrame) {
                        final int size = Math.min(childWidth, childHeight);
                        childWidth = size;
                        childHeight = size;
                    } else if (mCameraHelper.isOpened()) {
                        final Camera.Size previewSize = mCameraHelper.getPreviewSize();
                        if (previewSize != null) {
                            final int previewWidth;
                            final int previewHeight;
                            switch (getResources().getConfiguration().orientation) {
                                case Configuration.ORIENTATION_PORTRAIT:
                                    previewWidth = previewSize.height;
                                    previewHeight = previewSize.width;
                                    break;
                                default:
                                    previewWidth = previewSize.width;
                                    previewHeight = previewSize.height;
                                    break;
                            }
                            final double scale = Math.min((double) childWidth / (double) previewWidth, (double) childHeight / (double) previewHeight);
                            childWidth = (int) Math.floor(previewWidth * scale);
                            childHeight = (int) Math.floor(previewHeight * scale);
                        }
                    }

                    final int childLeft;
                    final int childTop;
                    if (mPreviewAlignCenter) {
                        childLeft = (width - childWidth) / 2;
                        childTop = (height - childHeight) / 2;
                    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        childLeft = (width - childWidth) / 2;
                        childTop = 0;
                    } else {
                        childLeft = 0;
                        childTop = (height - childHeight) / 2;
                    }
                    child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

                    preview = child;
                    break;
                }
            }
        }
        if (preview != null) {
            if (mPushBufferSurface != null) {
                mPushBufferSurface.layout(preview.getLeft(), preview.getTop(), preview.getRight(), preview.getBottom());
            }
            if (mOverlayView != null) {
                mOverlayView.layout(preview.getLeft(), preview.getTop(), preview.getRight(), preview.getBottom());
            }
        }
    }

    @Override
    public void removeAllViews() {
        mOverlayView = null;
        super.removeAllViews();
    }

    @Override
    public void removeView(final View view) {
        if (mOverlayView != null && mOverlayView.equals(view)) {
            mOverlayView = null;
        }
        super.removeView(view);
    }

    @Override
    public void removeViewAt(final int index) {
        final View view = getChildAt(index);
        if (mOverlayView != null && mOverlayView.equals(view)) {
            mOverlayView = null;
        }
        super.removeViewAt(index);
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        try {
            openCamera(mCameraHelper.getCameraId());
        } catch (final RuntimeException e) {
            if (mOnErrorListener != null) {
                mOnErrorListener.onError(OnErrorListener.ERROR_CAMERA_INITIAL_OPEN, e, this);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
        if (!mCameraHelper.isOpened()) {
            return;
        }

        if (mUsePreviewCallback) {
            try {
                mPreviewSurfaceHelper.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.e(TAG, "IOException caused by setPreviewDisplay()", e);
                throw new IllegalStateException(e.getMessage(), e);
            }
        }

        if (mAutoStart) {
            startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        releaseCamera();
    }

    public CameraHelper getCameraHelper() {
        return mCameraHelper;
    }

    public Preview getPreview() {
        return mPreview;
    }

    public void setPreview(final Preview preview) {
        setPreview(preview, preview != null && !(preview instanceof DefaultPreview));
    }

    public void setPreview(final Preview preview, final boolean usePreviewCallback) {
        removePreview();

        mUsePreviewCallback = usePreviewCallback;

        if (preview != null) {
            if (preview instanceof SurfaceView) {
                final SurfaceView surface = (SurfaceView) preview;

                if (usePreviewCallback) {
                    mPushBufferSurface = mPreviewSurfaceHelper.createPushBufferSurfaceViewIfNeed(getContext());
                }

                if (mPushBufferSurface != null) {
                    mPushBufferSurface.getHolder().addCallback(this);
                    mPreviewSurfaceHelper.setZOrderMediaOverlay(surface);
                } else {
                    surface.getHolder().addCallback(this);
                }

                addView(surface, 0);

                if (mPushBufferSurface != null) {
                    addView(mPushBufferSurface, 0);
                }
            } else {
                //throw new IllegalArgumentException();
            }
            mPreview = preview;
            mPreview.setCameraHelper(mCameraHelper);
        }
    }

    public void removePreview() {
        if (mPreview != null) {
            if (mPreview instanceof SurfaceView) {
                final SurfaceView surface = (SurfaceView) mPreview;
                surface.getHolder().removeCallback(this);
            }
            if (mPreview instanceof View) {
                final View view = (View) mPreview;
                removeView(view);
            }
            mPreview.setCameraHelper(null);
            mPreview = null;
        }
        if (mPushBufferSurface != null) {
            mPushBufferSurface.getHolder().removeCallback(this);
            removeView(mPushBufferSurface);
            mPushBufferSurface = null;
        }
        mUsePreviewCallback = false;
    }

    public boolean isAutoStart() {
        return mAutoStart;
    }

    public void setAutoStart(final boolean autoStart) {
        mAutoStart = autoStart;
    }

    public boolean isPreviewAlignCenter() {
        return mPreviewAlignCenter;
    }

    public void setPreviewAlignCenter(final boolean previewAlignCenter) {
        mPreviewAlignCenter = previewAlignCenter;
        requestLayout();
    }

    public boolean isSquareFrame() {
        return mSquareFrame;
    }

    public void setSquareFrame(final boolean square) {
        if (mSquareFrame != square) {
            mSquareFrame = square;
            requestLayout();
        }
    }

    public void setOverlayView(final View child) {
        if (mOverlayView != null) {
            removeView(mOverlayView);
        }
        mOverlayView = child;
        if (mOverlayView != null) {
            addView(child);
        }
    }

    public View getOverlayView() {
        return mOverlayView;
    }

    public PreviewSizePolicy getPreviewSizePolicy() {
        return mPreviewSizePolicy;
    }

    public void setPreviewSizePolicy(final PreviewSizePolicy previewSizePolicy) {
        if (previewSizePolicy == null) {
            throw new IllegalArgumentException("PreviewSizePolicy must not be null");
        }
        mPreviewSizePolicy = previewSizePolicy;
    }

    public void setCameraStateListener(final CameraStateListener callback) {
        mCameraStateListener = callback;
    }

    public void setOnErrorListener(final OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void switchCamera(final int cameraId) {
        openCamera(cameraId);
        startPreview();
    }

    private void openCamera(final int cameraId) {
        synchronized (this) {
            mCameraHelper.openCamera(cameraId);
            mPreview.onOpenCamera();

            if (mCameraStateListener != null) {
                mCameraStateListener.onOpenCamera();
            }
        }
    }

    private void releaseCamera() {
        stopPreview();

        synchronized (this) {
            if (mCameraStateListener != null) {
                mCameraStateListener.onReleaseCamera();
            }
            mCameraHelper.releaseCamera();
            mPreview.onReleaseCamera();
        }
    }

    public void startPreview() {
        stopPreview();

        synchronized (this) {
            if (mCameraHelper.isOpened()) {
                int width;
                int height;
                switch (mPreviewSizePolicy) {
                    case DISPLAY: {
                        final Point point = DisplayHelperFactory.newDisplayHelper(getContext()).getRawDisplaySize();
                        width = point.x;
                        height = point.y;
                    } break;

                    case VIEW: {
                        width = getWidth();
                        height = getHeight();
                    } break;

                    case PREVIEW: {
                        final View view = (View) mPreview;
                        width = view.getWidth();
                        height = view.getHeight();
                    } break;

                    case MANUAL: {
                        width = 0;
                        height = 0;
                    } break;

                    default:
                        throw new IllegalStateException("Unsupported PreviewSizePolicy type " + mPreviewSizePolicy.toString());
                }
                mPreview.startPreview(width, height, mCameraStateListener);
            }
        }
    }

    public void stopPreview() {
        synchronized (this) {
            mCameraHelper.stopPreview();
            mPreview.onStopPreview();
        }
    }

    public void capture(final CameraView.CaptureCallback callback) {
        capture(callback, true);
    }

    public void capture(final CameraView.CaptureCallback callback, final boolean autoFocus) {
        mPreview.takePicture(callback, autoFocus);
    }
}