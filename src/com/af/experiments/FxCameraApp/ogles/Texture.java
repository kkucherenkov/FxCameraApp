package com.af.experiments.FxCameraApp.ogles;

public interface Texture {
    int getTexName();
    int getWidth();
    int getHeight();
    void setup();
    void release();
}