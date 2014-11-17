package com.af.experiments.FxCameraApp.display;

import android.content.Context;
import android.os.Build;

public final class DisplayHelperFactory {

    private static final int HONEYCOMB_MR2 = 13;
private static final int HONEYCOMB = 11;

    private static final int FROYO = 8;

    public static DisplayHelper newDisplayHelper(final Context context) {
        @SuppressWarnings("deprecation")
        final int version = Integer.parseInt(Build.VERSION.SDK);
        if (version >= HONEYCOMB_MR2) {
            return new DisplayHelperHoneycombMR2(context);
        } else if (version >= HONEYCOMB) {
            return new DisplayHelperHoneycomb(context);
        } else if (version >= FROYO) {
            return new DisplayHelperFroyo(context);
        }
        return new DisplayHelperBase(context);
    }

    private DisplayHelperFactory() {}

}