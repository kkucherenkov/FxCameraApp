package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform2f;
import static android.opengl.GLES20.glUniform3fv;

public class GlVignetteShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +

			"uniform lowp vec2 vignetteCenter;" +
			"uniform lowp vec3 vignetteColor;" +
			"uniform highp float vignetteStart;" +
			"uniform highp float vignetteEnd;" +

			"void main() {" +
				"lowp vec3 rgb = texture2D(sTexture, vTextureCoord).rgb;" +
				"lowp float d = distance(vTextureCoord, vec2(vignetteCenter.x, vignetteCenter.y));" +
				"lowp float percent = smoothstep(vignetteStart, vignetteEnd, d);" +
				"gl_FragColor = vec4(mix(rgb.x, vignetteColor.x, percent), mix(rgb.y, vignetteColor.y, percent), mix(rgb.z, vignetteColor.z, percent), 1.0);" +
			"}";

	private float mVignetteCenterX = 0.5f;
	private float mVignetteCenterY = 0.5f;
	private float[] mVignetteColor = new float[]{ 0f, 0f, 0f };
	private float mVignetteStart = 0.2f;
	private float mVignetteEnd = 0.85f;

	public GlVignetteShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}


	public float getVignetteStart() {
		return mVignetteStart;
	}

	public void setVignetteStart(final float vignetteStart) {
		mVignetteStart = vignetteStart;
	}

	public float getVignetteEnd() {
		return mVignetteEnd;
	}

	public void setVignetteEnd(final float vignetteEnd) {
		mVignetteEnd = vignetteEnd;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform2f(getHandle("vignetteCenter"), mVignetteCenterX, mVignetteCenterY);
		glUniform3fv(getHandle("vignetteColor"), 0, mVignetteColor, 0);
		glUniform1f(getHandle("vignetteStart"), mVignetteStart);
		glUniform1f(getHandle("vignetteEnd"), mVignetteEnd);
	}

}