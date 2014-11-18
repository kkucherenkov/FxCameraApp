package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;

public class GlPolkaDotShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp float fractionalWidthOfPixel;" +
			"uniform highp float aspectRatio;" +
			"uniform highp float dotScaling;" +

			"void main() {" +
				"highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);" +

				"highp vec2 samplePos = vTextureCoord - mod(vTextureCoord, sampleDivisor) + 0.5 * sampleDivisor;" +
				"highp vec2 textureCoordinateToUse = vec2(vTextureCoord.x, (vTextureCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));" +
				"highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));" +
				"highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);" +
				"lowp float checkForPresenceWithinDot = step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);" +

				"gl_FragColor = vec4(texture2D(sTexture, samplePos ).rgb * checkForPresenceWithinDot, 1.0);" +
			"}";

	private float mFractionalWidthOfPixel = 0.013f;
	private float mAspectRatio = 0.5f;
	private float mDotScaling = 0.90f;

	public GlPolkaDotShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	protected String mShaderName = "polka dot";

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

	public float getDotScaling() {
		return mDotScaling;
	}

	public void setDotScaling(final float dotScaling) {
		mDotScaling = dotScaling;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("fractionalWidthOfPixel"), mFractionalWidthOfPixel);
		glUniform1f(getHandle("aspectRatio"), mAspectRatio);
		glUniform1f(getHandle("dotScaling"), mDotScaling);
	}

}