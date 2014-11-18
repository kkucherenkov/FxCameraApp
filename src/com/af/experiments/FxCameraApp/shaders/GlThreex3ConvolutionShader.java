package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniformMatrix3fv;

public class GlThreex3ConvolutionShader extends GlThreex3TextureSamplingShader {

	private static final String FRAGMENT_SHADER =
			"precision highp float;\n" +

			"uniform lowp sampler2D sTexture;\n" +
			"uniform mediump mat3 convolutionMatrix;\n" +

			"varying vec2 textureCoordinate;\n" +
			"varying vec2 leftTextureCoordinate;\n" +
			"varying vec2 rightTextureCoordinate;\n" +

			"varying vec2 topTextureCoordinate;\n" +
			"varying vec2 topLeftTextureCoordinate;\n" +
			"varying vec2 topRightTextureCoordinate;\n" +

			"varying vec2 bottomTextureCoordinate;\n" +
			"varying vec2 bottomLeftTextureCoordinate;\n" +
			"varying vec2 bottomRightTextureCoordinate;\n" +

			"void main() {\n" +
				"mediump vec4 bottomColor      = texture2D(sTexture, bottomTextureCoordinate);\n" +
				"mediump vec4 bottomLeftColor  = texture2D(sTexture, bottomLeftTextureCoordinate);\n" +
				"mediump vec4 bottomRightColor = texture2D(sTexture, bottomRightTextureCoordinate);\n" +

				"mediump vec4 centerColor = texture2D(sTexture, textureCoordinate);\n" +
				"mediump vec4 leftColor   = texture2D(sTexture, leftTextureCoordinate);\n" +
				"mediump vec4 rightColor  = texture2D(sTexture, rightTextureCoordinate);\n" +

				"mediump vec4 topColor      = texture2D(sTexture, topTextureCoordinate);\n" +
				"mediump vec4 topRightColor = texture2D(sTexture, topRightTextureCoordinate);\n" +
				"mediump vec4 topLeftColor  = texture2D(sTexture, topLeftTextureCoordinate);\n" +

				"mediump vec4 resultColor = topLeftColor * convolutionMatrix[0][0] + topColor * convolutionMatrix[0][1] + topRightColor * convolutionMatrix[0][2];\n" +
				"resultColor += leftColor * convolutionMatrix[1][0] + centerColor * convolutionMatrix[1][1] + rightColor * convolutionMatrix[1][2];\n" +
				"resultColor += bottomLeftColor * convolutionMatrix[2][0] + bottomColor * convolutionMatrix[2][1] + bottomRightColor * convolutionMatrix[2][2];\n" +
				"gl_FragColor = resultColor;\n" +
			"}";

	private static final float[] DEFAULT_CONVOLUTION_KERNEL = new float[] {
			0f, 0f, 0f,
			0f, 1f, 0f,
			0f, 0f, 0f
		};
	
	private float[] mConvolutionKernel;

	public GlThreex3ConvolutionShader() {
		this(DEFAULT_CONVOLUTION_KERNEL);
	}

	public GlThreex3ConvolutionShader(final float[] convolutionKernel) {
		super(FRAGMENT_SHADER);
		mConvolutionKernel = convolutionKernel;
	}


	public float[] getConvolutionKernel() {
		return mConvolutionKernel;
	}

	public void setConvolutionKernel(final float[] convolutionKernel) {
		mConvolutionKernel = convolutionKernel;
	}

	@Override
	public void onDraw() {
		super.onDraw();
		glUniformMatrix3fv(getHandle("convolutionMatrix"), 1, false, mConvolutionKernel, 0);
	}

}