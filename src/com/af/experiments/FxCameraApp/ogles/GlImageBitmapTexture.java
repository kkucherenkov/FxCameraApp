package com.af.experiments.FxCameraApp.ogles;

import android.graphics.Bitmap;

public class GlImageBitmapTexture extends GlImageTexture {

    private Bitmap mBitmap;

    private final boolean mAutoRecycle;

    public GlImageBitmapTexture(final Bitmap bitmap) {
        this(bitmap, true);
    }

    public GlImageBitmapTexture(final Bitmap bitmap, final boolean autoRecycle) {
        mBitmap = bitmap;
        mAutoRecycle = autoRecycle;
    }

    public boolean isAutoRecycle() {
        return mAutoRecycle;
    }

    @Override
    public void setup() {
        attachToTexture(mBitmap);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mAutoRecycle) {
                dispose();
            }
        } finally {
            super.finalize();
        }
    }

    public void dispose() {
        if (mBitmap != null) {
            if (!mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mBitmap = null;
        }
    }

}
