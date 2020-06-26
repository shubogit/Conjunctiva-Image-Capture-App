package com.shubo7868.shubham.facedetectioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.shubo7868.shubham.facedetectioapp.Helper.GraphicOverlay;
import com.shubo7868.shubham.facedetectioapp.Helper.RectOverlay;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

// This is QR CODE DETECTOR ACTIVITY (This Activity id depricated, and not in use). It processes image for barcode detection
public class AccountActivity extends AppCompatActivity {


    private CameraView cameraView;
    private Button btnDetect;
    private AlertDialog waitingDialog;
    private ImageButton onSettingMode;
    private GraphicOverlay graphicOverlay;

    private static String selection1;
    private static String statusMode;

    private static boolean onSettingModeClicked = false;
    private static boolean modeChangeFromQRActivity = false;

    private static String m_Text = "";
    private static String uhID = "";

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        graphicOverlay = findViewById(R.id.graphic_overlay);
        onSettingMode = findViewById(R.id.askID);
        cameraView = findViewById(R.id.cameraview);
        btnDetect = findViewById(R.id.btn_detect);
        waitingDialog = new SpotsDialog.Builder()
                    .setMessage("Scanning Please Wait..")
                    .setContext(this)
                .setCancelable(false)
                .build();

        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });


        onSettingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             onSettingModeClicked = true;
            SelectMode();

            }
        });

    cameraView.addCameraKitListener(new CameraKitEventListener() {
        @Override
        public void onEvent(CameraKitEvent cameraKitEvent) {

        }

        @Override
        public void onError(CameraKitError cameraKitError) {

        }

        @Override
        public void onImage(CameraKitImage cameraKitImage) {
            waitingDialog.show();
            Bitmap bitmap = cameraKitImage.getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
            cameraView.stop();

            runDetector(bitmap);
        }

        @Override
        public void onVideo(CameraKitVideo cameraKitVideo) {

        }
    });






    }

    public static boolean getDefaultMode() {
        return onSettingModeClicked;
    }

    public static String getStatusMode() {
       // return  selection1;    // changed just now .. will use it to carry selected mode in order to set it as per directed until change again
        return statusMode;
    }
    public static String getUhID() {
        return uhID;
    }
    public static String getM_Text() {
        return m_Text;
    }


    private void runDetector(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE,
                        FirebaseVisionBarcode.FORMAT_PDF417,
                        FirebaseVisionBarcode.FORMAT_AZTEC
                )
                .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        processResult(firebaseVisionBarcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void SelectMode() {
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
        builder.setTitle("Confiqure in:");
        final String[] colors = AccountActivity.this.getResources().getStringArray(R.array.mode);

        builder.setSingleChoiceItems(R.array.mode, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selection1 = colors[which];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selection1.equals("Use QR Code Scanning")) {
                    statusMode = selection1;
                    startActivity(new Intent(AccountActivity.this, AccountActivity.class));
                }else if(selection1.equals("Use Manual UHID Entry")) {
                        statusMode = selection1;
                        startActivity(new Intent(AccountActivity.this, UhidEntryActivity.class));
                    }
                System.out.println(statusMode+"FFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAANNNNNNNNNGGGGGGGGGGGGGGGGGGGGGGGGG");
                Toast.makeText(AccountActivity.this, "Selected Option :"+selection1, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        for(FirebaseVisionBarcode item: firebaseVisionBarcodes) {
            Rect itemBoundingBox = item.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay,itemBoundingBox);
            graphicOverlay.add(rectOverlay);
            int value_type = item.getValueType();



            switch(value_type)
            {
                case FirebaseVisionBarcode.TYPE_TEXT: {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

                    builder.setMessage(item.getRawValue());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {






                            dialogInterface.dismiss();
                        }
                    });

                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;

                case FirebaseVisionBarcode.TYPE_URL:
//                {//starts browser URL
//                    System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSShIIIIIIIIIIIIIIIIIIIITTTTTTTTTTTTTTTTT");
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRawValue()));
//                    startActivity(intent);
//                }
                break;

                case FirebaseVisionBarcode.TYPE_CONTACT_INFO:
                {
//                    String info = new StringBuilder("Name: ")
//                            .append(item.getContactInfo().getName().getFormattedName())
//                            .append("\n")
//                            .append("Address: ")
//                            .append(item.getContactInfo().getAddresses().get(0).getAddressLines())
//                            .append("\n")
//                            .append("Email: ")
//                            .append(item.getContactInfo().getEmails().get(0).getAddress())
//                            .toString();
//                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
//                    builder.setMessage(info);
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int which) {
//                            dialogInterface.dismiss();
//                        }
//                    });
//                    androidx.appcompat.app.AlertDialog dialog = builder.create();
//                    dialog.show();
                }
                break;

                case FirebaseVisionBarcode.TYPE_WIFI:
//                {
//                    String ssid = item.getWifi().getSsid();
//                    String password = item.getWifi().getPassword();
//                    int type = item.getWifi().getEncryptionType();
//                }
                break;

                case FirebaseVisionBarcode.TYPE_PHONE:
                {
                    uhID = item.getPhone().getNumber();
                    System.out.println(uhID+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    if(uhID.length() >= 13) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(true);
                        builder.setTitle("UHID DETECTED: "+uhID);

                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent intent = new Intent(AccountActivity.this, MainActivity.class);

                                        intent.putExtra("#UHIDQR", uhID);

                                        startActivity(intent);
                                       // dialog.dismiss();
                                    }
                                });
                        builder.setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                       // startActivity(new Intent(AccountActivity.this, MainActivity.class));


                    }
                }
                break;



                default:
                    break;
            }
        }

        waitingDialog.dismiss();
    }


//    public void AlertPopUp() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //
//        builder.setTitle("Enter UHID");
//
//// Set up the input
//        final EditText input = new EditText(this);
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
////      input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
////       // input.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_CLASS_NUMBER);
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
//        builder.setView(input);
//
//// Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                m_Text = input.getText().toString();
//                System.out.println(m_Text);
//                if(m_Text.length() >= 13) {
//                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
//
//                    intent.putExtra("@UHID", m_Text);
//
//                    startActivity(intent);
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }




}
