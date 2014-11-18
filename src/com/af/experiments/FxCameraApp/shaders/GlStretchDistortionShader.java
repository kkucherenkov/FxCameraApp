package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform2f;

public class GlStretchDistortionShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp vec2 center;" +

			"void main() {" +
				"highp vec2 normCoord = 2.0 * vTextureCoord - 1.0;" +
				"highp vec2 normCenter = 2.0 * center - 1.0;" +

				"normCoord -= normCenter;" +
				"mediump vec2 s = sign(normCoord);" +
				"normCoord = abs(normCoord);" +
				"normCoord = 0.5 * normCoord + 0.5 * smoothstep(0.25, 0.5, normCoord) * normCoord;" +
				"normCoord = s * normCoord;" +

				"normCoord += normCenter;" +

				"mediump vec2 textureCoordinateToUse = normCoord / 2.0 + 0.5;" +

				"gl_FragColor = texture2D(sTexture, textureCoordinateToUse);" +
			"}";


	private float mCenterX = 0.5f;
	private float mCenterY = 0.5f;

	protected String mShaderName = "Stretch";

	public String getName() {
		return mShaderName;
	}

	public GlStretchDistortionShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform2f(getHandle("center"), mCenterX, mCenterY);
	}

}