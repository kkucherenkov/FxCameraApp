package com.af.experiments.FxCameraApp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import com.af.experiments.FxCameraApp.Utils.Fps;
import com.af.experiments.FxCameraApp.Utils.OpenGlUtils;
import com.af.experiments.FxCameraApp.camera.CameraHelper;
import com.af.experiments.FxCameraApp.ogles.GLES20ConfigChooser;
import com.af.experiments.FxCameraApp.ogles.GLES20ContextFactory;
import com.af.experiments.FxCameraApp.ogles.GlPreviewTextureFactory;
import com.af.experiments.FxCameraApp.ogles.Texture;
import com.af.experiments.FxCameraApp.renderer.GLES20FramebufferObject;
import com.af.experiments.FxCameraApp.renderer.GlFrameBufferObjectRenderer;
import com.af.experiments.FxCameraApp.shaders.*;

import javax.microedition.khronos.egl.EGLConfig;
import java.io.IOException;

import static android.opengl.GLES20.*;

public class GlPreview  extends GLSurfaceView implements CameraView.Preview, Camera.PictureCallback {

    CameraHelper mCameraHelper;
    Renderer mRenderer;

    boolean mFaceMirror = true;
    public GlPreview(final Context context) {
        super(context);
        initialize(context);
    }

    public GlPreview(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(final Context context) {
        setEGLConfigChooser(new GLES20ConfigChooser(false));
        setEGLContextFactory(new GLES20ContextFactory());

        mRenderer = new Renderer();
        setRenderer(mRenderer);

        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public final boolean isFaceMirror() {
        return mFaceMirror;
    }

    public final void setFaceMirror(final boolean mirror) {
        mFaceMirror = mirror;
    }

    public void setShader(final GlShader shader) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setShader(shader);
            }
        });
    }
    public void setInputTexture(final Texture texture) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setTexture(texture);
            }
        });
    }

    public void setFps(final Fps fps) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setFps(fps);
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////

    @Override
    public void setCameraHelper(final CameraHelper helper) {
        mCameraHelper = helper;
    }

    @Override
    public boolean isSquareFrameSupported() {
        return true;
    }
    @Override public void onOpenCamera() {}

    @Override public void onReleaseCamera() {}

    private boolean mPreviewing;

    private int mMeasurePreviewWidth;
    private int mMeasurePreviewHeight;
    private CameraView.CameraStateListener mCameraStateListener;
    private boolean mWaitingStartPreview;

    @Override
    public void startPreview(final int measurePreviewWidth, final int measurePreviewHeight, final CameraView.CameraStateListener listener) {
        synchronized (this) {
            mMeasurePreviewWidth = measurePreviewWidth;
            mMeasurePreviewHeight = measurePreviewHeight;
            mCameraStateListener = listener;

            if (mRenderer.mMaxTextureSize != 0) {
                startPreview();
            } else {
                mWaitingStartPreview = true;
            }
        }
    }

    void onRendererInitialized() {
        if (mWaitingStartPreview) {
            mWaitingStartPreview = false;
            startPreview();
        }
    }

    private void startPreview() {
        synchronized (this) {
            mPreviewing = false;

            if (mMeasurePreviewWidth > 0 && mMeasurePreviewHeight > 0) {
                mCameraHelper.setupOptimalPreviewSizeAndPictureSize(mMeasurePreviewWidth, mMeasurePreviewHeight, mRenderer.mMaxTextureSize);
            }
            requestLayout();

            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.onStartPreview();
                }
            });
        }
    }

    void onStartPreviewFinished() {
        synchronized (this) {
            if (!mPreviewing && mCameraHelper.isOpened()) {
                mCameraHelper.startPreview();
                mPreviewing = true;

                if (mCameraStateListener != null) {
                    mCameraStateListener.onStartPreview();
                    mCameraStateListener = null;
                }
            }
        }
    }

    @Override
    public void onStopPreview() {
        synchronized (this) {
            mWaitingStartPreview = false;
            mPreviewing = false;
        }
    }

    private CameraView.CaptureCallback mCaptureCallback;

    @Override
    public void takePicture(final CameraView.CaptureCallback callback) {
        takePicture(callback, true);
    }

    @Override
    public void takePicture(final CameraView.CaptureCallback callback, final boolean autoFocus) {
        mCaptureCallback = callback;
        mCameraHelper.takePicture(this, autoFocus);
    }

    @Override
    public void onPictureTaken(final byte[] data, final Camera camera) {
        mCameraHelper.stopPreview();

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.capture();
            }
        });
    }

    public void capture(final CameraView.CaptureCallback callback) {
        mCaptureCallback = callback;

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.capture();
            }
        });
    }

    void onImageCapture(final Bitmap bitmap) {
        if (!mCaptureCallback.onImageCapture(bitmap) && bitmap != null) {
            bitmap.recycle();
        }
        mCaptureCallback = null;
    }

    private final class Renderer extends GlFrameBufferObjectRenderer implements PreviewTexture.OnFrameAvailableListener {

        private static final String TAG = "GLES20Preview.Renderer";

        private final Handler mHandler = new Handler();

        private PreviewTexture mPreviewTexture;
        private boolean mUpdateSurface = false;

        private Texture mImageTexture;
        private boolean mUploadTexture;

        private int mTexName;

        private float[] mMVPMatrix  = new float[16];
        private float[] mProjMatrix = new float[16];
        private float[] mMMatrix    = new float[16];
        private float[] mVMatrix    = new float[16];
        private float[] mSTMatrix   = new float[16];
        private float mCameraRatio  = 1.0f;

        private GLES20FramebufferObject mFramebufferObject;
        private GlPreviewShader mPreviewShader;
        private GlPreviewShader mImageShader;

        private GlShader mShader;
        private boolean mIsNewShader;
        int mMaxTextureSize;

        public Renderer() {
            Matrix.setIdentityM(mSTMatrix, 0);
        }

        public void setShader(final GlShader shader) {
            if (mShader != null) {
                mShader.release();
            }
            if (shader != null) {
                mIsNewShader = true;
            }
            mShader = shader;
            mIsNewShader = true;
            requestRender();
        }

        public void onStartPreview() {
            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.rotateM(mMMatrix, 0, -mCameraHelper.getOptimalOrientation(), 0.0f, 0.0f, 1.0f);
            if (mCameraHelper.isFaceCamera() && !mFaceMirror) {
                Matrix.scaleM(mMMatrix, 0, 1.0f, -1.0f, 1.0f);
            }

            final Camera.Size previewSize = mCameraHelper.getPreviewSize();
            mCameraRatio = (float) previewSize.width / previewSize.height;

            try {
                mPreviewTexture.setup(mCameraHelper);
            } catch (final IOException e) {
                Log.e(TAG, "Cannot set preview texture target!");
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onStartPreviewFinished();
                }
            });
        }

        public void setTexture(final Texture texture) {
            synchronized (this) {
                if (mImageTexture != null) {
                    mImageTexture.release();
                }
                Matrix.setIdentityM(mMMatrix, 0);
                mImageTexture = texture;
                mUploadTexture = true;
            }
            requestRender();
        }

        private static final int GINGERBREAD = 9;

        public void capture() {
            final Bitmap bitmap;
            if (mCameraHelper != null) {
                bitmap= getBitmap(mCameraHelper.getOrientation(), Integer.parseInt(Build.VERSION.SDK) < GINGERBREAD && mCameraHelper.isFaceCamera());
            } else {
                bitmap= getBitmap();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onImageCapture(bitmap);
                }
            });
        }

        @Override
        public void onSurfaceCreated(final EGLConfig config) {
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            final int[] args = new int[1];

            glGenTextures(args.length, args, 0);
            mTexName = args[0];

            mPreviewTexture = GlPreviewTextureFactory.newPreviewTexture(mTexName);
            mPreviewTexture.setOnFrameAvailableListener(this);

            glBindTexture(mPreviewTexture.getTextureTarget(), mTexName);
            OpenGlUtils.setupSampler(mPreviewTexture.getTextureTarget(), GL_LINEAR, GL_NEAREST);
            glBindTexture(GL_TEXTURE_2D, 0);

            mFramebufferObject = new GLES20FramebufferObject();
            mPreviewShader = new GlPreviewShader(mPreviewTexture.getTextureTarget());
            mPreviewShader.setup();
            mImageShader = new GlPreviewShader(GL_TEXTURE_2D);
            mImageShader.setup();

            Matrix.setLookAtM(mVMatrix, 0,
                    0.0f, 0.0f, 5.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f
            );

            synchronized (this) {
                mUpdateSurface = false;
            }
            if (mImageTexture != null) {
                mUploadTexture = true;
            }
            if (mShader != null) {
                mIsNewShader = true;
            }

            glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);
            mMaxTextureSize = args[0];

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onRendererInitialized();
                }
            });
        }

        @Override
        public void onSurfaceChanged(final int width, final int height) {
            mFramebufferObject.setup(width, height);
            mPreviewShader.setFrameSize(width, height);
            mImageShader.setFrameSize(width, height);
            if (mShader != null) {
                mShader.setFrameSize(width, height);
            }

            final float aspectRatio = (float) width / height;
            Matrix.frustumM(mProjMatrix, 0, -aspectRatio, aspectRatio, -1, 1, 5, 7);
        }

        @Override
        public void onDrawFrame(final GLES20FramebufferObject fbo) {

            synchronized (this) {
                if (mUpdateSurface) {
                    mPreviewTexture.updateTexImage();
                    mPreviewTexture.getTransformMatrix(mSTMatrix);
                    mUpdateSurface = false;
                }
            }

            if (mUploadTexture) {
                mImageTexture.setup();
                mCameraRatio = (float) mImageTexture.getWidth() / mImageTexture.getHeight();
                Matrix.setIdentityM(mSTMatrix, 0);
                mUploadTexture = false;
            }

            if (mIsNewShader) {
                if (mShader != null) {
                    mShader.setup();
                    mShader.setFrameSize(fbo.getWidth(), fbo.getHeight());
                }
                mIsNewShader = false;
            }

            if (mShader != null) {
                mFramebufferObject.enable();
                glViewport(0, 0, mFramebufferObject.getWidth(), mFramebufferObject.getHeight());
            }

            glClear(GL_COLOR_BUFFER_BIT);

            Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);			// 視野行列とモデルビュー行列を乗算します。
            Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);	// 投影行列と乗算します。

            if (mImageTexture != null) {
                mImageShader.draw(mImageTexture.getTexName(), mMVPMatrix, mSTMatrix, mCameraRatio);
            } else {
                mPreviewShader.draw(mTexName, mMVPMatrix, mSTMatrix, mCameraRatio);
            }

            if (mShader != null) {
                fbo.enable();
                glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
                glClear(GL_COLOR_BUFFER_BIT);
                mShader.draw(mFramebufferObject.getTexName(), fbo);
            }
        }

        @Override
        public synchronized void onFrameAvailable(final PreviewTexture previewTexture) {
            mUpdateSurface = true;
            requestRender();
        }
    }
}