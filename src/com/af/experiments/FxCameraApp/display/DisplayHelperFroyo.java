package com.af.experiments.FxCameraApp.display;

import android.content.Context;
import android.view.Surface;

public class DisplayHelperFroyo extends DisplayHelperBase {
    public DisplayHelperFroyo(final Context context) {
        super(context);
    }

    @Override
    public int getDisplayAngle() {
        switch (getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                throw new IllegalStateException();
        }
    }

}