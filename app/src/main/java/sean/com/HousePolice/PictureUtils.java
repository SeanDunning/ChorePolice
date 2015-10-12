package sean.com.HousePolice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Sean on 10/11/2015.
 */
public class PictureUtils {
    public static Bitmap getScaledBitmap (String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    } //checks screen size and scales down to match

    public static Bitmap getScaledBitmap (String path, int destWidth, int destHeight) {
        //Read dimensions of image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure how much to scale down
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcHeight > srcWidth) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read and create bitmap
        return BitmapFactory.decodeFile(path, options);
    }

}
