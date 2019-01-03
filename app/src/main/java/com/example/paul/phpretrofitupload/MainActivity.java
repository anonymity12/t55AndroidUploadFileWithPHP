package com.example.paul.phpretrofitupload;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.paul.phpretrofitupload.Remote.IUploadAPI;
import com.example.paul.phpretrofitupload.Remote.RetrofitClient;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends AppCompatActivity {


    public static final String BASE_URL = "http://192.169.0.101/";
    public static final int REQUEST_PERMISSION = 101;
    public static final int PICK_FILE_REQUEST = 1001;
    IUploadAPI mService;

    Button btnUpload;
    ImageView imageView;

    Uri selectedFileUri;

    private IUploadAPI getAPIUpload() {
        return RetrofitClient.getClient(BASE_URL).create(IUploadAPI.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }



        mService = getAPIUpload();

        btnUpload = (Button) findViewById(R.id.btn_upload);
        imageView = (ImageView) findViewById (R.id.image_view);

        imageView.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                chooseFile();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    return;
                }
                else {
                    selectedFileUri = data.getData();
                    if (selectedFileUri != null && !selectedFileUri.getPath().isEmpty()) {
                        imageView.setImageURI(selectedFileUri);
                    }
                    else {
                        Toast.makeText(this, "Can't upload file to server, data is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void chooseFile() {
        Intent getContentIntent = Intent.createChooser(
                FileUtils.createGetContentIntent(), "Select a file");
        startActivityForResult(getContentIntent, PICK_FILE_REQUEST);
    }

}
