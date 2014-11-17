package com.af.experiments.FxCameraApp.Utils;

import android.util.Log;

import java.io.Writer;

public final class LogWriter extends Writer {

    private final StringBuilder mBuilder = new StringBuilder();
    private final String mTag;

    public LogWriter(final String tag) {
        mTag = tag;
    }

    @Override
    public void close() {
        flushBuilder();
    }

    @Override
    public void flush() {
        flushBuilder();
    }

    @Override
    public void write(final char[] buf, final int offset, final int count) {
        for(int i = 0; i < count; i++) {
            final char c = buf[offset + i];
            if (c == '\n') {
                flushBuilder();
            } else {
                mBuilder.append(c);
            }
        }
    }

    private void flushBuilder() {
        if (mBuilder.length() > 0) {
            Log.v(mTag, mBuilder.toString());
            mBuilder.delete(0, mBuilder.length());
        }
    }

}