package com.notbytes.barcodereader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.notbytes.barcode_reader.BarcodeReaderActivity;
import com.notbytes.barcode_reader.BarcodeReaderFragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class SecondActivity extends AppCompatActivity implements BarcodeReaderFragment.BarcodeReaderListener {
    private static final int BARCODE_READER_ACTIVITY_REQUEST = 1208;
    String product_id;
    BarcodeReaderFragment readerFragment;
    Button btndismiss;
    Dialog epicDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        AlertDialog.Builder flashBuilder = new AlertDialog.Builder(this);

        flashBuilder.setPositiveButton("ON",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        readerFragment = BarcodeReaderFragment.newInstance(true, true, View.VISIBLE);
                        addBarcodeReaderFragment();

                    }
                });

        flashBuilder.setNegativeButton("OFF", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
                addBarcodeReaderFragment();
            }
        });


        flashBuilder.setMessage("Want to turn on the Flash?");
        flashBuilder.setCancelable(false);
        AlertDialog flashAlert = flashBuilder.create();
        flashAlert.show();
    }

    private void addBarcodeReaderFragment() {
        readerFragment.setListener(this);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fm_container, readerFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {

        readerFragment.onPause();

        AlertDialog.Builder aleartBuilder = new AlertDialog.Builder(this);
        aleartBuilder.setMessage("Sure to Exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        readerFragment.onResume();
                    }
                });

        AlertDialog alertDialog = aleartBuilder.create();
        aleartBuilder.show();
    }


    public void  success() {
        epicDialog = new Dialog(this);
        epicDialog.setContentView(R.layout.my_dialog);
        epicDialog.setCancelable(false);
        btndismiss = epicDialog.findViewById(R.id.buttonCan);

        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerFragment.onResume();
                epicDialog.dismiss();
            }
        });
        epicDialog.show();
    }

    public void failed() {
        epicDialog = new Dialog(this);
        epicDialog.setContentView(R.layout.notfound);
        epicDialog.setCancelable(false);
        btndismiss = epicDialog.findViewById(R.id.buttonNF);

        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                readerFragment.onResume();
                epicDialog.dismiss();
            }
        });
        epicDialog.show();
    }

    public void returned() {
        epicDialog = new Dialog(this);
        epicDialog.setContentView(R.layout.returned);
        epicDialog.setCancelable(false);
        btndismiss = epicDialog.findViewById(R.id.buttonRT);

        btndismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readerFragment.onResume();
                epicDialog.dismiss();
            }
        });
        epicDialog.show();
    }



    @Override
    public void onScanned(Barcode barcode) {
        Toast.makeText(this, barcode.rawValue, Toast.LENGTH_SHORT).show();

        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

        readerFragment.onPause();

        AlertDialog.Builder ParentBuilder = new AlertDialog.Builder(this);
        product_id = barcode.rawValue.toString();

        final Background bg = new Background(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);



        builder.setPositiveButton("Delivered",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        bg.execute(product_id,"deliver");


                    }
                });

        builder.setNegativeButton("Revert",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bg.execute(product_id,"return");
            }
        });

        builder.setNeutralButton("Continue",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                readerFragment.onResume();
            }
        });


        builder.setMessage("Detected!  ID: " + product_id);
        builder.setCancelable(false);
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(this, "Camera permission denied!", Toast.LENGTH_LONG).show();
    }


    public class Background extends AsyncTask<String, Void,String> {


        Context context;
        int method;

        public Background(Context context) {
            this.context=context;
        }


        @Override
        protected void onPostExecute(String s) {



            if(s.equals("Not Found")) {
                failed();

            }

            if(s.equals("FoundReturned")) {
                returned();
            }

            if(s.equals("FoundDelivered")) {
                success();
            }


        }


        @Override
        protected String doInBackground(String... voids) {

            String result = "";
            String product_id = voids[0];
            String type = voids[1];

            String constr = "http://accsectiondemo.site11.com/UpdateProductState.php";

            try {
                URL url = new URL(constr);
                HttpURLConnection http= (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));

                String data = URLEncoder.encode("product_id","UTF-8")+"="+URLEncoder.encode(product_id,"UTF-8") + "&&" +
                        URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode(type,"UTF-8");
                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
                String line = "";

                while ((line=reader.readLine()) != null) {

                    result += line;

                }
                reader.close();
                ips.close();
                http.disconnect();
                return result;

            } catch (MalformedURLException e) {
                result = e.getMessage();
            } catch (IOException e) {
                result = e.getMessage();
            }

            return result;
        }
    }

}
