package com.example.task3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    EditText editurl;
    Button downloadBTN;
    boolean connected = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        editurl = (EditText) findViewById(R.id.editURL);
        img = (ImageView) findViewById(R.id.imageView);
        downloadBTN = (Button) findViewById(R.id.downloadBTN);
        
            downloadBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        connected = true;
                    }
                    else{
                        connected = false;
                    }
                    if(connected){
                        if (editurl.getText().toString().equals("") || !URLUtil.isValidUrl(editurl.getText().toString()) ) {
                            Toast.makeText(MainActivity.this, "Internet Connected but Please Enter a valid URL", Toast.LENGTH_SHORT).show();
                        } else {
                            DownloadTask task = new DownloadTask(context);
                            task.execute(editurl.getText().toString());
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    class DownloadTask extends AsyncTask<String, String, Bitmap> {
        Context context;
        ProgressDialog progressDialog;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    "Progress Dialog", null);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap result = null;
            result = imgDownload(urls[0]);
            if (result != null)
            {
                return result;
            }
            else{
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... text) {
            progressDialog.setMessage(text[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null)
            {
                img.setImageBitmap(bitmap);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Downloaded Successfully", Toast.LENGTH_SHORT).show();
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "No image Found/Url doesn't contain an image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Bitmap imgDownload (String url){
        Bitmap result = null;
        URL urlObject = null;
        HttpURLConnection con;
        InputStream is = null;
        try{
            urlObject = new URL(url);
            con = (HttpURLConnection)urlObject.openConnection();
            con.connect();
            is = con.getInputStream();
            result  = BitmapFactory.decodeStream(is);
        } catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }
}