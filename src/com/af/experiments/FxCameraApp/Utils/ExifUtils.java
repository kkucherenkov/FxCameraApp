package com.af.experiments.FxCameraApp.Utils;

import android.location.Location;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExifUtils {

    private static final String TAG = "ExifUtils";

    private static final String DATE_FORMAT = "yyyy:MM:dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATETIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

    private ExifUtils() {}

    public static int getAngle(final String filename) throws IOException {
        final ExifInterface exif = new ExifInterface(filename);
        switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return 0;
        }
    }

    private static final String TAG_GPS_DATESTAMP = "GPSDateStamp";

    public static void save(final String filename, final Date datetime, final int orientation, final Boolean flash, final Location location) throws IOException {
        final ExifInterface exif = new ExifInterface(filename);

        if (datetime != null) {
            exif.setAttribute(ExifInterface.TAG_DATETIME, new SimpleDateFormat(DATETIME_FORMAT, Locale.ENGLISH).format(datetime));
        }
        exif.setAttribute(ExifInterface.TAG_MAKE, Build.MANUFACTURER);
        exif.setAttribute(ExifInterface.TAG_MODEL, Build.MODEL);
        exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(orientation));

        if (flash != null) {
            exif.setAttribute(ExifInterface.TAG_FLASH, String.valueOf(flash.booleanValue() ? 1 : 0));
        }

        if (location != null) {
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, formatExifGpsDMS(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, formatExifGpsDMS(location.getLongitude()));


            exif.setAttribute(TAG_GPS_DATESTAMP, new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(datetime));
        }

        exif.saveAttributes();
    }

    private static String formatExifGpsDMS(final double d) {
        final double degrees = Math.floor(d);
        final double minutes = Math.floor((d - degrees) * 60D);
        final double seconds = (d - degrees - minutes / 60D) * 3600D * 1000D;

        final String _degrees = String.valueOf((int) degrees);
        final String _minutes = String.valueOf((int) minutes);
        final String _seconds = String.valueOf((int) seconds);

        return new StringBuilder()
                .append(_degrees).append("/1,")
                .append(_minutes).append("/1,")
                .append(_seconds.substring(0, Math.min(_seconds.length(), 4))).append("/1000")
                .toString();
    }

    public static void dumpExif(final String filename) throws IOException {
        final ExifInterface exif = new ExifInterface(filename);

        Log.d(TAG, "image width: " + exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, -1));
        Log.d(TAG, "image length: " + exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, -1));
        Log.d(TAG, "orientation: " + exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1));
        Log.d(TAG, "datetime: " + exif.getAttribute(ExifInterface.TAG_DATETIME));
        Log.d(TAG, "make: " + exif.getAttribute(ExifInterface.TAG_MAKE));
        Log.d(TAG, "model: " + exif.getAttribute(ExifInterface.TAG_MODEL));


        Log.d(TAG, "white balance: " + exif.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, -1));
        Log.d(TAG, "flash: " + exif.getAttributeInt(ExifInterface.TAG_FLASH, -1));

        Log.d(TAG, "gps latitude ref: " + exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
        Log.d(TAG, "gps latitude: " + exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));	// 緯度
        Log.d(TAG, "gps longitude ref: " + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));
        Log.d(TAG, "gps longitude: " + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));	// 経度
        Log.d(TAG, "gps altitude ref: " + exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF));
        Log.d(TAG, "gps altitude: " + exif.getAttribute(ExifInterface.TAG_GPS_ALTITUDE));	// 経度

        Log.d(TAG, "gps datestamp: " + exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP));
        Log.d(TAG, "gps timestamp: " + exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP));
    }
}
