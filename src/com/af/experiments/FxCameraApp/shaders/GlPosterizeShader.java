package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;

public class GlPosterizeShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp float colorLevel;" +

			"void main() {" +
				"highp vec4 color = texture2D(sTexture, vTextureCoord);" +
				"gl_FragColor = floor((color * colorLevel) + vec4(0.5)) / colorLevel;" +
			"}";

	private float mColorLevel = 5f;

	protected String mShaderName = "Posterize";

	public String getName() {
		return mShaderName;
	}

	public GlPosterizeShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}


	public float getColorLevel() {
		return mColorLevel;
	}

	public void setColorLevel(final float colorLevel) {
		mColorLevel = colorLevel;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("colorLevel"), mColorLevel);
	}

}