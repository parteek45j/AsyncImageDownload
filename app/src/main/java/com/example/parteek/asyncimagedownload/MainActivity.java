package com.example.parteek.asyncimagedownload;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    ProgressBar progressBar;
    Button button;
    EditText editText;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        button=(Button)findViewById(R.id.button);
        editText=(EditText)findViewById(R.id.editText);
        textView=(TextView)findViewById(R.id.text);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MyTask myTask=new MyTask();

        myTask.execute(editText.getText().toString());
    }

    class MyTask extends AsyncTask<String,Integer,Boolean>{
        boolean success=false;
        int counter=0;
        int result=0;
        int lenth=-1;
        HttpURLConnection connection=null;
        InputStream stream=null;
        File file=null;
        FileOutputStream outputStream=null;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            button.setFocusable(false);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            button.setEnabled(true);
            button.setFocusable(true);
            if (success){
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String ... params) {

            try {
                URL url=createUrl(params[0]);
                connection=(HttpURLConnection)url.openConnection();
                lenth=connection.getContentLength();
                stream=connection.getInputStream();
                file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsoluteFile()+"/"+ Uri.parse(params[0]).getLastPathSegment());
                Log.d("Location",file.getAbsolutePath());
                outputStream=new FileOutputStream(file);
                int read=-1;
                byte[] bytes=new byte[1024];
                while ((read=stream.read(bytes))!=-1){
                    outputStream.write(bytes,0,read);
                    counter=counter+read;
                    publishProgress(counter);
                }
                success=true;
            } catch (IOException e) {
                e.printStackTrace();
                success=false;
            }
            finally {
                if(connection!=null){
                    connection.disconnect();
                }
                if (stream!=null){
                    try {
                        stream.close();
                    } catch (IOException e) {

                    }
                }
                if (outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return success;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            result=(int)(((double)values[0]/lenth)*100);
            progressBar.setProgress(result);
            textView.setText(result+"%");
        }

        URL createUrl(String sUrl){
            URL url=null;
            try {
                url=new URL(sUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return url;
        }
    }
}
