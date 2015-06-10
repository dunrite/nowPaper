/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dunrite.now;

import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.util.Calendar;


/**
 * Simple watchface example which loads and displays Muzei images as the background
 */
public class MuzeiWatchface extends CanvasWatchFaceService {

    @Override
    public CanvasWatchFaceService.Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine
            implements Loader.OnLoadCompleteListener<Bitmap> {
        private Paint mBackgroundPaint, mTextPaint;
        private Float mTextXOffset, mTextYOffset;
        private WatchfaceArtworkImageLoader mLoader;
        private Bitmap mImage;
        private String mTime;
        private Calendar mCalendar;



        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setWatchFaceStyle(new WatchFaceStyle.Builder(MuzeiWatchface.this)
                    .setStatusBarGravity(Gravity.TOP | Gravity.CENTER)
                    .setPeekOpacityMode(WatchFaceStyle.PEEK_OPACITY_MODE_TRANSLUCENT)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_VISIBLE)
                    .setShowSystemUiTime(false)
                    .build());
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(getResources().getColor(android.R.color.black));

            // Create the Paint for later use
            mTextPaint = new Paint();
            mTextPaint.setTextSize(60);
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setShadowLayer(1,1,1,Color.BLACK);
            mTextPaint.setAntiAlias(true);

            mCalendar = Calendar.getInstance();

            // In order to make text in the center, we need adjust its position
            mTextXOffset = mTextPaint.measureText("12:00") / 2;
            mTextYOffset = ((mTextPaint.ascent() + mTextPaint.descent()) / 2);

            mLoader = new WatchfaceArtworkImageLoader(MuzeiWatchface.this);
            mLoader.registerListener(0, this);
            mLoader.startLoading();
        }

        private String formatTwoDigitNumber(int hour) {
            return String.format("%02d", hour);
        }

        @Override
        public void onLoadComplete(Loader<Bitmap> loader, Bitmap image) {
            mImage = image;
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            if (isInAmbientMode() || mImage == null) {
                canvas.drawRect(0, 0, width, height, mBackgroundPaint);
            } else {
                canvas.drawBitmap(mImage, (width - mImage.getWidth()) / 2,
                        (height - mImage.getHeight()) / 2, null);
            }
            //get the hour
            String hourString;
            int hour = mCalendar.get(Calendar.HOUR);
            if (hour == 0) {
                hour = 12;
            }

            hourString = String.valueOf(hour);
            //get the minute
            String minuteString = formatTwoDigitNumber(mCalendar.get(Calendar.MINUTE));

            canvas.drawText(hourString + ":" + minuteString,
                    bounds.centerX() - mTextXOffset,
                    bounds.centerY() - mTextYOffset,
                    mTextPaint);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            invalidate();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mLoader != null) {
                mLoader.unregisterListener(this);
                mLoader.reset();
                mLoader = null;
            }
        }
    }
}
