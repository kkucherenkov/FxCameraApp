package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;

public class GlPixellateShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" +	// 演算精度を指定します。
			"varying vec2 vTextureCoord;\n" +
			"uniform lowp sampler2D sTexture;\n" +

			"uniform highp float fractionalWidthOfPixel;\n" +
			"uniform highp float aspectRatio;\n" +

			"void main() {\n" +
				"highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
				"highp vec2 samplePos = vTextureCoord - mod(vTextureCoord, sampleDivisor) + 0.5 * sampleDivisor;\n" +
				"gl_FragColor = texture2D(sTexture, samplePos);\n" +
			"}\n";

	private float mFractionalWidthOfPixel = 1f / 80f;//0.013f;
	private float mAspectRatio = 1.0f;

	public GlPixellateShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}



	protected String mShaderName = "pixelate";

	public String getName() {
		return mShaderName;
	}
	public float getFractionalWidthOfPixel() {
		return mFractionalWidthOfPixel;
	}

	public void setFractionalWidthOfPixel(final float fractionalWidthOfPixel) {
		mFractionalWidthOfPixel = fractionalWidthOfPixel;
	}

	public float getAspectRatio() {
		return mAspectRatio;
	}

	public void setAspectRatio(final float aspectRatio) {
		mAspectRatio = aspectRatio;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	protected void onDraw() {
		glUniform1f(getHandle("fractionalWidthOfPixel"), mFractionalWidthOfPixel);
		glUniform1f(getHandle("aspectRatio"), mAspectRatio);
	}

}