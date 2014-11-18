package com.af.experiments.FxCameraApp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.af.experiments.FxCameraApp.Utils.Fps;
import com.af.experiments.FxCameraApp.View.CameraView;
import com.af.experiments.FxCameraApp.View.GlPreview;
import com.af.experiments.FxCameraApp.camera.CameraHelper;
import com.af.experiments.FxCameraApp.shaders.*;
import com.af.experiments.FxCameraApp.shaders.fx.GlLutShader;

import java.io.*;
import java.util.ArrayList;

public class MyActivity extends Activity {

    private CameraView mCameraView;
    private GlPreview mPreview;
    private ImageView photoView;
    private ArrayList<GlShader> shaders;

    private ListView listView;
    private FilterAdapter filterAdapter;

    private GlShader mCurrentShader;
    private ProgressBar mProgress;

    private android.os.Handler mHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        mHandler = new android.os.Handler();
        mProgress = (ProgressBar) findViewById(R.id.progress);

        final TextView fpsTextView = (TextView) findViewById(R.id.fps_tv);

        mPreview = new GlPreview(this);
        mPreview.setFps( new Fps(new Fps.Callback() {
            @Override
            public void onFps(int fps) {
                fpsTextView.setText("fps: " + fps);
            }
        }));

        listView = (ListView) findViewById(R.id.shaders_list);
        shaders = new ArrayList<GlShader>();

        mCurrentShader = new GlShader();
        shaders.add(new GlShader());
        shaders.add(new GlColorInvertShader());
        shaders.add(new GlFalseColorShader());
        shaders.add(new GlXRayShader());

        shaders.add(new GlBilateralShader());
        shaders.add(new GlBoxBlurShader());
        shaders.add(new GlBulgeDistortionShader());
        shaders.add(new GlCGAColorspaceShader());
        //shaders.add(new GlColorPackingShader());
        //shaders.add(new GlConvolutionShader());
        shaders.add(new GlEmbossShader());
        shaders.add(new GlFastBlurShader());
        shaders.add(new GlGaussianBlurShader());
        //shaders.add(new GlHarrisCornerDetectionShader());
        shaders.add(new GlMonochromeShader());
        //shaders.add(new GlNobleCornerDetectionShader());
        //shaders.add(new GlPerlinNoiseShader());
        shaders.add(new GlPinchDistortionShader());
        shaders.add(new GlPixellateShader());
        shaders.add(new GlPolarPixellateShader());
        shaders.add(new GlPolkaDotShader());
        shaders.add(new GlPosterizeShader());

        shaders.add(new GlSharpenShader());
        //shaders.add(new GlShiTomasiFeatureDetectionShader());
        shaders.add(new GlSphereRefractionShader());
        shaders.add(new GlStretchDistortionShader());
        //shaders.add(new GlToneShader());
        shaders.add(new GlVignetteShader());
        //shaders.add(new GlWeakPixelInclusionShader());


        //shaders.add(new GlXYDerivativeShader());

        shaders.add(new GlGlassSphereShader());
        shaders.add(new GlGrayScaleShader());
        shaders.add(new GlSepiaShader());
        shaders.add(new GlSobelEdgeDetectionShader());

        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0000_vintage, "vintage"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0001_instant, "instant"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0002_bleach, "bleach"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0003_blue_crush, "blue"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0004_bw_contrast, "bw contrast"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0005_punch, "punch"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0006_x_process, "x process"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0007_washout, "latte"));
        shaders.add(new GlLutShader(getResources(), R.drawable.filtershow_fx_0008_washout_color, "latte color"));

        filterAdapter = new FilterAdapter(this, R.layout.rowlayout, shaders);

        listView.setAdapter(filterAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mCurrentShader = shaders.get(position);
                mPreview.setShader(mCurrentShader);
            }
        });

        photoView = (ImageView) findViewById(R.id.photo_view);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                photoView.setVisibility(View.GONE);
//                mCameraView.setVisibility(View.VISIBLE);

            }
        });

        findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.startPreview();
            }
        });

        findViewById(R.id.catch_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setVisibility(View.VISIBLE);
                mCameraView.capture(new CameraView.CaptureCallback() {
                    @Override
                    public boolean onImageCapture(Bitmap bitmap) {

                        mCameraView.stopPreview();
                        SaveImageTask task = new SaveImageTask();
                        task.execute(bitmap);
                        return true;
                    }
                });
            }
        });
    }

    class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            saveBitmap(bitmap);
            return true;
        }

        private void saveBitmap( Bitmap bitmap) {
            File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/fxCameraApp");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            String path = imagesFolder.getAbsolutePath() + "/fx_" + System.currentTimeMillis() + ".jpg";
            Log.d("face_rec", "saved face is " + path);

            File file = new File(path);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(bytes.toByteArray());
                out.flush();
                out.close();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                MyActivity.this.sendBroadcast(mediaScanIntent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            mProgress.setVisibility(View.GONE);
            mCameraView.startPreview();
            mPreview.setShader(mCurrentShader);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCameraView != null ) {
            mCameraView.setPreview(mPreview);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCameraView.startPreview();
                }
            }, 1000);
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

        mCameraView.setPreviewSizePolicy(CameraView.PreviewSizePolicy.DISPLAY);

    }


    public CameraView getCameraView() {
        return mCameraView;
    }

    public CameraHelper getCameraHelper() {
        return mCameraView.getCameraHelper();
    }
}