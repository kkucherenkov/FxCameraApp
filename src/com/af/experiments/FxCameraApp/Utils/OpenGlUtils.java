package com.af.experiments.FxCameraApp.Utils;


import android.graphics.*;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.*;

import static android.opengl.GLES20.*;

public class OpenGlUtils {

    public static final int GL_HALF_FLOAT_OES = 0x8D61;

    public static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private static final int FLOAT_SIZE_BYTES = 4;

    public static final int NO_TEXTURE = -1;

    public static int loadTexture(final Bitmap img, final int usedTexId) {
        return loadTexture(img, usedTexId, true);
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
            textures[0] = usedTexId;
        }
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

    public static int loadTexture(final IntBuffer data, final Camera.Size size, final int usedTexId) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, size.width, size.height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, size.width,
                    size.height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
            textures[0] = usedTexId;
        }
        return textures[0];
    }

    public static int loadTextureAsBitmap(final IntBuffer data, final Camera.Size size, final int usedTexId) {
        Bitmap bitmap = Bitmap
                .createBitmap(data.array(), size.width, size.height, Bitmap.Config.ARGB_8888);
        return loadTexture(bitmap, usedTexId);
    }

    public static int loadShader(final String strSource, final int iType) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(iType);
        GLES20.glShaderSource(iShader, strSource);
        GLES20.glCompileShader(iShader);
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static int createProgram(final int vertexShader, final int pixelShader) throws GLException {
        final int program = glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Could not create program");
        }

        glAttachShader(program, vertexShader);
        glAttachShader(program, pixelShader);

        glLinkProgram(program);
        final int[] linkStatus = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GL_TRUE) {
            glDeleteProgram(program);
            throw new RuntimeException("Could not link program");
        }
        return program;
    }


    public static int loadProgram(final String strVSource, final String strFSource) {
        int iVShader;
        int iFShader;
        int iProgId;
        int[] link = new int[1];
        iVShader = loadShader(strVSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(strFSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }

        iProgId = GLES20.glCreateProgram();

        GLES20.glAttachShader(iProgId, iVShader);
        GLES20.glAttachShader(iProgId, iFShader);

        GLES20.glLinkProgram(iProgId);

        GLES20.glGetProgramiv(iProgId, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(iVShader);
        GLES20.glDeleteShader(iFShader);
        return iProgId;
    }

    public static float rnd(final float min, final float max) {
        float fRandNum = (float) Math.random();
        return min + (max - min) * fRandNum;
    }

    public static int createBuffer(final float[] data) {
        return createBuffer(toFloatBuffer(data));
    }
    public static int createBuffer(final FloatBuffer data) {
        final int[] buffers = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        updateBufferData(buffers[0], data);
        return buffers[0];
    }

    public static void updateBufferData(final int bufferName, final float[] data) {
        updateBufferData(bufferName, toFloatBuffer(data));
    }

    public static void updateBufferData(final int bufferName, final FloatBuffer data) {
        glBindBuffer(GL_ARRAY_BUFFER, bufferName);
        glBufferData(GL_ARRAY_BUFFER, data.capacity() * FLOAT_SIZE_BYTES, data, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static FloatBuffer toFloatBuffer(final float[] data) {
        final FloatBuffer buffer = ByteBuffer
                .allocateDirect(data.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    public static Bitmap createBitmap(final int[] pixels, final int width, final int height, final Bitmap.Config config, final int orientation, final boolean mirror) {

        final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0, 0, 1, 0, 0,
                0, 1, 0, 0, 0,
                1, 0, 0, 0, 0,
                0, 0, 0, 1, 0
        })));

        final Bitmap bitmap;
        final int diff;
        if ((orientation % 180) == 0) {
            bitmap = Bitmap.createBitmap(width, height, config);
            diff = 0;
        } else {
            bitmap = Bitmap.createBitmap(height, width, config);
            diff = (width - height) / 2;
        }
        final Canvas canvas = new Canvas(bitmap);

        final Matrix matrix = new Matrix();
        matrix.postScale(mirror ? -1.0f : 1.0f, -1.0f, width / 2, height / 2);
        matrix.postRotate(-orientation, width / 2, height / 2);
        if (diff != 0) {
            matrix.postTranslate(-diff, diff);
        }

        canvas.concat(matrix);

        canvas.drawBitmap(pixels, 0, width, 0, 0, width, height, false, paint);

        return bitmap;
    }

    public static void setupSampler(final int target, final int mag, final int min) {
        glTexParameterf(target, GL_TEXTURE_MAG_FILTER, mag);
        glTexParameterf(target, GL_TEXTURE_MIN_FILTER, min);
        glTexParameteri(target, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(target, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public static void texImage2D(final int target, final int level, final Bitmap bitmap, final int border) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        texImage2D(target, level, width, height, border, pixels);
    }

    private static void texImage2D(int target, int level, int width, int height, int border, int[] pixels){
        int size = width * height;
        for (int i = 0; i < size; i++) {
            int p = pixels[i];
            pixels[i] =
                    (((pixels[i]) & 0xFF000000) | // A
                            ((pixels[i] << 16) & 0x00FF0000) | // R
                            ((pixels[i]      ) & 0x0000FF00) | // G
                            ((pixels[i] >> 16) & 0x000000FF)); // B
        }
        Buffer _pixels = IntBuffer.wrap(pixels);
        glTexImage2D(target, level, GL_RGBA, width, height, border, GL_RGBA, GL_UNSIGNED_BYTE, _pixels);

    }

    public static ByteBuffer toByteBuffer(final int size) {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.position(0);
        return buffer;
    }
}
