package com.shubo7868.shubham.facedetectioapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//Manual Uhid Entry Mode Class
public class UhidEntryActivity extends AppCompatActivity {

    private Button uhidButton;
    public EditText uhidNumber;
    private Button clearText;
    private ImageButton setModeUhid;

    private static String selectionFromUhid;
    private static String statusMode;

    public static Boolean onSettingModeChangeFromUhid = false;

    public static String uhidNumberObtainedManual = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uhidmode);

        uhidButton = findViewById(R.id.acceptUhid);
        uhidNumber = findViewById(R.id.uhid_input);
        clearText = findViewById(R.id.clear_text);
        setModeUhid = findViewById(R.id.set_mode);

        uhidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable numUID = uhidNumber.getText();
                if(numUID != null)  uhidNumberObtainedManual = numUID.toString();

                Intent intent = new Intent(UhidEntryActivity.this, FaceCapture.class);
                intent.putExtra("@UHID",uhidNumberObtainedManual);
                startActivity(intent);
            }
        });

        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uhidNumber.setText("");
            }
        });

        setModeUhid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectModeFromUhid();
            }
        });


    }

    public static String get_uhidNumber() {
        return uhidNumberObtainedManual;
    }
    public static Boolean get_ModeChangedFromUhidActivity() {
        return onSettingModeChangeFromUhid;
    }



    public void SelectModeFromUhid() {
        final android.app.AlertDialog.Builder builder = new AlertDialog.Builder(UhidEntryActivity.this);
        builder.setTitle("Configure in:");
        final String[] colors = UhidEntryActivity.this.getResources().getStringArray(R.array.mode);

        builder.setSingleChoiceItems(R.array.mode, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectionFromUhid = colors[which];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSettingModeChangeFromUhid = true;
                if(selectionFromUhid.equals("Use QR Code Scanning")) {
                    statusMode = selectionFromUhid;
                    startActivity(new Intent(UhidEntryActivity.this, Scanner.class));
                }else if(selectionFromUhid.equals("Use Manual UHID Entry")) {
                    statusMode = selectionFromUhid;
                    startActivity(new Intent(UhidEntryActivity.this, UhidEntryActivity.class));
                }
                System.out.println(statusMode+"FFFFFFFFFFFFFFFFFFFAAAAAAAAAAAAAAAAAAANNNNNNNNNGGGGGGGGGGGGGGGGGGGGGGGGG");
                Toast.makeText(UhidEntryActivity.this, "Selected Option :"+selectionFromUhid, Toast.LENGTH_SHORT).show();
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

    public static String getStatusModeFromUhid() {
        // return  selection1;
        return selectionFromUhid;
    }
}
