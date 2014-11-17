package com.af.experiments.FxCameraApp.shaders;

import android.util.Pair;
import com.af.experiments.FxCameraApp.renderer.GLES20FramebufferObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;


public class GlShaderGroup extends GlShader {

    private final Collection<GlShader> mShaders;

    private final ArrayList<Pair<GlShader, GLES20FramebufferObject>> mList = new ArrayList<Pair<GlShader, GLES20FramebufferObject>>();

    public GlShaderGroup(final GlShader... shaders) {
        this(Arrays.asList(shaders));
    }

    public GlShaderGroup(final Collection<GlShader> shaders) {
        mShaders = shaders;
    }


    @Override
    public void setup() {
        super.setup();

        if (mShaders != null) {
            final int max = mShaders.size();
            int count = 0;

            for (final GlShader shader : mShaders) {
                shader.setup();
                final GLES20FramebufferObject fbo;
                if ((count + 1) < max) {
                    fbo = new GLES20FramebufferObject();
                } else {
                    fbo = null;
                }
                mList.add(Pair.create(shader, fbo));
                count++;
            }
        }
    }

    @Override
    public void release() {
        for (final Pair<GlShader, GLES20FramebufferObject> pair : mList) {
            if (pair.first != null) {
                pair.first.release();
            }
            if (pair.second != null) {
                pair.second.release();
            }
        }
        mList.clear();
        super.release();
    }

    @Override
    public void setFrameSize(final int width, final int height) {
        super.setFrameSize(width, height);

        for (final Pair<GlShader, GLES20FramebufferObject> pair : mList) {
            if (pair.first != null) {
                pair.first.setFrameSize(width, height);
            }
            if (pair.second != null) {
                pair.second.setup(width, height);
            }
        }
    }

    private int mPrevTexName;

    @Override
    public void draw(final int texName, final GLES20FramebufferObject fbo) {
        mPrevTexName = texName;
        for (final Pair<GlShader, GLES20FramebufferObject> pair : mList) {
            if (pair.second != null) {
                if (pair.first != null) {
                    pair.second.enable();
                    glClear(GL_COLOR_BUFFER_BIT);

                    pair.first.draw(mPrevTexName, pair.second);
                }
                mPrevTexName = pair.second.getTexName();

            } else {
                if (fbo != null) {
                    fbo.enable();
                } else {
                    glBindFramebuffer(GL_FRAMEBUFFER, 0);
                }

                if (pair.first != null) {
                    pair.first.draw(mPrevTexName, fbo);
                }
            }
        }
    }

}