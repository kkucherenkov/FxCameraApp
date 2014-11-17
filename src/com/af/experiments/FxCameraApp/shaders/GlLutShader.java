package com.af.experiments.FxCameraApp.shaders;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import com.af.experiments.FxCameraApp.Utils.OpenGlUtils;
import com.af.experiments.FxCameraApp.ogles.GlImageBitmapTexture;

public class GlLutShader extends GlShader {
    private final static String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "uniform mediump sampler2D lutTexture; \n" +
                    "uniform lowp sampler2D sTexture; \n" +
                    "varying highp vec2 vTextureCoord; \n" +
                    "vec4 sampleAs3DTexture(vec3 uv, float width) {\n" +
                    "    float sliceSize = 1.0 / width;              // space of 1 slice\n" +
                    "    float slicePixelSize = sliceSize / width;           // space of 1 pixel\n" +
                    "    float sliceInnerSize = slicePixelSize * (width - 1.0);  // space of width pixels\n" +
                    "    float zSlice0 = min(floor(uv.z * width), width - 1.0);\n" +
                    "    float zSlice1 = min(zSlice0 + 1.0, width - 1.0);\n" +
                    "    float xOffset = slicePixelSize * 0.5 + uv.x * sliceInnerSize;\n" +
                    "    float s0 = xOffset + (zSlice0 * sliceSize);\n" +
                    "    float s1 = xOffset + (zSlice1 * sliceSize);\n" +
                    "    vec4 slice0Color = texture2D(lutTexture, vec2(s0, uv.y));\n" +
                    "    vec4 slice1Color = texture2D(lutTexture, vec2(s1, uv.y));\n" +
                    "    float zOffset = mod(uv.z * width, 1.0);\n" +
                    "    vec4 result = mix(slice0Color, slice1Color, zOffset);\n" +
                    "    return result;\n" +
                    "}\n" +
                    "void main() {\n" +
                    "   vec4 pixel = texture2D(sTexture, vTextureCoord);\n" +
                    "   vec4 gradedPixel = sampleAs3DTexture(pixel.rgb, 16.);\n" +
                    "   gradedPixel.a = pixel.a;\n" +
                    "   pixel = gradedPixel;\n" +
                    "   gl_FragColor = pixel;\n " +
                    "}";

    public GlLutShader(Bitmap lutTexture) {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
        this.lutTexture = lutTexture;
    }

    private int hTex;

    private GlImageBitmapTexture texture;
    private Bitmap lutTexture;

    @Override
    public void onDraw() {
        //setLutTexture(lutTexture);
        //glUniform1i(getHandle("lutTexture"), getLutTexture());

        int offsetDepthMapTextureUniform = getHandle("lutTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTex);
        GLES20.glUniform1i(offsetDepthMapTextureUniform, 3);
    }

    public int getLutTexture() {
        return texture.getTexName();
    }

    @Override
    public void setup() {
        super.setup();
        hTex = OpenGlUtils.loadTexture(lutTexture, OpenGlUtils.NO_TEXTURE, true);
    }


    private void setLutTexture(Bitmap resID) {
        texture = new GlImageBitmapTexture(resID);
        texture.setup();
    }
}
