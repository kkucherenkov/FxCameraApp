package com.af.experiments.FxCameraApp.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapFactoryUtils {
    private BitmapFactoryUtils() {}

    public static Bitmap decodeFile(final String filename, final int maxSize, final boolean square) throws IOException {
        final int angle = ExifUtils.getAngle(filename);

        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, opts);

        final int size = Math.max(opts.outWidth, opts.outHeight);
        if (size > maxSize) {
            opts.inSampleSize = size / maxSize;
        } else {
            opts.inSampleSize = 1;
        }

        Bitmap bitmap = decodeFile(filename, opts.inSampleSize, 0, 2);
        if (angle != 0) {
            final Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            final Bitmap _bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();
            bitmap = _bitmap;
        }
        if (square && bitmap.getWidth() != bitmap.getHeight()) {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                final Bitmap _bitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - bitmap.getHeight()) / 2, 0, bitmap.getHeight(), bitmap.getHeight());
                bitmap.recycle();
                bitmap = _bitmap;
            } else if (bitmap.getWidth() < bitmap.getHeight()) {
                final Bitmap _bitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getWidth());
                bitmap.recycle();
                bitmap = _bitmap;
            }
        }
        return bitmap;
    }
    public static Bitmap decodeStream(final InputStream is) {
        return decodeStream(is, 1, 0, 2);
    }

    public static Bitmap decodeStream(final InputStream is, final int startInSampleSize, final int add, final int multi) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        int inSampleSize = startInSampleSize;
        while (true) {
            opts.inSampleSize = inSampleSize;
            opts.inDither = true;
            try {
                return BitmapFactory.decodeStream(is, null, opts);
            } catch (final OutOfMemoryError e) {
                inSampleSize = (inSampleSize + add) * multi;
            }
        }
    }

    public static Bitmap decodeResource(final Resources res, final int id) {
        return decodeResource(res, id, 1, 0, 2);
    }

    public static Bitmap decodeResource(final Resources res, final int id, final int startInSampleSize, final int add, final int multi) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        int inSampleSize = startInSampleSize;
        while (true) {
            opts.inSampleSize = inSampleSize;
            opts.inDither = true;
            try {
                return BitmapFactory.decodeResource(res, id, opts);
            } catch (final OutOfMemoryError e) {
                inSampleSize = (inSampleSize + add) * multi;
            }
        }
    }

    public static Bitmap decodeFile(final String pathName) {
        return decodeFile(pathName, 1, 0, 2);
    }

    public static Bitmap decodeFile(final String pathName, final int startInSampleSize, final int add, final int multi) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        int inSampleSize = startInSampleSize;
        while (true) {
            opts.inSampleSize = inSampleSize;
            opts.inDither = true;
            try {
                return BitmapFactory.decodeFile(pathName, opts);
            } catch (final OutOfMemoryError e) {
                inSampleSize = (inSampleSize + add) * multi;
            }
        }
    }

    public static Bitmap decodeStream(final Context context, final String name, final BitmapFactory.Options opts) throws FileNotFoundException {
        final InputStream in = new BufferedInputStream(context.openFileInput(name));
        try {
            return BitmapFactory.decodeStream(in, null, opts);
        } finally {
            try {
                in.close();
            } catch (final IOException e) {}	// 無視する
        }
    }


    public static Bitmap decodeByteArray(final byte[] data, final Bitmap.Config config) {
        return decodeByteArray(data, 0, data.length, config);
    }

    public static Bitmap decodeByteArray(final byte[] data, final int offset, final int length, final Bitmap.Config config) {
        Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(data, offset, length);
        if (bitmap.getConfig().compareTo(config) == 0) {
            return bitmap;
        }
        final int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        bitmap.recycle();

        return Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), config);
    }
}
