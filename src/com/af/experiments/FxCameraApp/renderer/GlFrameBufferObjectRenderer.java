package com.af.experiments.FxCameraApp.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.af.experiments.FxCameraApp.Utils.Fps;
import com.af.experiments.FxCameraApp.Utils.OpenGlUtils;
import com.af.experiments.FxCameraApp.shaders.GlShader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.LinkedList;
import java.util.Queue;

import static android.opengl.GLES20.*;


public abstract class GlFrameBufferObjectRenderer implements GLSurfaceView.Renderer {

    private GLES20FramebufferObject mFramebufferObject;
    private GlShader mShader;

    private final Queue<Runnable> mRunOnDraw;

    private Fps mFps;

    protected GlFrameBufferObjectRenderer() {
        mRunOnDraw = new LinkedList<Runnable>();
    }


    public void setFps(final Fps fps) {
        if (mFps != null) {
            mFps.stop();
            mFps = null;
        }
        mFps = fps;
    }

    public Bitmap getBitmap() {
        return mFramebufferObject.getBitmap();
    }

    public Bitmap getBitmap(final int orientation) {
        return mFramebufferObject.getBitmap(orientation);
    }

    public Bitmap getBitmap(final int orientation, final boolean mirror) {
        return mFramebufferObject.getBitmap(orientation, mirror);
    }

    @Override
    public final void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        mFramebufferObject = new GLES20FramebufferObject();
        mShader = new GlShader();
        mShader.setup();
        onSurfaceCreated(config);
        if (mFps != null) {
            mFps.start();
        }
    }

    @Override
    public final void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        mFramebufferObject.setup(width, height);
        mShader.setFrameSize(width, height);
        onSurfaceChanged(width, height);
    }

    @Override
    public final void onDrawFrame(final GL10 gl) {
        synchronized (mRunOnDraw) {
            while (!mRunOnDraw.isEmpty()) {
                mRunOnDraw.poll().run();
            }
        }
        mFramebufferObject.enable();
        glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

        onDrawFrame(mFramebufferObject);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        mShader.draw(mFramebufferObject.getTexName(), null);

        if (mFps != null) {
            mFps.countup();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (mFps != null) {
                mFps.stop();
                mFps = null;
            }
        } finally {
            super.finalize();
        }
    }

    public abstract void onSurfaceCreated(EGLConfig config);

    public abstract void onSurfaceChanged(int width, int height);

    public abstract void onDrawFrame(GLES20FramebufferObject fbo);

    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }
    private int mGLTextureId = OpenGlUtils.NO_TEXTURE;

    public void setImageBitmap(final Bitmap bitmap, boolean b) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                mGLTextureId = OpenGlUtils.loadTexture(bitmap, mGLTextureId, false);

                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        });
    }

    public void deleteImage() {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                try {
                    GLES20.glDeleteTextures(1, new int[]{mGLTextureId}, 0);
                    mGLTextureId = -1;
                }catch (Exception e){
                    Log.d("DEBUG", "", e);
                }
            }
        });
    }
}

