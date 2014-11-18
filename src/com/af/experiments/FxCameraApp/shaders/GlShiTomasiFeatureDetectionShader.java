package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;

public class GlShiTomasiFeatureDetectionShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" + 	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float sensitivity;" +

			"void main() {" +
				"mediump vec3 derivativeElements = texture2D(sTexture, vTextureCoord).rgb;" +

				"mediump float derivativeDifference = derivativeElements.x - derivativeElements.y;" +
				"mediump float zElement = (derivativeElements.z * 2.0) - 1.0;" +

				// R = Ix^2 + Iy^2 - sqrt( (Ix^2 - Iy^2)^2 + 4 * Ixy * Ixy)
				"mediump float cornerness = derivativeElements.x + derivativeElements.y - sqrt(derivativeDifference * derivativeDifference + 4.0 * zElement * zElement);" +

				"gl_FragColor = vec4(vec3(cornerness * sensitivity), 1.0);" +
			"}";

	private float mSensitivity = 1.5f;

	public GlShiTomasiFeatureDetectionShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}


	public float getSensitivity() {
		return mSensitivity;
	}

	public void setSensitivity(final float sensitivity) {
		mSensitivity = sensitivity;
	}

	@Override
	public void onDraw() {
		glUniform1f(getHandle("sensitivity"), mSensitivity);
	}

}