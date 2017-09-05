package com.vadim_smirnov.qrscanner.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.vadim_smirnov.qrscanner.Constants;
import com.vadim_smirnov.qrscanner.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private int mColorCode;
    private int mColorBackground;

    private SharedPreferences mPreferences;

    @BindView(R.id.edit_text_content)
    EditText mEditTextContent;

    @BindView(R.id.edit_text_decode_content)
    EditText mEditTextDecodeContent;

    @BindView(R.id.image_button_code_color)
    ImageButton mImageButtonCodeColor;

    @BindView(R.id.image_button_background_color)
    ImageButton mImageButtonBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            mEditTextDecodeContent.setText(getIntent().getStringExtra(Constants.CONTENT_ARG));
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initColors();
        setButtonBackgroundColor(mImageButtonCodeColor, mColorCode);
        setButtonBackgroundColor(mImageButtonBackgroundColor, mColorBackground);
    }

    @OnClick({R.id.image_button_background_color, R.id.image_button_code_color,
            R.id.button_generate_code, R.id.button_scan_code})
    void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.image_button_code_color:
                showColorPickerDialog(view, mColorCode, getString(R.string.code_color_text));
                break;
            case R.id.image_button_background_color:
                showColorPickerDialog(view, mColorBackground, getString(R.string.background_color_text));
                break;
            case R.id.button_generate_code:
                if (mEditTextContent.getText().toString().isEmpty()) {
                    mEditTextContent.setError(getString(R.string.field_required));
                    break;
                }
                intent = new Intent(this, QRCodeActivity.class);
                intent.putExtra(Constants.CONTENT_ARG, mEditTextContent.getText().toString());
                startActivity(intent);
                break;
            case R.id.button_scan_code:
                intent = new Intent(this, ScannerActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initColors() {
        mColorCode = mPreferences.getInt(Constants.CODE_COLOR, -1);
        if (mColorCode == -1) {
            mColorCode = 0xFF000000;
            mPreferences.edit().putInt(Constants.CODE_COLOR, mColorCode).apply();
        }

        mColorBackground = mPreferences.getInt(Constants.BACKGROUND_COLOR, -1);
        if (mColorBackground == -1) {
            mColorBackground = 0xFFFFFFFF;
            mPreferences.edit().putInt(Constants.BACKGROUND_COLOR, mColorBackground).apply();
        }
    }

    private void setButtonBackgroundColor(View view, int color) {
        GradientDrawable bgShape = (GradientDrawable) view.getBackground();
        bgShape.setColor(color);
    }

    private void showColorPickerDialog(final View view, final int color, String title) {

        final int[] selectedColor = {color};

        AlertDialog colorPickerDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(R.layout.dialog_color_picker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (view.getId() == R.id.image_button_code_color) {
                            mColorCode = selectedColor[0];
                            mPreferences.edit().putInt(Constants.CODE_COLOR, mColorCode).apply();
                            setButtonBackgroundColor(view, mColorCode);
                            return;
                        }
                        mColorBackground = selectedColor[0];
                        mPreferences.edit().putInt(Constants.BACKGROUND_COLOR, mColorBackground).apply();
                        setButtonBackgroundColor(view, mColorBackground);
                    }
                })
                .create();
        colorPickerDialog.show();

        SeekBar seekBarR = colorPickerDialog.findViewById(R.id.seek_bar_r);
        SeekBar seekBarG = colorPickerDialog.findViewById(R.id.seek_bar_g);
        SeekBar seekBarB = colorPickerDialog.findViewById(R.id.seek_bar_b);

        final ImageView imageViewSelectedColor = colorPickerDialog.findViewById(R.id.image_view_selected_color);

        if (seekBarR == null || seekBarG == null || seekBarB == null || imageViewSelectedColor == null) {
            return;
        }

        seekBarR.setProgress(Color.red(color));
        seekBarG.setProgress(Color.green(color));
        seekBarB.setProgress(Color.blue(color));
        imageViewSelectedColor.setBackgroundColor(color);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (seekBar.getId()) {
                    case R.id.seek_bar_r:
                        selectedColor[0] = Color.rgb(seekBar.getProgress(), Color.green(selectedColor[0]), Color.blue(selectedColor[0]));
                        break;
                    case R.id.seek_bar_g:
                        selectedColor[0] = Color.rgb(Color.red(selectedColor[0]), seekBar.getProgress(), Color.blue(selectedColor[0]));
                        break;
                    case R.id.seek_bar_b:
                        selectedColor[0] = Color.rgb(Color.red(selectedColor[0]), Color.green(selectedColor[0]), seekBar.getProgress());
                        break;
                }
                imageViewSelectedColor.setBackgroundColor(selectedColor[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        seekBarR.setOnSeekBarChangeListener(listener);
        seekBarG.setOnSeekBarChangeListener(listener);
        seekBarB.setOnSeekBarChangeListener(listener);
    }

}
