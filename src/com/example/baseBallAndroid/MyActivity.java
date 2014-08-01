package com.example.baseBallAndroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void downloadVideo(View view){
        //Test that the device is properly connected
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new downloadVideoRESTCall().execute();

        }
        else {
            // display error
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Unable to complete request.")
                    .setTitle("Connection Error");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



    //TODO: change this method to a class and have it extend ASYNCTask
    //TODO: read this (http://docs.oracle.com/javase/tutorial/java/generics/why.html)
    public class downloadVideoRESTCall extends AsyncTask<String, Integer, String>  {

        //TODO: Implement DownloadManager from the Android API
        // http://developer.android.com/reference/android/app/DownloadManager.html

        //Use this urlString for whatever LAN connection you're on
        //String urlString = "http://10.0.0.2:8080/api/videos/539e5c84a389002013ddb237";

        //This is meant to be used for the emulator's localhost IP
        String urlString = "http://10.0.2.2:8080/api/videos/53a9d8c8f36aaad3e945d13c";
        InputStream is = null;
        int len = 500;


        @Override
        protected String doInBackground(String... videoName) {

            try {
                URL url = new URL(urlString);
                File fileDir = null;
                File file = null;
                if(isExternalStorageWritable()) {
                    fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "Baseball Videos");
                    fileDir.mkdirs();

                    file = new File(fileDir, "testVideo.mp4");
                    file.createNewFile();
                }
                long startTime = System.currentTimeMillis();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000 /* milliseconds */);
                conn.setConnectTimeout(10000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                // Starts the query
                conn.connect();

                int response = conn.getResponseCode();
                Log.d("DEBUG:", "The response is: " + response);
                is = conn.getInputStream();

                int totalSize = conn.getContentLength();


                BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buff = new byte[5 * 1024];

                //Read bytes (and store them) until there is nothing more to read(-1)
                int len;
                int count = 0;
                while ((len = inStream.read(buff)) != -1)
                {
                    outStream.write(buff,0,len);
                }


                //This is to refresh the Android Gallery after the file has been successfully downloaded, otherwise will reuquire the
                //Android device to be rebooted before the user acan see it
                MediaScannerConnection.scanFile(MyActivity.this, new String[] {file.getPath().toString()}, null, null);

                //clean up
                outStream.flush();
                outStream.close();
                inStream.close();

            } catch(IOException e) {
                String x = "something";
                //throw e;
            }
            finally {
                if(is != null) {
                    //is.close();
                }
            }

            return "Rest Video Downloaded";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

        }
    }

    public void viewOrders(View view) {
        Intent intent = new Intent(this, OrderList.class);

        Bundle bundle = new Bundle();
        startActivity(intent);
    }

    public void goToUploadFile (View view){
        Intent intent = new Intent(this, UploadFile.class);
        startActivity(intent);
    }

}
