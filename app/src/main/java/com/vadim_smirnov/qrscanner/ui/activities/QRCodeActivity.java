package com.vadim_smirnov.qrscanner.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.vadim_smirnov.qrscanner.Constants;
import com.vadim_smirnov.qrscanner.R;
import com.vadim_smirnov.qrscanner.utils.QRCodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QRCodeActivity extends AppCompatActivity {

    @BindView(R.id.image_view_qr_code)
    ImageView mImageViewQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        ButterKnife.bind(this);

        String content = getIntent().getStringExtra(Constants.CONTENT_ARG);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int colorCode = sharedPreferences.getInt(Constants.CODE_COLOR, 0xFF000000);
        int colorBackground = sharedPreferences.getInt(Constants.BACKGROUND_COLOR, 0xFFFFFFFF);

        try {
            mImageViewQRCode.setImageBitmap(QRCodeUtils.generateQRCode(content, 500, 500, colorCode, colorBackground));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
