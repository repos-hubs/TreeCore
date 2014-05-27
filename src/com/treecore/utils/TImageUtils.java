package com.treecore.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

public class TImageUtils {
	private static float[] carray = new float[20];

	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int height = bmpOriginal.getHeight();
		int width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		paint.setColorFilter(null);
		c.drawBitmap(bmpGrayscale, 0.0F, 0.0F, paint);
		ColorMatrix cm = new ColorMatrix();
		getValueBlackAndWhite();
		cm.set(carray);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
		return bmpGrayscale;
	}

	public static void getValueSaturation() {
		carray[0] = 5.0F;
		carray[1] = 0.0F;
		carray[2] = 0.0F;
		carray[3] = 0.0F;
		carray[4] = -254.0F;
		carray[5] = 0.0F;
		carray[6] = 5.0F;
		carray[7] = 0.0F;
		carray[8] = 0.0F;
		carray[9] = -254.0F;
		carray[10] = 0.0F;
		carray[11] = 0.0F;
		carray[12] = 5.0F;
		carray[13] = 0.0F;
		carray[14] = -254.0F;
		carray[15] = 0.0F;
		carray[16] = 0.0F;
		carray[17] = 0.0F;
		carray[18] = 5.0F;
		carray[19] = -254.0F;
	}

	private static void getValueBlackAndWhite() {
		carray[0] = 0.308F;
		carray[1] = 0.609F;
		carray[2] = 0.082F;
		carray[3] = 0.0F;
		carray[4] = 0.0F;
		carray[5] = 0.308F;
		carray[6] = 0.609F;
		carray[7] = 0.082F;
		carray[8] = 0.0F;
		carray[9] = 0.0F;
		carray[10] = 0.308F;
		carray[11] = 0.609F;
		carray[12] = 0.082F;
		carray[13] = 0.0F;
		carray[14] = 0.0F;
		carray[15] = 0.0F;
		carray[16] = 0.0F;
		carray[17] = 0.0F;
		carray[18] = 1.0F;
		carray[19] = 0.0F;
	}

	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		int color = -12434878;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(-12434878);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
			int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	public static Bitmap createReflectedImage(Bitmap originalImage) {
		int reflectionGap = 4;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1.0F, -1.0F);

		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height
				+ height / 2, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(originalImage, 0.0F, 0.0F, null);

		Paint defaultPaint = new Paint();
		canvas.drawRect(0.0F, height, width, height + 4, defaultPaint);

		canvas.drawBitmap(reflectionImage, 0.0F, height + 4, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0.0F,
				originalImage.getHeight(), 0.0F,
				bitmapWithReflection.getHeight() + 4, 1895825407, 16777215,
				Shader.TileMode.MIRROR);

		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		canvas.drawRect(0.0F, height, width,
				bitmapWithReflection.getHeight() + 4, paint);

		return bitmapWithReflection;
	}
}