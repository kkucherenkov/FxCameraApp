package com.af.experiments.FxCameraApp.ogles;

public class GLES20ContextFactory extends DefaultContextFactory {

    private static final int EGL_CONTEXT_CLIENT_VERSION = 2;

    public GLES20ContextFactory() {
        super(EGL_CONTEXT_CLIENT_VERSION);
    }

}