package com.example.baseBallAndroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;


/**
 * Created by Randall on 5/27/2014.
 */


public class UploadFile extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_upload);
    }

    public void selectFile(View view) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        startActivityForResult(Intent.createChooser(i, "Pick a photo"), 1);
        String x = "made it back!";
    }

    public void onActivityResult(int requestCode, int resultCode, Intent fileData) {

        //new fileUpload().execute(fileData);
        try {
            URI uri = new URI(fileData.getData().toString());

            File file = new File(getRealPathFromURI(UploadFile.this, Uri.parse(uri.toString())));

            RequestParams params = new RequestParams();
            params.put("Connection", "Keep-Alive");
            params.put("Cache-Control", "no-cache");
            params.put("Content-Type", "multi-part/form-data");
            params.put("name", "androidTestfile.mp4");

            try{
                params.put("fileBinary", file);
            }
            catch(FileNotFoundException e) {
                Log.d("DEBUG:", e.getMessage());
            }

            //emulator's localhost IP
            String urlString = "http://10.0.2.2:8080/api/videos";

            //Home network ip address
            //String urlString = "http://10.0.0.2:8080/api/videos";


            BaseBallRestClient.post(urlString, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                    String x = "something";
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    String y = "something";
                }
            });
        }
        catch(Exception e) {
            Log.d("DEBUG", e.getMessage());
        }

    }

    //3 paramaters in the ASYNC task is <Params, Progress, Result>
    public class fileUpload extends AsyncTask<Intent, Void, String> {

        @Override
        protected String doInBackground(Intent... fileData) {

            ProgressDialog dialog = null;

            int serverResponseCode = 0;

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            DataOutputStream dos = null;

            try {
                //URL url = new URL(urlString);

                //Need to get URI to get absolute path to file location
                URI uri = new URI(fileData[0].getData().toString());

                File file = new File(getRealPathFromURI(UploadFile.this, Uri.parse(uri.toString())));

                RequestParams params = new RequestParams();
                params.put("Connection", "Keep-Alive");
                params.put("Cache-Control", "no-cache");
                params.put("Content-Type", "multi-part/form-data");
                params.put("name", "androidTestfile.mp4");

                try{
                    params.put("fileBinary", file);
                }
                catch(FileNotFoundException e) {
                    return e.getMessage();
                }

                //emulator's localhost IP
                String urlString = "http://10.0.2.2:8080/api/videos";

                //Home network ip address
                //String urlString = "http://10.0.0.2:8080/api/videos";


                BaseBallRestClient.post(urlString, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String x = "something";
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        String y = "something";
                    }
                });

            }
            catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UploadFile.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }

            return "Video Uploaded!";
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}