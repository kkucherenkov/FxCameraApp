package com.af.experiments.FxCameraApp.display;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DisplayHelperBase implements DisplayHelper {
    private Context mContext;

    public DisplayHelperBase(final Context context) {
        mContext = context;
    }
    protected final Context getContext() {
        return mContext;
    }

    protected final Display getDefaultDisplay() {
        return ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getDisplayAngle() {
        return getDefaultDisplay().getOrientation();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Point getDisplaySize() {
        final Display display = getDefaultDisplay();
        return new Point(display.getWidth(), display.getHeight());
    }

    @Override
    public Point getRawDisplaySize() {
        return getDisplaySize();
    }

}
