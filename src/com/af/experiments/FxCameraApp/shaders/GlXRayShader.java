package com.af.experiments.FxCameraApp.shaders;

public class GlXRayShader extends GlShader {
    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform lowp sampler2D sTexture;" +
                    "const highp vec3 weight = vec3(0.2125, 0.7154, 0.0721);" +
                    "void main() {" +
                    "lowp vec4 color = texture2D(sTexture, vTextureCoord);" +
                    "color = vec4((1.0 - color.rgb), color.w);" +
                    "float luminance = dot(color.rgb, weight);" +
                    "gl_FragColor = vec4(vec3(luminance), 1.0);" +
                    "}";

    public GlXRayShader() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }

    protected String mShaderName = "X Ray";

    @Override
    public String getName() {
        return mShaderName;
    }

}
