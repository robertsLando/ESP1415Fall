package unipd.dei.ESP1415.falldetector;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

public class GraphView extends View {

	public static boolean LINE = false;

	private Paint paint;
	private float[] valX;
	private float[] valY;
	private float[] valZ;
	//private String[] horlabels;
	private String[] verlabels;
	private String title;
	//private boolean type;

	public GraphView(Context context, float[] valX, float[] valY, float[] valZ, String title, String[] verlabels, boolean type) {
		super(context);
		if (valX == null)
			valX = new float[0];
		else
			this.valY = valY;
		if (valY == null)
			valY = new float[0];
		else
			this.valX = valX;
		if (valZ == null)
			valZ = new float[0];
		else
			this.valZ = valZ;
		if (title == null)
			title = "";
		else
			this.title = title;
		/**if (horlabels == null)
			this.horlabels = new String[0];
		else
			this.horlabels = horlabels;*/
		if (verlabels == null)
			this.verlabels = new String[0];
		else
			this.verlabels = verlabels;
		//this.type = type;
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float border = 20;
		float horstart = border * 2;
		float height = getHeight();
		float width = getWidth() - 1;
		/*float max = getMax();
    float min = getMin();*/
		float max = 15;
		float min = -15;
		float diff = max - min;
		float graphheight = height - (2 * border);
		float graphwidth = width - (2 * border);

		paint.setTextAlign(Align.LEFT);
		paint.setTextSize(30f);
		int vers = verlabels.length - 1;
		for (int i = 0; i < verlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float y = ((graphheight / vers) * i) + border;
			canvas.drawLine(horstart, y, width, y, paint);
			paint.setColor(Color.BLACK);
			canvas.drawText(verlabels[i], 0, y, paint);
		}
		/**int hors = horlabels.length - 1;
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(Color.DKGRAY);
			float x = ((graphwidth / hors) * i) + horstart;
			canvas.drawLine(x, height - border, x, border, paint);
			paint.setTextAlign(Align.CENTER);
			if (i==horlabels.length-1)
				paint.setTextAlign(Align.RIGHT);
			if (i==0)
				paint.setTextAlign(Align.LEFT);
			paint.setColor(Color.WHITE);
			canvas.drawText(horlabels[i], x, height - 4, paint);
		}*/

		paint.setTextAlign(Align.CENTER);
		canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);

		if (max != min) {
			paint.setColor(Color.RED);

			float datalength = valX.length;
			float colwidth = (width - (2 * border)) / datalength;
			float halfcol = colwidth / 2;
			float lasth = 0;
			float lasth1 = 0;
			float lasth2 = 0;
			for (int i = 0; i < (valX.length - 1); i++) {
				paint.setColor(Color.RED);
				float val = valX[i] - min;
				float rat = val /diff;
				float h = graphheight * rat;
				canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h) + graphheight, paint);
				lasth = h;

				paint.setColor(Color.BLUE);
				float val1 = (valY[i]) - min;
				float rat1 = val1 /diff;
				float h1 = graphheight * rat1;
				canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth1) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h1) + graphheight, paint);
				lasth1 = h1;

				paint.setColor(Color.GREEN);
				float val2 = (valZ[i]) - min;
				float rat2 = val2 /diff;
				float h2 = graphheight * rat2;
				canvas.drawLine(((i - 1) * colwidth) + (horstart + 1) + halfcol, (border - lasth2) + graphheight, (i * colwidth) + (horstart + 1) + halfcol, (border - h2) + graphheight, paint);
				lasth2 = h2;
			}
		}

	}
}

/**private float getMax() {
		float largest = Integer.MIN_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] > largest)
				largest = values[i];
		return largest;
	}

	private float getMin() {
		float smallest = Integer.MAX_VALUE;
		for (int i = 0; i < values.length; i++)
			if (values[i] < smallest)
				smallest = values[i];
		return smallest;
	}

}*/
