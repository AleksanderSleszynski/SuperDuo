package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BookScanner  extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    // Tutorial from: https://github.com/dm77/barcodescanner
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    public void startCamera(){
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                mScannerView.startCamera();
            }
        }));
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Intent scannerResultIntent = new Intent();
        scannerResultIntent.putExtra("ean_code", rawResult.getText());
        setResult(AddBook.mySCAN, scannerResultIntent);
        finish();
    }
}
