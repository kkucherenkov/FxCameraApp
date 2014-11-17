package com.af.experiments.FxCameraApp.Utils;

import android.os.Handler;

public class Fps implements Runnable {

    public interface Callback {
        void onFps(final int fps);
    }

    Callback mCallback;

    private final Handler mHandler = new Handler();
    private final Runnable mCallbackRunner = new Runnable() {
        @Override
        public void run() {
            mCallback.onFps(mFrameCount);
            mFrameCount = 0;
        }
    };

    volatile int mFrameCount;

    private Thread mThread;

    public Fps(final Callback callback) {
        if (callback == null) {
            throw new NullPointerException("Callback must not be null");
        }
        mCallback = callback;
    }

    public void start() {
        synchronized (this) {
            stop();
            mFrameCount = 0;
            mThread = new Thread(this);
            mThread.start();
        }
    }

    public void stop() {
        synchronized (this) {
            mThread = null;
        }
    }

    public void countup() {
        mFrameCount++;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000L);

                synchronized (this) {
                    if (mThread == null || mThread != Thread.currentThread()) {
                        break;
                    }
                }

                mHandler.post(mCallbackRunner);
            } catch (final InterruptedException e) {
                break;
            }
        }
    }

}