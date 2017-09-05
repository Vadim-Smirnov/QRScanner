package com.vadim_smirnov.qrscanner.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.vadim_smirnov.qrscanner.CameraSource;
import com.vadim_smirnov.qrscanner.Constants;
import com.vadim_smirnov.qrscanner.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        Detector.Processor<Barcode> {

    private BarcodeDetector mBarcodeDetector;
    private CameraSource mCameraSource;

    @BindView(R.id.surface_view_camera)
    SurfaceView mSurfaceViewCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        ButterKnife.bind(this);

        mBarcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        mCameraSource = new CameraSource.Builder(this, mBarcodeDetector)
                .setAutoFocus(true)
                .build();

        mSurfaceViewCamera.getHolder().addCallback(this);
        mBarcodeDetector.setProcessor(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (ActivityCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ScannerActivity.this,
                        new String[]{Manifest.permission.CAMERA}, Constants.PERMISSIONS_REQUEST_CAMERA);
                return;
            }
            mCameraSource.start(mSurfaceViewCamera.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraSource.stop();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> qrCodes = detections.getDetectedItems();
        if (qrCodes.size() == 0) {
            return;
        }
        mBarcodeDetector.release();
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.CONTENT_ARG, qrCodes.valueAt(0).displayValue);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        mCameraSource.start(mSurfaceViewCamera.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


}
