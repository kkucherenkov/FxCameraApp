package com.af.experiments.FxCameraApp.shaders;

public class GlFalseColorShader extends GlShader {

    protected String mShaderName = "IR filter";

    @Override
    public String getName() {
        return mShaderName;
    }
    private static final String FRAGMENT_SHADER =
            "precision lowp float;" +

                    "varying vec2 vTextureCoord;" +
                    "uniform lowp sampler2D sTexture;" +
                    "const mediump vec4 firstColor = vec4(0.0, 0.0, 0.5, 1.0);" +
                    "const mediump vec4 secondColor = vec4(1.0, 0.0, 0.0, 1.0);" +
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
        //glUniform4fv(getHandle("firstColor"), 0, mFirstColor, 0);
        //glUniform4fv(getHandle("secondColor"), 0, mSecondColor, 0);
    }
}
