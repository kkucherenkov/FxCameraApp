package com.af.experiments.FxCameraApp.shaders;
import static android.opengl.GLES20.glUniform1f;

public class GlColorPackingShader extends GlShader {

	private static final String VERTEX_SHADER =
			"attribute vec4 aPosition;" +
			"attribute vec4 aTextureCoord;" +

			"uniform highp float texelWidth;" +
			"uniform highp float texelHeight;" +

			"varying vec2 upperLeftInputTextureCoordinate;" +
			"varying vec2 upperRightInputTextureCoordinate;" +
			"varying vec2 lowerLeftInputTextureCoordinate;" +
			"varying vec2 lowerRightInputTextureCoordinate;" +

			"void main() {" +
				"gl_Position = aPosition;" +
				"vec2 texcoord = aTextureCoord.xy;" +
				"upperLeftInputTextureCoordinate  = texcoord.xy + vec2(-texelWidth, -texelHeight);" +
				"upperRightInputTextureCoordinate = texcoord.xy + vec2( texelWidth, -texelHeight);" +
				"lowerLeftInputTextureCoordinate  = texcoord.xy + vec2(-texelWidth,  texelHeight);" +
				"lowerRightInputTextureCoordinate = texcoord.xy + vec2( texelWidth,  texelHeight);" +
			"}";

	private static final String FRAGMENT_SHADER =
			"precision lowp float;" +	// 演算精度を指定します。

			"uniform lowp sampler2D sTexture;" +
			"uniform mediump mat3 convolutionMatrix;" +

			"varying vec2 upperLeftInputTextureCoordinate;" +
			"varying vec2 upperRightInputTextureCoordinate;" +
			"varying vec2 lowerLeftInputTextureCoordinate;" +
			"varying vec2 lowerRightInputTextureCoordinate;" +

			"void main() {" +
				"float upperLeftIntensity = texture2D(sTexture, upperLeftInputTextureCoordinate).r;" +
				"float upperRightIntensity = texture2D(sTexture, upperRightInputTextureCoordinate).r;" +
				"float lowerLeftIntensity = texture2D(sTexture, lowerLeftInputTextureCoordinate).r;" +
				"float lowerRightIntensity = texture2D(sTexture, lowerRightInputTextureCoordinate).r;" +

				"gl_FragColor = vec4(upperLeftIntensity, upperRightIntensity, lowerLeftIntensity, lowerRightIntensity);" +
			"}";

	private float mTexelWidth = 0.5f / 64f;//1024f;
	private float mTexelHeight = 0.5f / 64f;

	public GlColorPackingShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}

	protected String mShaderName = "Color packing";

	public String getName() {
		return mShaderName;
	}

	public float getTexelWidth() {
		return mTexelWidth;
	}

	public void setTexelWidth(float texelWidth) {
		mTexelWidth = texelWidth;
	}

	public float getTexelHeight() {
		return mTexelHeight;
	}

	public void setTexelHeight(float texelHeight) {
		mTexelHeight = texelHeight;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("texelWidth"), mTexelWidth);
		glUniform1f(getHandle("texelHeight"), mTexelHeight);
	}

}