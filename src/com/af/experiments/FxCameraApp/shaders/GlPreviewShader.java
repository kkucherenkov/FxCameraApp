package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.*;
import static com.af.experiments.FxCameraApp.Utils.OpenGlUtils.GL_TEXTURE_EXTERNAL_OES;

public class GlPreviewShader  extends GlShader{
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "uniform float uCRatio;\n" +

                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying highp vec2 vTextureCoord;\n" +

                    "void main() {\n" +
                    "vec4 scaledPos = aPosition;\n" +
                    "scaledPos.x = scaledPos.x * uCRatio;\n" +
                    "gl_Position = uMVPMatrix * scaledPos;\n" +
                    "vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private final int mTexTarget;

    public GlPreviewShader(final int texTarget) {
        super(VERTEX_SHADER, createFragmentShaderSourceOESIfNeed(texTarget));
        mTexTarget = texTarget;
    }

    private static String createFragmentShaderSourceOESIfNeed(final int texTarget) {
        if (texTarget == GL_TEXTURE_EXTERNAL_OES) {
            return new StringBuilder()
                    .append("#extension GL_OES_EGL_image_external : require\n")
                    .append(DEFAULT_FRAGMENT_SHADER.replace("sampler2D", "samplerExternalOES"))
                    .toString();
        }
        return DEFAULT_FRAGMENT_SHADER;
    }

    public void draw(final int texName, final float[] mvpMatrix, final float[] stMatrix, final float aspectRatio) {
        useProgram();

        glUniformMatrix4fv(getHandle("uMVPMatrix"), 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(getHandle("uSTMatrix"),  1, false, stMatrix,  0);
        glUniform1f(getHandle("uCRatio"), aspectRatio);

        glBindBuffer(GL_ARRAY_BUFFER, getVertexBufferName());
        glEnableVertexAttribArray(getHandle("aPosition"));
        glVertexAttribPointer(getHandle("aPosition"), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
        glEnableVertexAttribArray(getHandle("aTextureCoord"));
        glVertexAttribPointer(getHandle("aTextureCoord"), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(mTexTarget, texName);
        glUniform1i(getHandle(DEFAULT_UNIFORM_SAMPLER), 0);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glDisableVertexAttribArray(getHandle("aPosition"));
        glDisableVertexAttribArray(getHandle("aTextureCoord"));
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

}
