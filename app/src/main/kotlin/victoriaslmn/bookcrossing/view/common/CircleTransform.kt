package victoriaslmn.bookcrossing.view.common;

import android.graphics.*
import com.squareup.picasso.Transformation


class CircleTransform : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.getWidth(), source.getHeight());
        val x = (source.getWidth() - size) / 2;
        val y = (source.getHeight() - size) / 2;

        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        val bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        val canvas = Canvas(bitmap);
        val paint = Paint();
        val shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        val r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();

        return bitmap;
    }

    override fun key(): String {
        return "circle";
    }
}
