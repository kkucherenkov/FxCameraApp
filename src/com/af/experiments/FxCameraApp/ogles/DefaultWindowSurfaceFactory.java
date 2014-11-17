package com.af.experiments.FxCameraApp.ogles;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class DefaultWindowSurfaceFactory implements GLSurfaceView.EGLWindowSurfaceFactory {
    private static final String TAG = "DefaultWindowSurfaceFactory";

    @Override
    public EGLSurface createWindowSurface(final EGL10 egl, final EGLDisplay display, final EGLConfig config, final Object nativeWindow) {
        try {
            return egl.eglCreateWindowSurface(display, config, nativeWindow, null);
        } catch (final IllegalArgumentException e) {
            Log.e(TAG, "eglCreateWindowSurface", e);
            return null;
        }
    }

    @Override
    public void destroySurface(final EGL10 egl, final EGLDisplay display, final EGLSurface surface) {
        egl.eglDestroySurface(display, surface);
    }

}