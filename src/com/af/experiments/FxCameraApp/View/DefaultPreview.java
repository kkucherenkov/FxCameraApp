package com.af.experiments.FxCameraApp.View;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.af.experiments.FxCameraApp.camera.CameraHelper;

import java.io.IOException;

public class DefaultPreview extends SurfaceView implements CameraView.Preview, Camera.PictureCallback, GestureDetector.OnGestureListener {

    private static final String TAG = "DefaultPreview";

    private CameraHelper mCameraHelper;

    private GestureDetector mGestureDetector;

    public DefaultPreview(final Context context) {
        super(context);
        initialize(context);
    }

    public DefaultPreview(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public DefaultPreview(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    @SuppressWarnings("deprecation")
    private void initialize(final Context context) {
        mGestureDetector = new GestureDetector(context, this);

        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void setCameraHelper(final CameraHelper helper) {
        mCameraHelper = helper;
    }

    @Override
    public boolean isSquareFrameSupported() {
        return false;
    }

    @Override
    public void onOpenCamera() {
    }

    @Override
    public void onReleaseCamera() {
    }

    @Override
    public void startPreview(final int measurePreviewWidth, final int measurePreviewHeight, final CameraView.CameraStateListener listener) {
        if (measurePreviewWidth > 0 && measurePreviewHeight > 0) {
            mCameraHelper.setupOptimalPreviewSizeAndPictureSize(measurePreviewWidth, measurePreviewHeight, 0);
        }
        requestLayout();

        mCameraHelper.setDisplayOrientation(mCameraHelper.getOptimalOrientation());
        try {
            mCameraHelper.setPreviewDisplay(getHolder());
        } catch (IOException e) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", e);
            throw new IllegalStateException(e.getMessage(), e);
        }
        mCameraHelper.startPreview();

        if (listener != null) {
            listener.onStartPreview();
        }
    }

    @Override
    public void onStopPreview() {
    }

    private CameraView.CaptureCallback mCaptureCallback;

    @Override
    public void takePicture(final CameraView.CaptureCallback callback) {
        takePicture(callback, true);
    }

    @Override
    public void takePicture(final CameraView.CaptureCallback callback, final boolean autoFocus) {
        mCaptureCallback = callback;
        mCameraHelper.takePicture(this, autoFocus);
    }

    @Override
    public void onPictureTaken(final byte[] data, final Camera camera) {
        mCameraHelper.stopPreview();

        final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (!mCaptureCallback.onImageCapture(bitmap) && bitmap != null) {
            bitmap.recycle();
        }
        mCaptureCallback = null;
    }


    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    @Override
    public boolean onDown(final MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(final MotionEvent e) {
        onTap(e);
    }

    @Override
    public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(final MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(final MotionEvent e) {
        return onTap(e);
    }

    //////////////////////////////////////////////////////////////////////////

    private static final int AREA_SIZE = 2000;
    private static final int AREA_HALF_SIZE = AREA_SIZE / 2;
    private static final int DEFAULT_AREA_WEIGHT = 1000;

    private boolean onTap(final MotionEvent event) {
        boolean result = false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            final double x = event.getX() / getWidth()  * AREA_SIZE - AREA_HALF_SIZE;
            final double y = event.getY() / getHeight() * AREA_SIZE - AREA_HALF_SIZE;

            final int angle = mCameraHelper.getOrientation() * -1;
            final int x2 = Math.min(Math.max((int) Math.round(x * Math.cos(angle) - y * Math.sin(angle)), -AREA_HALF_SIZE), AREA_HALF_SIZE);
            final int y2 = Math.min(Math.max((int) Math.round(x * Math.sin(angle) + y * Math.cos(angle)), -AREA_HALF_SIZE), AREA_HALF_SIZE);
            final int size = (int) Math.max(event.getSize() / 2, 10);

            final CameraHelper.AreaCompat area = new CameraHelper.AreaCompat(new Rect(
                    Math.max(x2 - size, -AREA_HALF_SIZE),
                    Math.max(y2 - size, -AREA_HALF_SIZE),
                    Math.min(x2 + size,  AREA_HALF_SIZE),
                    Math.min(y2 + size,  AREA_HALF_SIZE)
            ), DEFAULT_AREA_WEIGHT);

            if (mCameraHelper.getMaxNumFocusAreas() > 0) {
                mCameraHelper.setFocusAreas(area);
                result = true;
            }
            if (mCameraHelper.getMaxNumMeteringAreas() > 0) {
                mCameraHelper.setMeteringAreas(area);
                result = true;
            }
        }

        return result;
    }
}