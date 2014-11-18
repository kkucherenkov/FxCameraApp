package com.af.experiments.FxCameraApp.shaders;

public class GlEmbossShader extends GlThreex3ConvolutionShader {

	private float mIntensity;

	public GlEmbossShader() {
		super();
		setIntensity(3f);
	}

	protected String mShaderName = "Emboss";

	public String getName() {
		return mShaderName;
	}

	public float getIntensity() {
		return mIntensity;
	}

	public void setIntensity(final float intensity) {
		mIntensity = intensity;
		setConvolutionKernel(new float[]{
				intensity * -2f, -intensity, 0f,
				-intensity, 1f, intensity,
				0f, intensity, intensity * 2f
			});
	}

}