package com.shubo7868.shubham.facedetectioapp;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class RightEyeRetakeActivity extends AccountActivity{


    private static final String TAG = "";
    private Button okRightEye;
    public static ImageView rightEyePreview;
    public static Bitmap rightEyeCropImg;
    public static Bitmap leftEyeCropImgCC;
    private Button retakeRightEye;

    public String UHID;
    public String customFileNameText = "";

    private String selection;
    public String statusEye;

    Intent myIntentRC;
    private MediaPlayer player;

    // Firebase stuff
    private Firebase mRootRef;
    private StorageReference mStorage;
    private static String FILENAMEright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_righteye_retake);

        //Firebase reference
        mRootRef = new Firebase("https://face-detection-ml-app.firebaseio.com/Students");
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://face-detection-ml-app.appspot.com");

        retakeRightEye = findViewById(R.id.button_retake_right);
        okRightEye = findViewById(R.id.button_OK_right);
        rightEyePreview = findViewById(R.id.righteye_retake);


        retakeRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RightEyeRetakeActivity.this, MainActivityRight.class));
            }
        });



        rightEyeCropImg = MainActivityRight.getTransferRightEyeCrop();
        loadImageFromStorage(saveToInternalStorage(rightEyeCropImg));

        okRightEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                play2();
                //save the taken right eye image
                createDirectoryAndSaveFile(rightEyeCropImg, ": RightEyeConjuctiva");


                final AlertDialog.Builder builder = new AlertDialog.Builder(RightEyeRetakeActivity.this);
                builder.setTitle("Conjunctiva Status:");
                final String[] colors = RightEyeRetakeActivity.this.getResources().getStringArray(R.array.conjuctiva);

                builder.setSingleChoiceItems(R.array.conjuctiva, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection = colors[which];
                    }
                });
                builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statusEye = selection;
                        System.out.println(statusEye+"FFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAANNNNNNNNNGGGGGGGGGGGGGGGGGGGGGGGGG");
                        Toast.makeText(RightEyeRetakeActivity.this, "Selected Option :"+selection, Toast.LENGTH_SHORT).show();

                        // We move the user to Uhid entry mode again.. as configured
                        Boolean defaultFromQRAccount = Scanner.getDefaultMode();
                        if(!defaultFromQRAccount) {
                            myIntentRC = new Intent(RightEyeRetakeActivity.this, Scanner.class);
                        }
                        else if(defaultFromQRAccount){
                            String ActionOnMode = Scanner.getStatusMode();
                            if(ActionOnMode.equals("Use QR Code Scanning")) {
                                myIntentRC = new Intent(RightEyeRetakeActivity.this, Scanner.class);
                            }
                            if(ActionOnMode.equals("Use Manual UHID Entry")) {
                                myIntentRC = new Intent(RightEyeRetakeActivity.this, UhidEntryActivity.class);
                            }
                        }
                        Boolean modeChangeFromUhidActivity = UhidEntryActivity.get_ModeChangedFromUhidActivity();
                        if(modeChangeFromUhidActivity) {
                            String ActionOnModefromUhid = UhidEntryActivity.getStatusModeFromUhid();
                            if(ActionOnModefromUhid.equals("Use QR Code Scanning")) {
                                myIntentRC = new Intent(RightEyeRetakeActivity.this,Scanner.class);
                            }
                            if(ActionOnModefromUhid.equals("Use Manual UHID Entry")) {
                                myIntentRC = new Intent(RightEyeRetakeActivity.this, UhidEntryActivity.class);
                            }

                        }
                        //save text file
                        System.out.println(fileName1+"-------------------------------------------!!!!!!!!!!!!!!!!!!!!---------------------------------");
                        System.out.println(dataToWrite1+"---------------------------------------@@@@@@@@@@@@@@@@@@@@@@@@-------------------------------");
                        String fileName2 = UHID+" IS "+ statusEye;
                        String dataToWrite3 = customFileNameText+ statusEye;
                        writeToFile(fileName2, dataToWrite3);
                        startActivity(myIntentRC);

                        //Sending data to firebase

                       Firebase childRef = mRootRef.child(UHID);
                        childRef.setValue(fileName2);

                        Uri rightEyeUri = getImageUri(RightEyeRetakeActivity.this, rightEyeCropImg);
                        StorageReference filepath = mStorage.child("FACES").child(FILENAMEright);
                        filepath.putFile(rightEyeUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(RightEyeRetakeActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                            }
                        });



                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //   recreate();
                    }
                });
                builder.create();
                builder.show();




                // dialog for Conjuctiva Status
                // Move to uhid/Scan mode
                // correect other activity links of MainActivityRight to this one
                // ??



            }
        });



        leftEyeCropImgCC = MainActivity.getTransferLeftEyeCrop();  // carry for firebase purpose



    }

    public void play2(){
        if(player == null) {
            player = MediaPlayer.create(this, R.raw.eystatusvoice);
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
            Toast.makeText(this, "Adjust Face inside SQUARE and press DETECT LEFT EYE", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "RightEyeTitle"+(System.currentTimeMillis()), null);
        return Uri.parse(path);
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
            rightEyePreview=(ImageView)findViewById(R.id.righteye_retake);
            rightEyePreview.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }



    }

    @TargetApi(Build.VERSION_CODES.N)
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileEndName) {

        Bundle extras = getIntent().getExtras();
        String UHID_QRCODE  = Scanner.getUhID();

        String UHID_MANUAL = UhidEntryActivity.get_uhidNumber();
        //  Bundle extras1 = getIntent().getExtras();
      /*  if(extras != null) {
             UHID_MANUAL =extras.getString("@UHID");
             UHID = UHID_MANUAL;
        }else {
           System.out.println("No Manual Entry from user");
//            UHID_MANUAL =extras.getString("@UHID");
//            UHID= UHID_MANUAL;
            UHID_QRCODE = extras.getString("#UHIDQR");
            UHID = UHID_QRCODE;
        }*/


        //  UHID_MANUAL = getIntent().getStringExtra("@UHID");


//        if(extras != null) {
//            if(extras.getString("#UHIDQRr") != null) {
//                UHID_QRCODE = extras.getString("#UHIDQRr");
//                UHID = UHID_QRCODE;
//                System.out.println(UHID+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@================================@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//            }
//
//
//            if(extras.getString("@UHIDr") != null) {
//                UHID_MANUAL =extras.getString("@UHIDr");
//                UHID = UHID_MANUAL;
//                System.out.println("No QRCODE Scanned By user");
//            }
//
//        }


        if(!UHID_MANUAL.isEmpty()) {
            UHID = UHID_MANUAL;
        }else {
            UHID = UHID_QRCODE;
        }


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd :: HH:mm:ss");
        String formattedDate = df.format(c.getTime());     // c is Calendar object
        System.out.println("========> formatted date => "+formattedDate);

        File direct = new File(Environment.getExternalStorageDirectory() + "/FACES");


        String FILENAME = UHID+ " : " + formattedDate + fileEndName + ".png";
        FILENAMEright = UHID+ " : " + formattedDate + ": RightEyeConjuctiva" + ".png";
        customFileNameText = UHID+ " : " +formattedDate+" : ";

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

    //trying to save text file

//    public void writeToFile(String data)
//    {
//        String path =
//                Environment.getExternalStorageDirectory() + File.separator  + "FACES";
//        // Create the folder.
//        File folder = new File(path);
//        folder.mkdirs();
//
//        // Create the file.
//        File file = new File(folder, "config.txt");
//
//        // Save your stream, don't forget to flush() it before closing it.
//
//        try
//        {
//            file.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(file);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            myOutWriter.append(data);
//
//            myOutWriter.close();
//
//            fOut.flush();
//            fOut.close();
//        }
//        catch (IOException e)
//        {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }


    String dataToWrite1 = customFileNameText + statusEye + ".txt";
    String  fileName1= UHID + "IS" + statusEye;

    public boolean writeToFile(String dataToWrite, String fileName) {

        String directoryPath =
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "FACES"
                        + File.separator;

        Log.d(TAG, "Dumping " + fileName +" At : "+directoryPath);

        // Create the fileDirectory.
        File fileDirectory = new File(directoryPath);

        // Make sure the directoryPath directory exists.
        if (!fileDirectory.exists()) {

            // Make it, if it doesn't exist
            if (fileDirectory.mkdirs()) {
                // Created DIR
                Log.i(TAG, "Log Directory Created Trying to Dump Logs");
            } else {
                // FAILED
                Log.e(TAG, "Error: Failed to Create Log Directory");
                return false;
            }
        } else {
            Log.i(TAG, "Log Directory Exist Trying to Dump Logs");
        }

        try {
            // Create FIle Objec which I need to write
            File fileToWrite = new File(directoryPath, fileName + ".txt");

            // ry to create FIle on card
            if (fileToWrite.createNewFile()) {
                //Create a stream to file path
                FileOutputStream outPutStream = new FileOutputStream(fileToWrite);
                //Create Writer to write STream to file Path
                OutputStreamWriter outPutStreamWriter = new OutputStreamWriter(outPutStream);
                // Stream Byte Data to the file
                outPutStreamWriter.append(dataToWrite);
                //Close Writer
                outPutStreamWriter.close();
                //Clear Stream
                outPutStream.flush();
                //Terminate STream
                outPutStream.close();
                return true;
            } else {
                Log.e(TAG, "Error: Failed to Create Log File");
                return false;
            }

        } catch (IOException e) {
            Log.e("Exception", "Error: File write failed: " + e.toString());
            e.fillInStackTrace();
            return false;
        }
    }

}
