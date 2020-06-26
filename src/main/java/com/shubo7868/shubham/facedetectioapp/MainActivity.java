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
import com.google.firebase.database.FirebaseDatabase;
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


//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.util.Patterns;
//import android.webkit.WebView;
//import android.widget.EditText;
//import android.widget.TextView;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.shubo7868.shubham.gymit.Helper.GraphicOverlay;
//import com.shubo7868.shubham.gymit.Helper.RectOverlay;

//import android.os.Bundle;


//Left eye Detection class, "MainActivity"
public class MainActivity extends AppCompatActivity {

    private Button faceDetectButton;
    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    private Handler mHandler = new Handler();


    public String UHID = "";

    AlertDialog alertDialog;
    private NotificationCompat.Builder mBuilder;

    MediaPlayer player;
    MediaPlayer player1;

    Intent myIntent;
   public Bitmap bitmap, bitmap1;

    boolean flag = false;
    String test;

    ImageView imgV, img;
   // public FirebaseVisionImage face;

    private int Indicator = 0;

    private ImageButton Logout;
    private FirebaseAuth mAuth;

    public Bitmap tempEyeCrop;
    public Bitmap tempFaceCrop;
    public Bitmap leftEyeCrop;
    public Bitmap rightEyeCrop;
    private String selection;

    private static Bitmap transferLeftEyeCrop;

    Intent starterIntent;
    public String statusEye;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.user_login);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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


        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
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



// Below adds voice support to this activity
    public void play1(){
        if(player == null) {
            player = MediaPlayer.create(this, R.raw.lowersectionlrfteye);
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
            player = MediaPlayer.create(this, R.raw.leftsaved);
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
            Toast.makeText(this, "Pull lower LEFT eye part and press TAKE LEFT EYE CONJUNCTIVA", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    // process bitmap for face detection
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
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });





    }



//  Gets contours of each face , so that bitmaps of eye and other face section can be obtained
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
                modRightY = minRY-80;
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
//               imgV = findViewById(R.id.imgView);
//              imgV.setImageDrawable(new BitmapDrawable(getResources(), rightEyeCrop));        //depricated as called in different rightEye class
//
                transferLeftEyeCrop = leftEyeCrop;






            }



            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
            ContourOverlay contourOverlay = new ContourOverlay(graphicOverlay, FaceDataMap);
            graphicOverlay.add(contourOverlay);




            myIntent = new Intent(this, LeftEyeRetakeActivity.class);
            myIntent.putExtra("leftRyePre", leftEyeCrop);



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


        }


        System.out.println("AFTER GETTING BITMAP");


        alertDialog.dismiss();
    }

    public static Bitmap getTransferLeftEyeCrop() {
        return transferLeftEyeCrop;
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void createDirectoryAndSaveFaceFile(Bitmap imageToSave, String fileEndName) {
        Bundle extras = getIntent().getExtras();
        String UHID_QRCODE  = "";
        String UHID = "";
        String UHID_MANUAL = "";



        if(extras != null) {
            if(extras.getString("#UHIDQR") != null) {
                UHID_QRCODE = extras.getString("#UHIDQR");
                UHID = UHID_QRCODE;
                System.out.println(UHID+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@================================@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            }


            if(extras.getString("@UHID") != null) {
                UHID_MANUAL =extras.getString("@UHID");
                UHID = UHID_MANUAL;
                System.out.println("No QRCODE Scanned By user");
            }

        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd :: HH:mm:ss");
        String formattedDate = df.format(c.getTime());     // c is Calendar object
        System.out.println("========> formatted date => "+formattedDate);

        File direct = new File(Environment.getExternalStorageDirectory() + "/FACESCONJUCTIVA");


        String FILENAME = UHID+ " : " + formattedDate + fileEndName + ".png";

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/FACESCONJUCTIVA/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File("/sdcard/FACESCONJUCTIVA/", FILENAME);
        if (file.exists()) {
            file.delete();
        }
        boolean succ = false;
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            succ = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(succ) {
            Toast.makeText(getApplicationContext(), "Face & Eye image saved to FACES", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Error during image saving to FACES", Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
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

}
