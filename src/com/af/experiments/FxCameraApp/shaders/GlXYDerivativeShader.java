package com.af.experiments.FxCameraApp.shaders;

public class GlXYDerivativeShader extends GlSobelEdgeDetectionShader {

	private static final String FRAGMENT_SHADER =
			"precision highp float;\n" +

			"uniform lowp sampler2D sTexture;\n" +

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
				"float topIntensity      = texture2D(sTexture, topTextureCoordinate).r;" +
				"float topRightIntensity = texture2D(sTexture, topRightTextureCoordinate).r;" +
				"float topLeftIntensity  = texture2D(sTexture, topLeftTextureCoordinate).r;" +
				"float bottomIntensity      = texture2D(sTexture, bottomTextureCoordinate).r;" +
				"float bottomLeftIntensity  = texture2D(sTexture, bottomLeftTextureCoordinate).r;" +
				"float bottomRightIntensity = texture2D(sTexture, bottomRightTextureCoordinate).r;" +
				"float leftIntensity  = texture2D(sTexture, leftTextureCoordinate).r;" +
				"float rightIntensity = texture2D(sTexture, rightTextureCoordinate).r;" +

				"float verticalDerivative = -topLeftIntensity - topIntensity - topRightIntensity + bottomLeftIntensity + bottomIntensity + bottomRightIntensity;" +
				"float horizontalDerivative = -bottomLeftIntensity - leftIntensity - topLeftIntensity + bottomRightIntensity + rightIntensity + topRightIntensity;" +
//				"float verticalDerivative = -topIntensity + bottomIntensity;" +
//				"float horizontalDerivative = -leftIntensity + rightIntensity;" +

				// Scaling the X * Y operation so that negative numbers are not clipped in the 0..1 range. This will be expanded in the corner detection filter
				"gl_FragColor = vec4(horizontalDerivative * horizontalDerivative, verticalDerivative * verticalDerivative, ((verticalDerivative * horizontalDerivative) + 1.0) / 2.0, 1.0);" +
			"}";

	protected String mShaderName = "Derivative";

	public String getName() {
		return mShaderName;
	}

	public GlXYDerivativeShader() {
		super(FRAGMENT_SHADER);
	}

}