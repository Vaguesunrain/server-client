package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class CircleImage extends AppCompatImageView {

    public CircleImage(Context context) {
        super(context);
    }

    public CircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取 ImageView 的宽度和高度
        int width = getWidth();
        int height = getHeight();

        // 创建一个用于绘制圆形的路径
        Path path = new Path();
        path.addCircle(width / 2f, height / 2f, Math.min(width, height) / 2f, Path.Direction.CCW);

        // 创建一个用于裁剪的画布
        canvas.save();
        canvas.clipPath(path);

        // 调用父类的绘制方法，以便绘制图像
        super.onDraw(canvas);

        // 还原画布状态
        canvas.restore();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        // 将传入的 Bitmap 转换为圆形图片
        if (bm != null) {
            Bitmap circularBitmap = createCircularBitmap(bm);
            super.setImageBitmap(circularBitmap);
        } else {
            super.setImageBitmap(bm);
        }
    }

    private Bitmap createCircularBitmap(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int diameter = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);

        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(src, (width - diameter) / 2f, (height - diameter) / 2f, paint);

        return output;
    }
}
