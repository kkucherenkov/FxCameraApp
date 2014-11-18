package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;

public class GlNobleCornerDetectionShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;\n" +

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform lowp float sensitivity;" +

			"void main() {" +
				"mediump vec3 derivativeElements = texture2D(sTexture, vTextureCoord).rgb;" +
				"mediump float derivativeSum = derivativeElements.x + derivativeElements.y;" +

				// R = (Ix^2 * Iy^2 - Ixy * Ixy) / (Ix^2 + Iy^2)
				"mediump float zElement = (derivativeElements.z * 2.0) - 1.0;" +
				//	"mediump float harrisIntensity = (derivativeElements.x * derivativeElements.y - (derivativeElements.z * derivativeElements.z)) / (derivativeSum);" +
				"mediump float cornerness = (derivativeElements.x * derivativeElements.y - (zElement * zElement)) / (derivativeSum);" +

				// Original Harris detector
				// R = Ix^2 * Iy^2 - Ixy * Ixy - k * (Ix^2 + Iy^2)^2
				//	"highp float harrisIntensity = derivativeElements.x * derivativeElements.y - (derivativeElements.z * derivativeElements.z) - harrisConstant * derivativeSum * derivativeSum;" +

				//	"gl_FragColor = vec4(vec3(harrisIntensity * 7.0), 1.0);" +
				"gl_FragColor = vec4(vec3(cornerness * sensitivity), 1.0);" +
			"}";

	private float mSensitivity = 2.0f;
	public GlNobleCornerDetectionShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	protected String mShaderName = "Nobble CD";

	public String getName() {
		return mShaderName;
	}

	public float getSensitivity() {
		return mSensitivity;
	}

	public void setSensitivity(final float sensitivity) {
		mSensitivity = sensitivity;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform1f(getHandle("sensitivity"), mSensitivity);
	}

}