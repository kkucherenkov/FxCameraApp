package com.af.experiments.FxCameraApp.ogles;

public class GLES20ConfigChooser extends DefaultConfigChooser {

    private static final int EGL_CONTEXT_CLIENT_VERSION = 2;

    public GLES20ConfigChooser() {
        super(EGL_CONTEXT_CLIENT_VERSION);
    }

    public GLES20ConfigChooser(final boolean withDepthBuffer) {
        super(withDepthBuffer, EGL_CONTEXT_CLIENT_VERSION);
    }

    public GLES20ConfigChooser(final int redSize, final int greenSize, final int blueSize, final int alphaSize, final int depthSize, final int stencilSize) {
        super(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize, EGL_CONTEXT_CLIENT_VERSION);
    }

}