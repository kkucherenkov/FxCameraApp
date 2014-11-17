package com.af.experiments.FxCameraApp.camera;

import android.content.Context;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraHelperICS extends CameraHelperHonycomb {

    public CameraHelperICS(final Context context) {
        super(context);
    }

    @Override
    public void initializeFocusMode() {
        final List<String> supportedFocusModes = getSupportedFocusModes();
        if (supportedFocusModes != null) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                final Camera.Parameters parameters = getCamera().getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                try {
                    getCamera().setParameters(parameters);
                } catch (final RuntimeException e) {
                }    // 無視する
            } else {
                super.initializeFocusMode();
            }
        }
    }

    @Override
    public int getMaxNumFocusAreas() {
        return getCamera().getParameters().getMaxNumFocusAreas();
    }

    @Override
    public List<CameraHelper.AreaCompat> getFocusAreas() {
        return repackCompatAreas(getCamera().getParameters().getFocusAreas());
    }

    @Override
    public void setFocusAreas(final CameraHelper.AreaCompat... focusAreas) {
        setFocusAreas(Arrays.asList(focusAreas));
    }

    @Override
    public void setFocusAreas(final List<CameraHelper.AreaCompat> focusAreas) {
        final Camera.Parameters params = getCamera().getParameters();
        params.setFocusAreas(repackInternalAreas(focusAreas));
        getCamera().setParameters(params);
    }

    @Override
    public int getMaxNumMeteringAreas() {
        return getCamera().getParameters().getMaxNumMeteringAreas();
    }

    @Override
    public List<CameraHelper.AreaCompat> getMeteringAreas() {
        return repackCompatAreas(getCamera().getParameters().getMeteringAreas());
    }

    @Override
    public void setMeteringAreas(final CameraHelper.AreaCompat... meteringAreas) {
        setMeteringAreas(Arrays.asList(meteringAreas));
    }

    @Override
    public void setMeteringAreas(final List<CameraHelper.AreaCompat> meteringAreas) {
        final Camera.Parameters params = getCamera().getParameters();
        params.setMeteringAreas(repackInternalAreas(meteringAreas));
        getCamera().setParameters(params);
    }

    protected static final List<CameraHelper.AreaCompat> repackCompatAreas(final List<Camera.Area> areas) {
        if (areas == null) {
            return null;
        }

        final List<CameraHelper.AreaCompat> results = new ArrayList<CameraHelper.AreaCompat>(areas.size());
        for (final Camera.Area area : areas) {
            results.add(new CameraHelper.AreaCompat(area.rect, area.weight));
        }
        return results;
    }

    protected static final List<Camera.Area> repackInternalAreas(final List<CameraHelper.AreaCompat> areas) {
        List<Camera.Area> results = null;

        if (areas != null) {
            results = new ArrayList<Camera.Area>(areas.size());
            for (final CameraHelper.AreaCompat area : areas) {
                results.add(new Camera.Area(area.rect, area.weight));
            }
        }

        return results;
    }

    @Override
    public boolean isAutoExposureLockSupported() {
        return getCamera().getParameters().isAutoExposureLockSupported();
    }

    @Override
    public void setAutoExposureLock(final boolean toggle) {
        final Camera.Parameters params = getCamera().getParameters();
        params.setAutoExposureLock(toggle);
        getCamera().setParameters(params);
    }

    ;

    @Override
    public boolean getAutoExposureLock() {
        return getCamera().getParameters().getAutoExposureLock();
    }

    @Override
    public boolean isAutoWhiteBalanceLockSupported() {
        return getCamera().getParameters().isAutoWhiteBalanceLockSupported();
    }

    @Override
    public void setAutoWhiteBalanceLock(final boolean toggle) {
        final Camera.Parameters params = getCamera().getParameters();
        params.setAutoWhiteBalanceLock(toggle);
        getCamera().setParameters(params);
    }

    @Override
    public boolean getAutoWhiteBalanceLock() {
        return getCamera().getParameters().getAutoWhiteBalanceLock();
    }

    @Override
    public boolean isVideoSnapshotSupported() {
        return getCamera().getParameters().isVideoSnapshotSupported();
    }
}