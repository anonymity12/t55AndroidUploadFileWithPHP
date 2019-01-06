package com.example.paul.phpretrofitupload.Remote;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by paul on 2018/12/30
 * last modified at 10:08.
 * Desc:
 */

public interface IUploadAPI {
    @Multipart
    @POST("upload/upload.php")
    Call<String> uploadFile(@Part MultipartBody.Part file);

}