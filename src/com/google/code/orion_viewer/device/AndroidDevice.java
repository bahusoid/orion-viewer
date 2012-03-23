package com.google.code.orion_viewer.device;

/*
 * Orion Viewer is a pdf and djvu viewer for android devices
 *
 * Copyright (C) 2011-2012  Michael Bogdanov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.graphics.Point;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;
import com.google.code.orion_viewer.*;
import universe.constellation.orion.viewer.R;
import universe.constellation.orion.viewer.prefs.GlobalOptions;

/**
 * User: mike
 * Date: 17.12.11
 * Time: 17:02
 */
public class AndroidDevice implements Device {

    protected static final int NOOK_PAGE_UP_KEY_RIGHT = 95;

    protected static final int NOOK_PAGE_DOWN_KEY_RIGHT = 94;

    protected static final int NOOK_PAGE_UP_KEY_LEFT = 93;

    protected static final int NOOK_PAGE_DOWN_KEY_LEFT = 92;

    private PowerManager.WakeLock screenLock;

    protected OrionBaseActivity activity;

    private static int DELAY = 60000;

    private static int VIEWER_DELAY = 600000;

    private int delay = DELAY;

    public GlobalOptions options;

    public AndroidDevice() {

    }

    public void updateTitle(String title) {

    }

    public void updatePageNumber(int current, int max) {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event, OperationHolder holder) {
        //check mapped keys
        if (options != null) {
            if (keyCode == options.getNextKey()) {
                holder.value = NEXT;
                return true;
            }

            if (keyCode == options.getPrevKey()) {
                holder.value = PREV;
                return true;
            }

        }

        if (Info.NOOK2) {
            switch (keyCode) {
                case NOOK_PAGE_UP_KEY_LEFT:
                case NOOK_PAGE_UP_KEY_RIGHT:
                    holder.value = PREV;
                    return true;
                case NOOK_PAGE_DOWN_KEY_LEFT:
                case NOOK_PAGE_DOWN_KEY_RIGHT:
                    holder.value = NEXT;
                    return true;
                }
        }

        if (Info.SONY_PRS_T1) {
            if (keyCode == 0) {
                 switch (event.getScanCode()) {
                    case 105:
                        holder.value = PREV;
                        return true;
                    case 106:
                        holder.value = NEXT;
                        return true;
                    }
            }

        }

        if (keyCode == KeyEvent.KEYCODE_SOFT_LEFT || keyCode == KeyEvent.KEYCODE_SOFT_RIGHT) {
            holder.value = keyCode == KeyEvent.KEYCODE_SOFT_LEFT ? PREV : NEXT;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            holder.value = PREV ;
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            holder.value = NEXT;
            return true;
        }
        return false;
    }

    public void onCreate(OrionBaseActivity activity) {
        options = activity.getOrionContext().getOptions();
        if (activity.getViewerType() == VIEWER_ACTIVITY) {
            delay = VIEWER_DELAY;
        }
        this.activity = activity;
        PowerManager power = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        screenLock = power.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "OrionViewer" + hashCode());
        screenLock.setReferenceCounted(false);
    }

    public void onPause() {
        if (screenLock != null) {
            screenLock.release();
        }
    }

    public void onResume() {
        if (screenLock != null) {
            screenLock.acquire(delay);
        }
    }


    public void onUserInteraction() {
        if (screenLock != null) {
            screenLock.acquire(delay);
        }
    }

    public void flushBitmap(int delay) {
        if (activity.getView() != null) {
            activity.getView().invalidate();
        }
    }

    public int getLayoutId() {
        return R.layout.android_main;
    }

    public String getDefaultDirectory() {
        return "";
    }

    public int getFileManagerLayoutId() {
        return R.layout.android_file_manager;
    }

    public int getHelpLayoutId() {
        return R.layout.android_help;
    }

    public boolean optionViaDialog() {
        return true;
    }

    public void onSetContentView() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Point getDeviceSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return new Point(display.getWidth(), display.getHeight());

    }
}
