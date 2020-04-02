/*
package com.shubo7868.shubham.facedetectioapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.util.LocaleData;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

//import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.shubo7868.shubham.facedetectioapp.Helper.GraphicOverlay;

*/
/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 *//*

class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    public Canvas canvas1;
    public Face face;
    int i = 0;
    int flag = 0;

    public Bitmap bitmap;


    FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
        flag = 1;
    }


    */
/**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     *//*

    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    */
/**
     * Draws the face annotations for position on the supplied canvas.
     *//*

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);

       */
/* Log.d("MyTag", "hello "+i);
        i++;
        if(flag == 1) {
             flag = 0;
             canvas1 = canvas;

             new MyAscyncTask().execute("ppppp");

        }*//*

    }
*/
/*
    class MyAsyncTask extends AsyncTask<String, Void, String> {
            private Context context;

            public MyAsyncTask() {

            }

            protected String doInBackground(String... params) {
                try
                {

                    Log.d("MyTag", "face.getWidth() "+face.getWidth());
                    Bitmap temp_bitmap = Bitmap.createBitmap((int)face.getWidth(), (int)face.getHeight(), Bitmap.Config.RGB_565);
                    canvas1.setBitmap(temp_bitmap);


                }
                catch (Exception e)
                {
                    Log.e("MyTag", "I got an error", e);
                    e.printStackTrace();
                }
                Log.d("MyTag", "doInBackground");
                return null;
            }

        protected void onPostExecute(String result) {
            Log.d("MyTag", "onPostExecute " + result);
            // tv2.setText(s);

        }

    }
*//*


}
*/


