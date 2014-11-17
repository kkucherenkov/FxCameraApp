package com.af.experiments.FxCameraApp.ogles;

import android.graphics.Bitmap;
import com.af.experiments.FxCameraApp.Utils.OpenGlUtils;
import com.af.experiments.FxCameraApp.renderer.GLES20FramebufferObject;
import com.af.experiments.FxCameraApp.shaders.GlShader;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_BINDING;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NEAREST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_BINDING_2D;
import static android.opengl.GLES20.GL_VIEWPORT;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glViewport;

public abstract class GlImageTexture implements Texture {

    private static final class GLES20FlipVerticalShader extends GlShader {

        private static final float[] VERTICES_DATA = new float[] {
                // X, Y, Z, U, V
                -1.0f,  1.0f, 0.0f, 0.0f, 0.0f,
                1.0f,  1.0f, 0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 1.0f
        };

        @Override
        public void setup() {
            super.setup();
            OpenGlUtils.updateBufferData(getVertexBufferName(), VERTICES_DATA);
        }

    };

    private final GLES20FramebufferObject mFramebufferObject = new GLES20FramebufferObject();

    protected final void attachToTexture(final Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap must not be  null");
        }
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("Bitmap is recycled");
        }

        final int[] saveFramebuffer = new int[1];
        glGetIntegerv(GL_FRAMEBUFFER_BINDING, saveFramebuffer, 0);
        final int[] saveViewport = new int[4];
        glGetIntegerv(GL_VIEWPORT, saveViewport, 0);
        final int[] saveTexName = new int[1];
        glGetIntegerv(GL_TEXTURE_BINDING_2D, saveTexName, 0);

        final GLES20FlipVerticalShader shader = new GLES20FlipVerticalShader();
        final int[] textures = new int[1];
        try {
            glGenTextures(textures.length, textures, 0);
            glBindTexture(GL_TEXTURE_2D, textures[0]);
            OpenGlUtils.setupSampler(GL_TEXTURE_2D, GL_LINEAR, GL_NEAREST);

            OpenGlUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

            mFramebufferObject.setup(bitmap.getWidth(), bitmap.getHeight());
            shader.setup();
            shader.setFrameSize(mFramebufferObject.getWidth(), mFramebufferObject.getHeight());

            mFramebufferObject.enable();
            glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());
            glClear(GL_COLOR_BUFFER_BIT);

            shader.draw(textures[0], null);
        } catch (final RuntimeException e) {
            mFramebufferObject.release();
            throw e;
        } finally {
            glDeleteTextures(textures.length, textures, 0);
            shader.release();

            glBindFramebuffer(GL_FRAMEBUFFER, saveFramebuffer[0]);
            glViewport(saveViewport[0], saveViewport[1], saveViewport[2], saveViewport[3]);
            glBindTexture(GL_TEXTURE_2D, saveTexName[0]);
        }
    }

    @Override
    public void release() {
        mFramebufferObject.release();
    }

    @Override
    public int getTexName() {
        return mFramebufferObject.getTexName();
    }

    @Override
    public int getWidth() {
        return mFramebufferObject.getWidth();
    }

    @Override
    public int getHeight() {
        return mFramebufferObject.getHeight();
    }

}
