package com.shubo7868.shubham.facedetectioapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
/*import com.shubo7868.shubham.facedetectioapp.Helper.CameraEye;*/
import com.shubo7868.shubham.facedetectioapp.Helper.ContourOverlay;
import com.shubo7868.shubham.facedetectioapp.Helper.GraphicOverlay;
import com.shubo7868.shubham.facedetectioapp.Helper.RectOverlay;
/*import com.shubo7868.shubham.facedetectioapp.Gallery;*/
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivityRight extends AppCompatActivity {
    private Button faceDetectButton;
    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    private Handler mHandler = new Handler();

    AlertDialog alertDialog;
    private NotificationCompat.Builder mBuilder;

    MediaPlayer player;
    MediaPlayer player1;

    Intent myIntent;
    public Bitmap bitmap, bitmap1;

    boolean flag = false;
    String test;

    ImageView rightEyeimgV, img;
    ImageView rightEyePreview;
    // public FirebaseVisionImage face;

    private int Indicator = 0;

    private ImageButton Logout;
    private FirebaseAuth mAuth;

    public Bitmap tempEyeCrop;
    public Bitmap tempFaceCrop;
    public Bitmap leftEyeCrop;
    public Bitmap rightEyeCrop;
    private String selection;

    private static Bitmap transferRightEyeCrop;

    Intent starterIntent;
    public String statusEye;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.user_login);
        setContentView(R.layout.activity_righteye);

        starterIntent = getIntent();



        Logout = findViewById(R.id.power);
        faceDetectButton = findViewById(R.id.detect_face_btn);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        cameraView = findViewById(R.id.camera_view);

        mAuth = FirebaseAuth.getInstance();
//        final FirebaseUser user = mAuth.getCurrentUser();

        ImageView myImage = findViewById(R.id.square_img);
        myImage.setAlpha(0.1f);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait, Processing...")
                .setCancelable(false)
                .build();


//        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityRight.this);
//        builder.setTitle("Conjuctiva Status:");
//        final String[] colors = MainActivityRight.this.getResources().getStringArray(R.array.conjuctiva);
//
//        builder.setSingleChoiceItems(R.array.conjuctiva, -1, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                selection = colors[which];
//            }
//        });
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                statusEye = selection;
//                System.out.println(statusEye+"FFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAANNNNNNNNNGGGGGGGGGGGGGGGGGGGGGGGGG");
//                Toast.makeText(MainActivityRight.this, "Selected Option :"+selection, Toast.LENGTH_SHORT).show();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//             //   recreate();
//            }
//        });
//        builder.create();



        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MainActivityRight.this, Main2Activity.class));
                finish();
            }
        });



        faceDetectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });
        if(!flag) {
            for(int j = 0;j < 2; j++) {
                play1();
            }
        }
        int REQ_CODE = 0;
        if(Build.VERSION.SDK_INT > 22) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, REQ_CODE);
        }
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();
                bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();


                processFacedetection(bitmap);
                System.out.println("ERRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOOOORRRRRRRRRRRRRRRRRRRRR");
//                builder.show();




            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


    }




    public void play1(){
        if(player == null) {
            player = MediaPlayer.create(this, R.raw.lowersectionrighteye);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }
    public void play() {
        if(player == null) {
            player = MediaPlayer.create(this, R.raw.rightsaved);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.start();
    }
    public void stop(View v) {
        stopPlayer();

    }
    public void stopPlayer() {
        if(player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "Pull lower RIGHT eye part and press TAKE RIGHT EYE CONJUNCTIVA", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    private void processFacedetection(Bitmap bitmap) {

       /* try {
             bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(saveToInternalStorage(bitmap))));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE )
                .build();
        final FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);

        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        getFaceResults(firebaseVisionFaces);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(MainActivityRight.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivityRight.this, MainActivityRight.class));
                    }
                });





    }

    public String saveToInternalStorage(Bitmap bitmapImage) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //  Toast.makeText(this, directory.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        return directory.getAbsolutePath();

    }

//    private void loadImageFromStorage(String path)
//    {
//
//        try {
//            File f=new File(path, "profile.jpg");
//            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
//            rightEyePreview=(ImageView)findViewById(R.id.righteye_retake);
//            rightEyePreview.setImageBitmap(b);
//        }
//        catch (FileNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//
//        Indicator = 2;
//
//    }


    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter = 0;

        for(FirebaseVisionFace face : firebaseVisionFaces) {
            Rect rect = face.getBoundingBox();

            int x = (int) (face.getBoundingBox().exactCenterX()/2);
            int y = (int) (face.getBoundingBox().exactCenterY()/2);


            // rotations
            float rotY = face.getHeadEulerAngleY();
            float rotZ = face.getHeadEulerAngleZ();

            // landmark detection
   /*        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
            if (leftEar != null) {
                FirebaseVisionPoint leftEarPositon = leftEar.getPosition();

           }
            FirebaseVisionFaceLandmark rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
           if (rightEar != null) {
                FirebaseVisionPoint rightEarPositon = rightEar.getPosition();
           }*/
            // similarly for other landmarks

            // contour detection

            // face
            List<FirebaseVisionPoint> faceOval = face.getContour(FirebaseVisionFaceContour.FACE).getPoints();

            // eyes
            List<FirebaseVisionPoint> leftEyeContour =
                    face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
            List<FirebaseVisionPoint> rightEyeContour =
                    face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).getPoints();
            // lips
            List<FirebaseVisionPoint> upperLipTopContour =
                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).getPoints();
            List<FirebaseVisionPoint> lowerLipTopContour =
                    face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).getPoints();
            List<FirebaseVisionPoint> upperLipBottomContour =
                    face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
            List<FirebaseVisionPoint> lowerLipBottomContour =
                    face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).getPoints();

            // nose
            List<FirebaseVisionPoint> noseBridge = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).getPoints();
            List<FirebaseVisionPoint> noseBottom = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).getPoints();

            HashMap <String, List<FirebaseVisionPoint>> FaceDataMap = new HashMap<>();
            FaceDataMap.put("face", faceOval);
            FaceDataMap.put("leftEye", leftEyeContour);
            FaceDataMap.put("rightEye", rightEyeContour);
            FaceDataMap.put("upperLipTop", upperLipTopContour);
            FaceDataMap.put("lowerLipTop", lowerLipTopContour);
            FaceDataMap.put("upperLipBottom", upperLipBottomContour);
            FaceDataMap.put("lowerLipBottom", lowerLipBottomContour);
            FaceDataMap.put("noseBridge", noseBridge);
            FaceDataMap.put("noseBottom", noseBottom);

            if(faceOval.size() > 0) {
                float minLX =0;
                float modLeftX=0;
                float minLY=0;
                float modLeftY=0;
                float maxLY=0;
                float modDownY=0;

                float maxX=0;
                float modRightX=0;
                float minRY=0;
                float modRightY=0;


                tempFaceCrop  = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());

                for(int i = 0; i < leftEyeContour.size()-1; i++) {
                    minLX = leftEyeContour.get(0).getX();
                    //   modLeftX;
                    minLY = leftEyeContour.get(0).getY();
                    //  float modLeftY;
                    maxLY = leftEyeContour.get(0).getY();
                    //  float modDownY;
                    //  canvas.drawCircle(leftEyeContour.get(i+1).getX(), leftEyeContour.get(i+1).getY(), 4f, mPaint);
                    if(leftEyeContour.get(i).getX() < minLX) {
                        minLX = leftEyeContour.get(i).getX();
                    }
                    if(leftEyeContour.get(i).getY() < minLY) {
                        minLY = leftEyeContour.get(i).getY();
                    }
                    if(leftEyeContour.get(i).getY() > maxLY) {
                        maxLY = leftEyeContour.get(i).getY();
                    }
                }
                modLeftY = minLY-40;
                modLeftX = minLX-10;
                modDownY = maxLY+60;

                System.out.println("modLeftY "+modLeftY);
                System.out.println("modLeftX "+modLeftX);
                System.out.println("modDownY "+modDownY);


                for(int i = 0; i < rightEyeContour.size()-1; i++) {
                    maxX = rightEyeContour.get(0).getX();

                    minRY = rightEyeContour.get(0).getY();

                    //    canvas.drawCircle(rightEyeContour.get(i+1).getX(), rightEyeContour.get(i+1).getY(), 4f, mPaint);
                    if(rightEyeContour.get(i).getX() > maxX) {
                        maxX = rightEyeContour.get(i).getX();
                    }
                    if(rightEyeContour.get(i).getY() < minRY) {
                        minRY = rightEyeContour.get(i).getY();
                    }
                }
                modRightX = maxX+100;
                modRightY = minRY-80;  //???? 40 ---> 60-->
                System.out.println("modRightY "+modRightY);
                System.out.println("modRightX "+modRightX);

                float avgY = (modLeftY+modRightY)/2;
                float widthEye = modRightX-modLeftX;
                float heightEye = modDownY-avgY;

                System.out.println("avgY "+avgY);
                System.out.println("widthEye "+widthEye);
                System.out.println("heightEye "+heightEye);


                int modLeftXX = (int)modLeftX;
                int avgYY= (int)avgY;
                int widthEyee = (int)widthEye;
                int heightEyee = (int)heightEye;

                // getting coordinates for Right Eye

                int avgX = (int)(modLeftXX+modRightX)/2;
                int widthRightEye = (avgX-modLeftXX);
                int widthLeftEye = (int)(modRightX-avgX) ;


                rightEyeCrop = Bitmap.createBitmap(bitmap, modLeftXX, avgYY, widthRightEye, heightEyee);
                leftEyeCrop = Bitmap.createBitmap(bitmap, avgX, avgYY, widthLeftEye, heightEyee);

                tempEyeCrop = Bitmap.createBitmap(bitmap, modLeftXX, avgYY, widthEyee,heightEyee);
//                rightEyeimgV = findViewById(R.id.righteyeimgView);
//                rightEyeimgV.setImageDrawable(new BitmapDrawable(getResources(), rightEyeCrop));
//                   showImageR(bitmap);
                transferRightEyeCrop = rightEyeCrop;

//                if (showImageR(rightEyeCrop)) {
////                   finish();
////                   startActivity(starterIntent);
//
//                   recreate();
//               }




            }



            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
            ContourOverlay contourOverlay = new ContourOverlay(graphicOverlay, FaceDataMap);
            graphicOverlay.add(contourOverlay);


//            Boolean defaultFromQRAccount = Scanner.getDefaultMode();
//            if(defaultFromQRAccount == false) {
//                myIntent = new Intent(this, Scanner.class);
//            }
//            else if(defaultFromQRAccount){
//                String ActionOnMode = Scanner.getStatusMode();
//                if(ActionOnMode.equals("Use QR Code Scanning")) {
//                    myIntent = new Intent(this, Scanner.class);
//                }
//                if(ActionOnMode.equals("Use Manual UHID Entry")) {
//                    myIntent = new Intent(this, UhidEntryActivity.class);
//                }
//            }
//            Boolean modeChangeFromUhidActivity = UhidEntryActivity.get_ModeChangedFromUhidActivity();
//            if(modeChangeFromUhidActivity) {
//                String ActionOnModefromUhid = UhidEntryActivity.getStatusModeFromUhid();
//                if(ActionOnModefromUhid.equals("Use QR Code Scanning")) {
//                    myIntent = new Intent(this,Scanner.class);
//                }
//                if(ActionOnModefromUhid.equals("Use Manual UHID Entry")) {
//                    myIntent = new Intent(this, UhidEntryActivity.class);
//                }
//
//            }


            myIntent = new Intent(MainActivityRight.this, RightEyeRetakeActivity.class);




            try {
//                play();

            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mNewIntent, 10);




            int x1 = (int) face.getBoundingBox().exactCenterX()/2;
            int y1 = (int)face.getBoundingBox().exactCenterY()/2;
            int x2 = face.getBoundingBox().left;
            int y2 = face.getBoundingBox().top;
            int x3 = face.getBoundingBox().right;
            int y3 = face.getBoundingBox().bottom;
            int width1 = Math.abs(x3-x2);
            int height1 =Math.abs(y2-y3);
            int width = face.getBoundingBox().width();
            int height =  face.getBoundingBox().height();

           /* System.out.println("centerX -> "+ face.getBoundingBox().centerX());
            System.out.println("centerY -> "+ face.getBoundingBox().centerY());
            System.out.println("excenter-X -> "+ face.getBoundingBox().exactCenterX());
            System.out.println("excenter-Y -> "+ face.getBoundingBox().exactCenterY());
            System.out.println("right or x3 -> "+ face.getBoundingBox().right);
            System.out.println("left or x2 -> "+ face.getBoundingBox().left);
            System.out.println("top or y2 -> "+ face.getBoundingBox().top);
            System.out.println("bottom or y3 -> "+ face.getBoundingBox().bottom);
            System.out.println("width1 -> "+width1 );
            System.out.println("height1  "+ height1);
            System.out.println("width -> "+ width);
            System.out.println("height -> "+ height);*/


            Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),Bitmap.Config.RGB_565);
            //    Bitmap tempBitmap1 = Bitmap.createBitmap(bitmap, img.getLeft(),img.getTop()+10, bitmap.getWidth()-100, bitmap.getHeight()-100);
            //   Bitmap tempBitmap2 = Bitmap.createBitmap(bitmap,x1, y1, width1,height1 );
//            Canvas tempCanvas = new Canvas(tempBitmap2);
//            tempCanvas.drawBitmap(bitmap, x2, y2,null);



//            Paint myRectPain = new Paint();
//            myRectPain.setStrokeWidth(10);
//            myRectPain.setColor(Color.RED);
//            myRectPain.setStyle(Paint.Style.STROKE);

//            Rect bound = face.getBoundingBox();
//            tempCanvas.drawRect(bound, myRectPain);
           /* for(FirebaseVisionFace face1: firebaseVisionFaces) {

            }*/


            /*ImageView imgV = findViewById(R.id.imgView);
            imgV.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap2));*/


        }


//       loadImageFromStorage(saveToInternalStorage(rightEyeCrop));     //Indicator --> 2   depricated here as called already in MainActivity class
        System.out.println("AFTER GETTING BITMAP");



        /*SavesToInternal(imgV);
        SavesToInternal(img);*/





      //  createDirectoryAndSaveFile(leftEyeCrop, ": LeftEyeConjuctiva");
      //  createDirectoryAndSaveFile(tempFaceCrop, statusEye);



        // loadImageFromStorage(saveToInternalStorage(tempEyeCrop));


        alertDialog.dismiss();
    }
    private Runnable mNewIntent = new Runnable() {

        @Override
        public void run() {
            startActivity(myIntent);
        }
    };
    @Override
    protected void onPause() {
        super.onPause();

        cameraView.stop();
    }
    @Override
    protected void onResume() {
        super.onResume();

        cameraView.start();
    }


    public static Bitmap getTransferRightEyeCrop() {
        return transferRightEyeCrop;
    }




//    private boolean showImageR(Bitmap bitmap) {
//        if(bitmap != null) {
//            imgV = findViewById(R.id.imgView);
//            imgV.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
//            Indicator = 1;
//            return true;
//        }else
//            return false;
//    }
//    private boolean showImageL(Bitmap bitmap) {
//        Indicator = 2;
//        imgV = findViewById(R.id.imgView);
//        imgV.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
//        return true;
//    }





}
