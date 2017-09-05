package com.vadim_smirnov.qrscanner.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCodeUtils {

    public static Bitmap generateQRCode(String content, int desiredWidth,
                                        int desiredHeight, int codeColor,
                                        int backgroundColor) throws WriterException {

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(content, BarcodeFormat.QR_CODE, desiredWidth, desiredHeight);

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? codeColor : backgroundColor;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
