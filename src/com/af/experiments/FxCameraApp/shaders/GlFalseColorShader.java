package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform3fv;

public class GlFalseColorShader extends GlShader {
    private static final String FRAGMENT_SHADER =
            "precision lowp float;" +

                    "varying vec2 vTextureCoord;" +
                    "uniform lowp sampler2D sTexture;" +
                    "uniform vec3 firstColor;" +
                    "uniform vec3 secondColor;" +

                    "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);" +

                    "void main() {" +
                    "lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
                    "float luminance = dot(color.rgb, luminanceWeighting);" +
                    "gl_FragColor = vec4(mix(firstColor.rgb, secondColor.rgb, luminance), color.a);" +
                    "}";

    private float[] mFirstColor = new float[]{0.0f, 0.0f, 0.5f, 1.0f};
    private float[] mSecondColor = new float[]{1.0f, 0.0f, 0.0f, 1.0f};

    public GlFalseColorShader() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public float[] getFirstColor() {
        return mFirstColor;
    }

    public void setFirstColor(final float[] firstColor) {
        mFirstColor = firstColor;
    }

    public float[] getSecondColor() {
        return mSecondColor;
    }

    public void setSecondColor(final float[] secondColor) {
        mSecondColor = secondColor;
    }

    @Override
    public void onDraw() {
        glUniform3fv(getHandle("firstColor"), 0, mFirstColor, 0);
        glUniform3fv(getHandle("secondColor"), 0, mSecondColor, 0);
    }
}
