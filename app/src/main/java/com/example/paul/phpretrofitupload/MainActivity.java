package com.example.paul.phpretrofitupload;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    private void chooseFile() {
        Intent getContentIntent = Intent.createChooser(
                FileUtils.createGetContentIntent(), "Select a file");
        startActivityForResult(getContentIntent, PICK_FILE_REQUEST);
    }

}
