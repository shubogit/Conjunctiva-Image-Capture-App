package com.shubo7868.shubham.facedetectioapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Detector;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.shubo7868.shubham.facedetectioapp.Helper.GraphicOverlay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
//This is QR CODE Scanner Class
public class Scanner extends AppCompatActivity {

   CameraView cameraView;
   boolean isDetected = false;
   Button buttonStartAgain;

    private AlertDialog waitingDialog;
    private ImageButton onSettingMode;
    private GraphicOverlay graphicOverlay;

    private static String selection1;
    private static String statusMode;

    private static boolean onSettingModeClicked = false;
    private static boolean modeChangeFromQRActivity = false;



   FirebaseVisionBarcodeDetectorOptions options;
   FirebaseVisionBarcodeDetector detector;


    private static String m_Text = "";
    private static String uhID = "";






   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.realtimeqrscanner);


    onSettingMode = findViewById(R.id.askID);




      Dexter.withActivity(this)
          //   .withPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
             .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
              .withListener(new MultiplePermissionsListener() {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport report) {
                      setupCamera();
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                  }
              }).check();


    onSettingMode.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectMode();
        }
    });

   }




   private void setupCamera() {
    buttonStartAgain = findViewById(R.id.btn_detect);
    buttonStartAgain.setEnabled(isDetected);
    buttonStartAgain.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        isDetected = !isDetected;
        }
    });
    cameraView = findViewById(R.id.camera_view);
    cameraView.setLifecycleOwner(this);
    cameraView.addFrameProcessor(new FrameProcessor() {
        @Override
        public void process(@NonNull Frame frame) {
            processImage(getVisionImageFromFrame(frame));
        }
    });

options = new FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
        .build();

detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

   }

    private void processImage(FirebaseVisionImage visionImageFromFrame) {
       if(!isDetected) {
           detector.detectInImage(visionImageFromFrame)
                   .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                       @Override
                       public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                            processResult(firebaseVisionBarcodes);
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(Scanner.this, ""+e.getMessage(),  Toast.LENGTH_SHORT).show();
                       }
                   });
       }
    }
// Detects different types of QR based on user case
    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

        if( firebaseVisionBarcodes.size() > 0) {
            isDetected = true;
            buttonStartAgain.setEnabled(isDetected);
            for(FirebaseVisionBarcode item: firebaseVisionBarcodes) {
                int value_type = item.getValueType();

                switch (value_type) {
                    case FirebaseVisionBarcode.TYPE_TEXT: {

                        uhID = item.getDisplayValue();
                        System.out.println(uhID+"PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
                        if(uhID.length() >= 13) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setCancelable(true);
                            builder.setTitle("UHID DETECTED: "+uhID);

                            builder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent = new Intent(Scanner.this, FaceCapture.class);
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
                                            startActivity(new Intent(Scanner.this, Scanner.class));
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();

                            // startActivity(new Intent(AccountActivity.this, MainActivity.class));


                        }
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_URL: {
                        creatDialog(item.getRawValue());
                    }
                    break;
                    case FirebaseVisionBarcode.TYPE_PHONE: {
//                        uhID = item.getPhone().getNumber();
//                        System.out.println(uhID+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                        if(uhID.length() >= 13) {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                            builder.setCancelable(true);
//                            builder.setTitle("UHID DETECTED: "+uhID);
//
//                            builder.setPositiveButton("OK",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                            Intent intent = new Intent(Scanner.this, MainActivity.class);
//
//                                            intent.putExtra("#UHIDQR", uhID);
//
//                                            startActivity(intent);
//                                            // dialog.dismiss();
//                                        }
//                                    });
//                            builder.setNegativeButton("Cancel",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                            startActivity(new Intent(Scanner.this, Scanner.class));
//                                            dialog.dismiss();
//                                        }
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//
//                            // startActivity(new Intent(AccountActivity.this, MainActivity.class));
//
//
//                        }
                    }
                    break;
                    default:
                        break;
                }
            }

        }

    }

    private void creatDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
   }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
       byte[] data = frame.getData();

        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
            //    .setRotation(frame.getRotation())
                .build();
    return FirebaseVisionImage.fromByteArray(data, metadata);
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

    public void SelectMode() {
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(Scanner.this);
        builder.setTitle("Configure in:");
        final String[] colors = Scanner.this.getResources().getStringArray(R.array.mode);

        builder.setSingleChoiceItems(R.array.mode, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selection1 = colors[which];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSettingModeClicked = true;
                if(selection1.equals("Use QR Code Scanning")) {
                    statusMode = selection1;
                    startActivity(new Intent(Scanner.this, Scanner.class));
                }else if(selection1.equals("Use Manual UHID Entry")) {

                    statusMode = selection1;
                    startActivity(new Intent(Scanner.this, UhidEntryActivity.class));
                }
                System.out.println(statusMode+"FFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAANNNNNNNNNGGGGGGGGGGGGGGGGGGGGGGGGG");
                Toast.makeText(Scanner.this, "Selected Option :"+selection1, Toast.LENGTH_SHORT).show();
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



}
