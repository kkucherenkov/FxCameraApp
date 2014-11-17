package com.af.experiments.FxCameraApp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.af.experiments.FxCameraApp.Utils.Fps;
import com.af.experiments.FxCameraApp.camera.CameraHelper;
import com.af.experiments.FxCameraApp.View.CameraView;
import com.af.experiments.FxCameraApp.View.GlPreview;
import com.af.experiments.FxCameraApp.shaders.*;

public class MyActivity extends Activity {

    private CameraView mCameraView;
    private GlPreview mPreview;
    private Button catchButton;
    private ImageView photoView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final TextView fpsTextView = (TextView) findViewById(R.id.fps_tv);

        mPreview = new GlPreview(this);
        mPreview.setFps( new Fps(new Fps.Callback() {
            @Override
            public void onFps(int fps) {
                fpsTextView.setText("fps: " + fps);
            }
        }));
        findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlShader shader = new GlShader();
                mPreview.setShader(shader);
            }
        });
        findViewById(R.id.bwButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //GlShader shader = new GlGrayScaleShader();
                Bitmap lutTexture = BitmapFactory.decodeResource(getResources(), R.drawable.filtershow_fx_0003_blue_crush);
//                Bitmap lutTexture = BitmapFactory.decodeResource(getResources(), R.drawable.filtershow_fx_0006_x_process);
                GlLutShader shader = new GlLutShader(lutTexture);
                mPreview.setShader(shader);
            }
        });
        findViewById(R.id.fcButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlShader shader = new GlSobelEdgeDetectionShader();
                mPreview.setShader(shader);
            }
        });

        photoView = (ImageView) findViewById(R.id.photo_view);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoView.setVisibility(View.GONE);
                mCameraView.setVisibility(View.VISIBLE);
                mCameraView.startPreview();
            }
        });


        findViewById(R.id.sepiaButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.capture(new CameraView.CaptureCallback() {
                    @Override
                    public boolean onImageCapture(Bitmap bitmap) {
                        photoView.setImageBitmap(bitmap);
                        photoView.setVisibility(View.VISIBLE);
                        mCameraView.setVisibility(View.GONE);
                        return true;
                    }
                });

            }
        });
        findViewById(R.id.invertButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlShader shader = new GlColorInvertShader();
                mPreview.setShader(shader);

            }
        });
        findViewById(R.id.sphereButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlShader shader = new GlGlassSphereShader();
                mPreview.setShader(shader);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraView != null && mCameraView.isAutoStart()) {
            mCameraView.setPreview(mPreview);
            mCameraView.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (mCameraView != null) {
            mCameraView.stopPreview();
        }
        super.onPause();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mCameraView = (CameraView) findViewById(R.id.camera);
        mCameraView.setPreview(mPreview);
        mCameraView.setSquareFrame(false);
        mCameraView.setPreviewSizePolicy(CameraView.PreviewSizePolicy.DISPLAY);

    }


    public CameraView getCameraView() {
        return mCameraView;
    }

    public CameraHelper getCameraHelper() {
        return mCameraView.getCameraHelper();
    }
}