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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//This activity previews left eye conjunctiva image

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
    private StorageReference storageRef;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lefteye_retake);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //Firebase reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
//        mRootRefLeft = new Firebase("https://face-detection-ml-app.firebaseio.com/Students");
//        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://face-detection-ml-app.appspot.com");
        // changed storage
        mRootRefLeft = new Firebase("https://hk-schoolkids-project.firebaseio.com/hk-schoolkids-project");
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://hk-schoolkids-project.appspot.com");

       // mRootRefLeft.keepSynced(true);
        // trying cache thing
        storageRef = storage.getReference();



        retakeLeft = findViewById(R.id.button_retake);
        okTakeRight = findViewById(R.id.button_OK);
        leftEyePreview = findViewById(R.id.lefteye_retake);

        // getting bitmap transferred so that it can be saved to internal and firebase
        leftEyeCropImg = MainActivity.getTransferLeftEyeCrop();
        loadImageFromStorage(saveToInternalStorage(leftEyeCropImg));

        okTakeRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Below Creates file and saves bitmap to internal storage
                createDirectoryAndSaveFile(leftEyeCropImg, ": LeftEyeConjuctiva");

                //Firebase Storage reference
                Uri leftEyeUri = getImageUri(LeftEyeRetakeActivity.this, leftEyeCropImg);
                StorageReference filepath = mStorage.child("ConjunctivaImages").child(FilenameFirebase);
                filepath.putFile(leftEyeUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(LeftEyeRetakeActivity.this, "Uploaded LeftEye",Toast.LENGTH_LONG).show();


                // if Uhid details needed move it through...
            }

        });
                startActivity(new Intent(LeftEyeRetakeActivity.this, MainActivityRight.class));

            }
        });

        retakeLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeftEyeRetakeActivity.this, MainActivity.class));
            }
        });

    }
    // Below method takes Bitmap(image) and timestamps so could be saved to firebase later
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "LeftEyeTitle"+(System.currentTimeMillis()), null);
        return Uri.parse(path);
    }


    // Takes bitmap to save
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

@TargetApi(Build.VERSION_CODES.N)
private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileEndName) {

    Bundle extras = getIntent().getExtras();
    String UHID_QRCODE  = Scanner.getUhID();
    String UHID = "";
    String UHID_MANUAL = UhidEntryActivity.get_uhidNumber();


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
