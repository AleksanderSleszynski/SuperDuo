package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BookScanner  extends DialogFragment implements ZXingScannerView.ResultHandler{
    // Tutorial from: https://github.com/dm77/barcodescanner
    public static final String BARCODE_SCANNED = "barcode_scanned";
    public static final String BARCODE_DATA = "barcode_data";
    private ZXingScannerView mScannerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.activity_scan, container, false);
        mScannerView = (ZXingScannerView) view.findViewById(R.id.scanner_view);
        return view;
    }

    @Override
    public void handleResult(Result rawResult) {
        if(Utility.isNetworkConnected(getContext())) {
            Toast.makeText(getActivity(),
                    "Contents = " + rawResult.getText() + " Format = " + rawResult.getBarcodeFormat()
                            .toString(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BARCODE_SCANNED);
            intent.putExtra(BARCODE_DATA, rawResult.getText());
            getActivity().sendBroadcast(intent);
        } else {
            Toast.makeText(getContext(), getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.startCamera();
        mScannerView.setResultHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
