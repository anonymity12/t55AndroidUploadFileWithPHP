package com.example.paul.phpretrofitupload;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.paul.phpretrofitupload.Remote.IUploadAPI;
import com.example.paul.phpretrofitupload.Remote.RetrofitClient;
import com.example.paul.phpretrofitupload.Utils.ProgressRequestBody;
import com.example.paul.phpretrofitupload.Utils.UploadCallbacks;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements UploadCallbacks {
    private static final String TAG = "tt1";

    public static String BASE_URL = "http://192.168.1.4/";
    public static final int REQUEST_PERMISSION = 101;
    public static final int PICK_FILE_REQUEST = 1001;
    IUploadAPI mService;

    Button btnUpload;
    ImageView imageView;

    Uri selectedFileUri;
    ProgressDialog dialog;


    private IUploadAPI getAPIUpload() {
        return RetrofitClient.getClient(BASE_URL, true).create(IUploadAPI.class);
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

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_url, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_url:
                show_update_url_dialog();
                 return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void show_update_url_dialog() {
        final EditText et = new EditText(this);
        new AlertDialog.Builder(this).setTitle("input new url")
                .setIcon(R.drawable.ic_update_url)
                .setView(et)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BASE_URL = et.getText().toString();
                        mService = getAPIUpload();
                        Log.e(TAG, "onClick: base url update: " + BASE_URL );
                        Toast.makeText(MainActivity.this, "base Url update: " + BASE_URL, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("cancel", null).show();
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
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

    private void uploadFile() {
        if (selectedFileUri != null) {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();

            File file = FileUtils.getFile(this, selectedFileUri);
            ProgressRequestBody requestFile = new ProgressRequestBody(file, this);

            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
            Log.e(TAG, "uploadFile: file name: " + file.getName());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService.uploadFile(body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onResponse: call:" + call );
                                    Log.e(TAG, "onResponse: resp: " + response);
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    dialog.dismiss();
                                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }

    @Override
    public void onProgressUpdate(int percentage) {
        dialog.setProgress(percentage);
    }
}
