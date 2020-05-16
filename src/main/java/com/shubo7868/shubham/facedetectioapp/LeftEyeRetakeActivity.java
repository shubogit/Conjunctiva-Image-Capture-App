package com.shubo7868.shubham.facedetectioapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
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

public class LeftEyeRetakeActivity extends AppCompatActivity {

    private Button retakeLeft;
    private Button  okTakeRight;
   public static ImageView leftEyePreview ;
    public static Bitmap leftEyeCropImg;

    public String UHID = "";
    public static String FilenameFirebase;


    //Firebase releated
    private Firebase mRootRefLeft;
    private StorageReference mStorage;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lefteye_retake);


        //Firebase
        mRootRefLeft = new Firebase("https://face-detection-ml-app.firebaseio.com/Students");
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://face-detection-ml-app.appspot.com");


        retakeLeft = findViewById(R.id.button_retake);
        okTakeRight = findViewById(R.id.button_OK);
        leftEyePreview = findViewById(R.id.lefteye_retake);

        leftEyeCropImg = MainActivity.getTransferLeftEyeCrop();
        loadImageFromStorage(saveToInternalStorage(leftEyeCropImg));

        okTakeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                createDirectoryAndSaveFile(leftEyeCropImg, ": LeftEyeConjuctiva");
                // Saving to Firebase
//                Firebase childRefLeft = mRootRefLeft.child(FilenameFirebase);
//                childRefLeft.setValue(leftEyeCropImg);

                //Firebase Storage reference
                Uri leftEyeUri = getImageUri(LeftEyeRetakeActivity.this, leftEyeCropImg);
                StorageReference filepath = mStorage.child("FACES").child(FilenameFirebase);
                filepath.putFile(leftEyeUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(LeftEyeRetakeActivity.this, "Uploaded",Toast.LENGTH_LONG).show();

                startActivity(new Intent(LeftEyeRetakeActivity.this, MainActivityRight.class));
                // if Uhid details needed move it through...
            }
        });









            }
        });
        //It was inside ok OK take right eye button call
        retakeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeftEyeRetakeActivity.this, MainActivity.class));
            }
        });

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "LeftEyeTitle"+(System.currentTimeMillis()), null);
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
            leftEyePreview=(ImageView)findViewById(R.id.lefteye_retake);
            leftEyePreview.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }



    }
//    @TargetApi(Build.VERSION_CODES.N)
//    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileEndName) {
//
//        Bundle extras = getIntent().getExtras();
//        String UHID_QRCODE  = "";
//
//        String UHID_MANUAL = "";
//        //  Bundle extras1 = getIntent().getExtras();
//      /*  if(extras != null) {
//             UHID_MANUAL =extras.getString("@UHID");
//             UHID = UHID_MANUAL;
//        }else {
//           System.out.println("No Manual Entry from user");
////            UHID_MANUAL =extras.getString("@UHID");
////            UHID= UHID_MANUAL;
//            UHID_QRCODE = extras.getString("#UHIDQR");
//            UHID = UHID_QRCODE;
//        }*/
//
//
//        //  UHID_MANUAL = getIntent().getStringExtra("@UHID");
//
//
//        if(extras != null) {
//            if(extras.getString("#UHIDQR") != null) {
//                UHID_QRCODE = extras.getString("#UHIDQR");
//                UHID = UHID_QRCODE;
//                System.out.println(UHID+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@================================@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//            }
//
//
//            if(extras.getString("@UHID") != null) {
//                UHID_MANUAL = extras.getString("@UHID");
//                UHID = UHID_MANUAL;
//                System.out.println("No QRCODE Scanned By user");
//            }
//
//        }
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd :: HH:mm:ss");
//        String formattedDate = df.format(c.getTime());     // c is Calendar object
//        System.out.println("========> formatted date => "+formattedDate);
//
//        File direct = new File(Environment.getExternalStorageDirectory() + "/FACES");
//
//
//        String FILENAME = UHID+ " : " + formattedDate + fileEndName + ".jpeg";
//
//        if (!direct.exists()) {
//            File wallpaperDirectory = new File("/sdcard/FACES/");
//            wallpaperDirectory.mkdirs();
//        }
//
//        File file = new File("/sdcard/FACES/", FILENAME);
//        if (file.exists()) {
//            file.delete();
//        }
//        boolean succ = false;
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//            succ = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if(succ) {
//            Toast.makeText(getApplicationContext(), "Face & Eye image saved to FACES", Toast.LENGTH_LONG).show();
//        }
//        else {
//            Toast.makeText(getApplicationContext(),
//                    "Error during image saving to FACES", Toast.LENGTH_LONG).show();
//        }
//    }
@TargetApi(Build.VERSION_CODES.N)
private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileEndName) {

    Bundle extras = getIntent().getExtras();
    String UHID_QRCODE  = Scanner.getUhID();
    String UHID = "";
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
     FilenameFirebase = UHID+ " : " + formattedDate + ": LeftEyeConjuctiva" + ".png";

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
