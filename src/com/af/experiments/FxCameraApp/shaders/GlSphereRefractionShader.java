package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform2f;

public class GlSphereRefractionShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp vec2 center;" +
			"uniform highp float radius;" +
			"uniform highp float aspectRatio;" +
			"uniform highp float refractiveIndex;" +

			"void main() {" +
				"highp vec2 textureCoordinateToUse = vec2(vTextureCoord.x, (vTextureCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));" +
				"highp float distanceFromCenter = distance(center, textureCoordinateToUse);" +
				"lowp float checkForPresenceWithinSphere = step(distanceFromCenter, radius);" +

				"distanceFromCenter = distanceFromCenter / radius;" +

				"highp float normalizedDepth = radius * sqrt(1.0 - distanceFromCenter * distanceFromCenter);" +
				"highp vec3 sphereNormal = normalize(vec3(textureCoordinateToUse - center, normalizedDepth));" +

				"highp vec3 refractedVector = refract(vec3(0.0, 0.0, -1.0), sphereNormal, refractiveIndex);" +

				"gl_FragColor = texture2D(sTexture, (refractedVector.xy + 1.0) * 0.5) * checkForPresenceWithinSphere;" +
			"}";

	private float mCenterX = 0.5f;
	private float mCenterY = 0.5f;
	private float mRadius = 0.5f;//0.25f;
	private float mAspectRatio = 1.0f;
	private float mRefractiveIndex = 0.71f;

	public GlSphereRefractionShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}

	protected String mShaderName = "Fish eye2";

	public String getName() {
		return mShaderName;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform2f(getHandle("center"), mCenterX, mCenterY);
		glUniform1f(getHandle("radius"), mRadius);
		glUniform1f(getHandle("aspectRatio"), mAspectRatio);
		glUniform1f(getHandle("refractiveIndex"), mRefractiveIndex);
	}

}