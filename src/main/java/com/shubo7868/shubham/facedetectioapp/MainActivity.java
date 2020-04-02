package com.shubo7868.shubham.facedetectioapp;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.camera2.params.Face;
import android.media.AudioAttributes;
import android.media.FaceDetector;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

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

public class MainActivity extends AppCompatActivity {

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

    ImageView imgV, img;
   // public FirebaseVisionImage face;



    ConstraintLayout constraintLayout;
    public Bitmap tempEyeCrop;
    public Bitmap tempFaceCrop;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceDetectButton = findViewById(R.id.detect_face_btn);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        cameraView = findViewById(R.id.camera_view);

        ImageView myImage = findViewById(R.id.square_img);
        myImage.setAlpha(0.1f);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait, Processing...")
                .setCancelable(false)
                .build();

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


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    public void play1(){
        if(player == null) {
            player = MediaPlayer.create(this, R.raw.detectface);
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
            player = MediaPlayer.create(this, R.raw.eyesaved);
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
            Toast.makeText(this, "Adjust Face inside SQUARE and press DETECT FACE", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
           img=(ImageView)findViewById(R.id.imgPicker);
           img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }


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
                modRightY = minRY-40;
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

                tempEyeCrop = Bitmap.createBitmap(bitmap, modLeftXX, avgYY, widthEyee,heightEyee);
                imgV = findViewById(R.id.imgView);
              imgV.setImageDrawable(new BitmapDrawable(getResources(), tempEyeCrop));



            }



            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
            ContourOverlay contourOverlay = new ContourOverlay(graphicOverlay, FaceDataMap);

            graphicOverlay.add(contourOverlay);






            myIntent = new Intent(this, MainActivity.class);
            /*new AlertDialog.Builder(this)
                    .setTitle("Eye Detected!")
                    .setMessage("Eye image captured")
                    .show();*/


            try {
                play();

            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mNewIntent, 3000);




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


        loadImageFromStorage(saveToInternalStorage(tempFaceCrop));
        System.out.println("AFTER GETTING BITMAP");



        /*SavesToInternal(imgV);
        SavesToInternal(img);*/



        createDirectoryAndSaveFile(tempEyeCrop);
        createDirectoryAndSaveFile(tempFaceCrop);




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

  /*  public void SavesToInternal(ImageView imageView) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File sdCardDirectory = Environment.getExternalStorageDirectory();
        //creating specific file for image storage
        String DATE = String.valueOf(System.currentTimeMillis());
        String FILENAME = DATE + ".png";

        File image = new File(sdCardDirectory, FILENAME);

        boolean success = false;
        FileOutputStream outputStream;
        try {
            System.out.println("<<???????????????????????????????????????<<<<<<<<<<<<<<<");
            outputStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.flush();
            outputStream.close();
            success = true;
        }catch (FileNotFoundException e) {
            System.out.println(e);
        }catch (IOException e) {
            System.out.println(e);
        }

        if(success) {
            Toast.makeText(getApplicationContext(), "Face & Eye image saved", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Error during image saving", Toast.LENGTH_LONG).show();
        }
    }*/

    private void createDirectoryAndSaveFile(Bitmap imageToSave) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/FACES");

        String DATE = String.valueOf(System.currentTimeMillis());
        String FILENAME = DATE + ".png";

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/FACES/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File("/sdcard/FACES/", FILENAME);
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
        }
    }






}
