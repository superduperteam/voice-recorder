package com.superduperteam.voicerecorder.voicerecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


// taken from: https://stackoverflow.com/questions/14295427/android-audio-recording-with-voice-level-visualization/34551064#34551064
// first answer

    public class VisualizerView extends View {
        private static final int LINE_WIDTH = 1; // width of visualizer lines
        private static final int LINE_SCALE = 13; // scales visualizer lines
        private List<Float> amplitudes; // amplitudes for line lengths
        private int width; // width of this View
        private int height; // height of this View
        private Paint linePaint; // specifies line drawing characteristics

        // constructor
        public VisualizerView(Context context, AttributeSet attrs) {
            super(context, attrs); // call superclass constructor
            linePaint = new Paint(); // create Paint for lines
            linePaint.setColor(Color.RED); // set color to green
            linePaint.setStrokeWidth(LINE_WIDTH); // set stroke width
            linePaint.setStyle(Paint.Style.STROKE);
        }

        // called when the dimensions of the View change
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            width = w; // new width of this View
            height = h; // new height of this View
            amplitudes = new ArrayList<Float>(width / LINE_WIDTH);
        }

        // clear all amplitudes to prepare for a new visualization
        public void clear() {
            amplitudes.clear();
        }

        // add the given amplitude to the amplitudes ArrayList
        public void addAmplitude(float amplitude) {
            amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

            // if the power lines completely fill the VisualizerView
            if (amplitudes.size() * LINE_WIDTH >= width) {
                amplitudes.remove(0); // remove oldest power value
            }
        }

//         draw the visualizer with scaled lines representing the amplitudes
        @Override
        public void onDraw(Canvas canvas) {
            int middle = height / 2; // get the middle of the View
            float curX = 0; // start curX at zero

            // for each item in the amplitudes ArrayList
            for (float power : amplitudes) {
                float scaledHeight = power / LINE_SCALE; // scale the power
                curX += LINE_WIDTH; // increase X by LINE_WIDTH

                // draw a line representing this item in the amplitudes ArrayList
                canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                        - scaledHeight / 2, linePaint);
            }
        }

//        public static byte floatToByteArray(float value) {
//            int intBits =  Float.floatToIntBits(value);
//            return (byte) (intBits >> 24);
//        }

//        Paint middleLine = new Paint();
//        @Override
//        protected void onDraw(Canvas canvas) {
//            if (amplitudes.size() != 0) {
//                middleLine.setColor(Color.RED);
//                int density = 70;
//                int gap = 4;
//                float barWidth = getWidth() / density;
//                float div = amplitudes.size() / density;
//                canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, middleLine);
//                linePaint.setStrokeWidth(barWidth - 4);
//
//                for(byte amp : amplitudes){
//                    for (int i = 0; i < density; i++) {
//                        int bytePosition = (int) Math.ceil(i * div);
//                        int top = getHeight() / 2 + (128 - Math.abs(amplitudes.get(bytePosition))) * (getHeight() / 2) / 128;
//
//                        int bottom = getHeight() / 2 - (128 - Math.abs(amplitudes.get(bytePosition))) * (getHeight() / 2) / 128;
//
//                        float barX = (i * barWidth) + (barWidth / 2);
//                        canvas.drawLine(barX, bottom, barX, getHeight() / 2, linePaint);
//                        canvas.drawLine(barX, top, barX, getHeight() / 2, linePaint);
//                    }
//                }
//                super.onDraw(canvas);
//            }
//        }

    }
