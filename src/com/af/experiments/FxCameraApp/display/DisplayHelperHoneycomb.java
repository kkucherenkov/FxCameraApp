package com.af.experiments.FxCameraApp.display;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class DisplayHelperHoneycomb extends DisplayHelperFroyo {

    public DisplayHelperHoneycomb(final Context context) {
        super(context);
    }

    @Override
    public Point getRawDisplaySize() {
        final Display display = getDefaultDisplay();
        try {
            return new Point(
                    (Integer) Display.class.getMethod("getRawWidth").invoke(display),
                    (Integer) Display.class.getMethod("getRawHeight").invoke(display)
            );
        } catch (final Exception e) {
            return super.getRawDisplaySize();
        }
    }

}
