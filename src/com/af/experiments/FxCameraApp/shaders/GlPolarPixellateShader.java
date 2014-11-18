package com.af.experiments.FxCameraApp.shaders;

import static android.opengl.GLES20.glUniform2f;

public class GlPolarPixellateShader extends GlShader {

	private static final String FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。

			"varying highp vec2 vTextureCoord;" +
			"uniform lowp sampler2D sTexture;" +
			"uniform highp vec2 center;" +
			"uniform highp vec2 pixelSize;" +

			"void main() {" +
				"highp vec2 normCoord = 2.0 * vTextureCoord - 1.0;" +
				"highp vec2 normCenter = 2.0 * center - 1.0;" +

				"normCoord -= normCenter;" +

				"highp float r = length(normCoord);" +	// to polar coords
				"highp float phi = atan(normCoord.y, normCoord.x);" +	// to polar coords

				"r = r - mod(r, pixelSize.x) + 0.03;" +
				"phi = phi - mod(phi, pixelSize.y);" +

				"normCoord.x = r * cos(phi);" +
				"normCoord.y = r * sin(phi);" +

				"normCoord += normCenter;" +

				"mediump vec2 textureCoordinateToUse = normCoord / 2.0 + 0.5;" +

				"gl_FragColor = texture2D(sTexture, textureCoordinateToUse);" +
			"}";

	private float mCenterX = 0.5f;
	private float mCenterY = 0.5f;
	private float mPixelWidth = 0.05f;
	private float mPixelHeight = 0.05f;

	public GlPolarPixellateShader() {
		super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
	}
	protected String mShaderName = "polar pixelate";

	public String getName() {
		return mShaderName;
	}

	public float getCenterX() {
		return mCenterX;
	}

	public void setCenterX(final float centerX) {
		mCenterX = centerX;
	}

	public float getCenterY() {
		return mCenterY;
	}

	public void setCenterY(final float centerY) {
		mCenterY = centerY;
	}

	public float getPixelWidth() {
		return mPixelWidth;
	}

	public void setPixelWidth(final float pixelWidth) {
		mPixelWidth = pixelWidth;
	}

	public float getPixelHeight() {
		return mPixelHeight;
	}

	public void setPixelHeight(final float pixelHeight) {
		mPixelHeight = pixelHeight;
	}

	//////////////////////////////////////////////////////////////////////////

	@Override
	public void onDraw() {
		glUniform2f(getHandle("center"), mCenterX, mCenterY);
		glUniform2f(getHandle("pixelSize"), mPixelWidth, mPixelHeight);
	}

}