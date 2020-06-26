package com.shubo7868.shubham.facedetectioapp.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.media.midi.MidiManager;
import android.widget.ImageView;

import com.shubo7868.shubham.facedetectioapp.MainActivity;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static android.graphics.Canvas.*;
import static androidx.core.content.ContextCompat.startActivity;

public class ContourOverlay extends GraphicOverlay.Graphic {
    private int mEyeColor = Color.BLUE;
    private int mUppperLipTopColor = Color.CYAN;
    private int mUppperLipBottomColor = Color.MAGENTA;
    private int mLowerLipTopColor = Color.CYAN;
    private int mLowerrLipBottomColor = Color.MAGENTA;
    private int mNoseBridgeColor = Color.RED;
    private int mBottomColor = Color.GREEN;
    private int mFaceColor = Color.CYAN;
    AlertDialog alertDialog;
    private CameraView cameraView;

    private List<FirebaseVisionPoint> faceOval;
    private List<FirebaseVisionPoint> leftEyeContour;
    private List<FirebaseVisionPoint> rightEyeContour;
    private List<FirebaseVisionPoint> upperLipTopContour;
    private List<FirebaseVisionPoint> lowerLipBottomContour;
    private List<FirebaseVisionPoint> noseBridgeContour;





    private float mStrokeWidth = 3.0f;
    private Paint mPaint;

    private GraphicOverlay graphicOverlay;
    private Context context;







    public ContourOverlay(GraphicOverlay graphicOverlay, HashMap<String, List<FirebaseVisionPoint>> FaceData){

        super(graphicOverlay);

        // face
        this.faceOval = FaceData.get("face");
        // eyes
        this.leftEyeContour = FaceData.get("leftEye");
        this.rightEyeContour = FaceData.get("rightEye");
        List<FirebaseVisionPoint> rightEyeContour  = FaceData.get("rightEye");
        // lips
        List<FirebaseVisionPoint> upperLipTopContour = FaceData.get("upperLipTop");
        List<FirebaseVisionPoint> lowerLipTopContour = FaceData.get("lowerLipTop");
        List<FirebaseVisionPoint> upperLipBottomContour = FaceData.get("upperLipBottom");
        List<FirebaseVisionPoint> lowerLipBottomContour = FaceData.get("lowerLipBottom");
        // nose
        this.noseBridgeContour = FaceData.get("noseBridge");
        List<FirebaseVisionPoint> noseBridgeContour = FaceData.get("noseBridge");
        List<FirebaseVisionPoint> noseBottomContour = FaceData.get("noseBottom");




    }

    @Override
    public void draw(Canvas canvas) {





        mPaint = new Paint();
        mPaint.setColor(mFaceColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2.f);
        System.out.println(faceOval.size());





        if(faceOval.size() > 0) {

           /* for(int i = 0; i < faceOval.size()-1; i++) {
                canvas.drawLine(faceOval.get(0).getX(), faceOval.get(0).getY(), faceOval.get(i).getX(), faceOval.get(i).getY(), mPaint);
               // System.out.println("Face points --> "+faceOval.get(i));
            }
*/
            /*for(int i = 0; i < leftEyeContour.size()-1; i++) {
                float minLX = leftEyeContour.get(0).getX();
                float modLeftX;
                float minLY = leftEyeContour.get(0).getY();
                float modLeftY;
                float maxLY = leftEyeContour.get(0).getY();
                float modDownY;
                canvas.drawCircle(leftEyeContour.get(i+1).getX(), leftEyeContour.get(i+1).getY(), 4f, mPaint);
                if(leftEyeContour.get(i).getX() < minLX) {
                    minLX = leftEyeContour.get(i).getX();
                }
                if(leftEyeContour.get(i).getY() < minLY) {
                    minLY = leftEyeContour.get(i).getY();
                }
                if(leftEyeContour.get(i).getY() > maxLY) {
                    maxLY = leftEyeContour.get(i).getY();
                }
                modLeftY = minLY-2;
                modLeftX = minLX-2;
                modDownY = maxLY+2;



                System.out.println("modLeftY "+modLeftY);
                System.out.println("modLeftX "+modLeftX);
                System.out.println("modDownY "+modDownY);
            }*/

           /* for(int i = 0; i < rightEyeContour.size()-1; i++) {
                float maxX = rightEyeContour.get(0).getX();
                float modRightX;
                float minRY = rightEyeContour.get(0).getY();
                float modRightY;
                canvas.drawCircle(rightEyeContour.get(i+1).getX(), rightEyeContour.get(i+1).getY(), 4f, mPaint);
                if(rightEyeContour.get(i).getX() > maxX) {
                    maxX = rightEyeContour.get(i).getX();
                }
                if(rightEyeContour.get(i).getY() < minRY) {
                    minRY = rightEyeContour.get(i).getY();
                }
                modRightX = maxX+2;
                modRightY = minRY-2;
                System.out.println("modRightY "+modRightY);
                System.out.println("modRightX "+modRightX);
            }*/
           /* for(int i = 0; i < noseBridgeContour.size()-1; i++) {
                canvas.drawLine(noseBridgeContour.get(0).getX(), noseBridgeContour.get(0).getY(), noseBridgeContour.get(i+1).getX(), noseBridgeContour.get(i+1).getY(), mPaint);
            }*/


        }


    }



}






