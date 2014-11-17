package com.af.experiments.FxCameraApp.display;

import android.content.Context;
import android.graphics.Point;

public class DisplayHelperHoneycombMR2 extends DisplayHelperHoneycomb {

        public DisplayHelperHoneycombMR2(final Context context) {
            super(context);
        }

        @Override
        public Point getDisplaySize() {
            final Point point = new Point();
            getDefaultDisplay().getSize(point);
            return point;
        }
}
