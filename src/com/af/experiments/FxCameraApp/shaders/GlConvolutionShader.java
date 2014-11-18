package com.af.experiments.FxCameraApp.shaders;
public class GlConvolutionShader extends GlShader {

	private static final String VERTEX_SHADER =
		"attribute vec4 aPosition;" +
		"attribute vec4 aTextureCoord;" +

		"highp float texelWidth = 1.0 / 1024.;;" +
		"highp float texelHeight = 1.0 / 728.;" +

		"varying vec2 textureCoordinate;" +
		"varying vec2 leftTextureCoordinate;" +
		"varying vec2 rightTextureCoordinate;" +

		"varying vec2 topTextureCoordinate;" +
		"varying vec2 topLeftTextureCoordinate;" +
		"varying vec2 topRightTextureCoordinate;" +

		"varying vec2 bottomTextureCoordinate;" +
		"varying vec2 bottomLeftTextureCoordinate;" +
		"varying vec2 bottomRightTextureCoordinate;" +

		"void main() {" +
			"gl_Position = aPosition;" +

			"vec2 widthStep = vec2(texelWidth, 0.0);" +
			"vec2 heightStep = vec2(0.0, texelHeight);" +
			"vec2 widthHeightStep = vec2(texelWidth, texelHeight);" +
			"vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);" +

			"textureCoordinate = aTextureCoord.xy;" +
			"leftTextureCoordinate = textureCoordinate.xy - widthStep;" +
			"rightTextureCoordinate = textureCoordinate.xy + widthStep;" +

			"topTextureCoordinate = textureCoordinate.xy - heightStep;" +
			"topLeftTextureCoordinate = textureCoordinate.xy - widthHeightStep;" +
			"topRightTextureCoordinate = textureCoordinate.xy + widthNegativeHeightStep;" +

			"bottomTextureCoordinate = textureCoordinate.xy + heightStep;" +
			"bottomLeftTextureCoordinate = textureCoordinate.xy - widthNegativeHeightStep;" +
			"bottomRightTextureCoordinate = textureCoordinate.xy + widthHeightStep;" +
		"}";

	private static final String FRAGMENT_SHADER =
			"precision highp float;" +	// 演算精度を指定します。

			"uniform lowp sampler2D sTexture;" +
			"mediump mat3 convolutionMatrix = mat3( 0., 0., 0., 0., 1., 0., 0., 0., 0.);" +

			"varying vec2 textureCoordinate;" +
			"varying vec2 leftTextureCoordinate;" +
			"varying vec2 rightTextureCoordinate;" +

			"varying vec2 topTextureCoordinate;" +
			"varying vec2 topLeftTextureCoordinate;" +
			"varying vec2 topRightTextureCoordinate;" +

			"varying vec2 bottomTextureCoordinate;" +
			"varying vec2 bottomLeftTextureCoordinate;" +
			"varying vec2 bottomRightTextureCoordinate;" +

			"void main() {" +
				"mediump vec4 bottomColor = texture2D(sTexture, bottomTextureCoordinate);" +
				"mediump vec4 bottomLeftColor = texture2D(sTexture, bottomLeftTextureCoordinate);" +
				"mediump vec4 bottomRightColor = texture2D(sTexture, bottomRightTextureCoordinate);" +

				"mediump vec4 centerColor = texture2D(sTexture, textureCoordinate);" +
				"mediump vec4 leftColor = texture2D(sTexture, leftTextureCoordinate);" +
				"mediump vec4 rightColor = texture2D(sTexture, rightTextureCoordinate);" +

				"mediump vec4 topColor = texture2D(sTexture, topTextureCoordinate);" +
				"mediump vec4 topRightColor = texture2D(sTexture, topRightTextureCoordinate);" +
				"mediump vec4 topLeftColor = texture2D(sTexture, topLeftTextureCoordinate);" +

				"mediump vec4 resultColor = topLeftColor * convolutionMatrix[0][0] + topColor * convolutionMatrix[0][1] + topRightColor * convolutionMatrix[0][2];" +
				"resultColor += leftColor * convolutionMatrix[1][0] + centerColor * convolutionMatrix[1][1] + rightColor * convolutionMatrix[1][2];" +
				"resultColor += bottomLeftColor * convolutionMatrix[2][0] + bottomColor * convolutionMatrix[2][1] + bottomRightColor * convolutionMatrix[2][2];" +

				"gl_FragColor = resultColor;" +
			"}";

	private float mTexelWidth = 1.0f / 1024f;
	private float mTexelHeight = 1.0f / 728;

	private float[] mConvolutionMatrix = new float[]{
			0f, 0f, 0f,
			0f, 1f, 0f,
			0f, 0f, 0f
		};

	public GlConvolutionShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	public float getTexelWidth() {
		return mTexelWidth;
	}

	public void setTexelWidth(final float texelWidth) {
		mTexelWidth = texelWidth;
	}

	public float getTexelHeight() {
		return mTexelHeight;
	}

	public void setTexelHeight(final float texelHeight) {
		mTexelHeight = texelHeight;
	}

	public float[] getConvolutionMatrix() {
		return mConvolutionMatrix;
	}

	public void setConvolutionMatrix(final float[] convolutionMatrix) {
		mConvolutionMatrix = convolutionMatrix;
	}

	//////////////////////////////////////////////////////////////////////////
	protected String mShaderName = "Convolution";

	public String getName() {
		return mShaderName;
	}
	@Override
	public void onDraw() {
	}

}