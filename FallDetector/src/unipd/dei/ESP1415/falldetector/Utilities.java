package unipd.dei.ESP1415.falldetector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Log;
import android.widget.ImageView;

public class Utilities {

	/**
	 * A method to get a date in the format dd-MM-yyyy HH:mm:ss
	 * 
	 * @param date
	 *            the date to format
	 * @return the date formatted
	 */
	public static String getDate(Date date) {

		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"dd-MM-yyyy HH:mm:ss");
			return format.format(date);
		} catch (Exception e) {
			Log.e("Date format EXCEPTION", e.getMessage());
			return null;
		}
	}

	/**
	 * This method generate a random thumbnail
	 * 
	 * @param v
	 *            the ImageView
	 * @return an array with 2 elements: color[0]: the Bgcolor, color[1] the
	 *         image color
	 */

	public static int[] setRandomBg(ImageView v) {
		Random rnd = new Random();
		int[] color = new int[2];
		color[0] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256));
		color[1] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256));

		if ((isColorDark(color[0]) && isColorDark(color[1]))
				|| (!isColorDark(color[0]) && !isColorDark(color[1]))) {
			while (isColorDark(color[1]))
				color[1] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
						rnd.nextInt(256));

			while (!isColorDark(color[0]))
				color[0] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
						rnd.nextInt(256));

			PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(
					color[1], PorterDuff.Mode.SRC_ATOP);
			v.setColorFilter(colorFilter);
		}

		v.setBackgroundColor(color[0]);

		return color;
	}

	/**
	 * Set the colors of the thumbnail pictures
	 * @param v the imageview
	 * @param bgColor the background color
	 * @param imgColor the icon color
	 */

	public static void setThumbnail(ImageView v, int bgColor, int imgColor) {

		PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(imgColor,
				PorterDuff.Mode.SRC_ATOP);
		v.setColorFilter(colorFilter);

		v.setBackgroundColor(bgColor);
	}

	/**
	 * This method establish if a color is dark or not
	 * 
	 * @param color
	 *            the color to inspect
	 * @return true if color is dark, false if not
	 */
	private static boolean isColorDark(int color) {
		double darkness = 1 - (0.299 * Color.red(color) + 0.587
				* Color.green(color) + 0.114 * Color.blue(color)) / 255;
		if (darkness < 0.5) {
			return false; // It's a light color
		} else {
			return true; // It's a dark color
		}
	}

}
