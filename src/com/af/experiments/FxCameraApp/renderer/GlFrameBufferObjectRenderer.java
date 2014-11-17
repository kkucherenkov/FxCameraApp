package com.af.experiments.FxCameraApp.renderer;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import com.af.experiments.FxCameraApp.Utils.Fps;
import com.af.experiments.FxCameraApp.shaders.GlShader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;


public abstract class GlFrameBufferObjectRenderer  implements GLSurfaceView.Renderer {

        private GLES20FramebufferObject mFramebufferObject;
        private GlShader mShader;

        private Fps mFps;


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

}

