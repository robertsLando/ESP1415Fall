package unipd.dei.ESP1415.falldetector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;


public class ChartXY extends View {

	private Paint mPaint;
	private float[] xValues,yValues;
	private float maxx,maxy,minx,miny,locxAxis,locyAxis;
	private int mArrayLength;
	private int axes = 1;
	
	//constructor
	public ChartXY(Context context, float[] xValues, float[] yValues, int axes) {
		super(context);
		//we pass the arays
		this.xValues=xValues;
		this.yValues=yValues;
		this.axes=axes;
		mArrayLength = xValues.length;
		mPaint = new Paint();
        
	    getAxes(xValues, yValues);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		//from android documentation we know that height and width the pixel of the canvas 
		float canvasHeight = getHeight(); //value in pixel
		float canvasWidth = getWidth();
		
		//we have to transform all the values in pixel (we have to layer the graph)
		int[] xValuesInPixels = toPixel(canvasWidth, minx, maxx, xValues); 
		int[] yValuesInPixels = toPixel(canvasHeight, miny, maxy, yValues);
		int locxAxisInPixels = toPixelInt(canvasHeight, miny, maxy, locxAxis);
		int locyAxisInPixels = toPixelInt(canvasWidth, minx, maxx, locyAxis);
		//description of the axis
		String xAxis = "time";
		String yAxis = "acceleration";

		mPaint.setStrokeWidth(2);
		canvas.drawARGB(255, 255, 255, 255);
		for (int i = 0; i < mArrayLength-1; i++) {
			//now we draw our graph---->using the data from database
			mPaint.setColor(Color.RED);
			//drawLine (float startX, float startY, float stopX, float stopY, Paint mPaint)
			canvas.drawLine(xValuesInPixels[i],canvasHeight-yValuesInPixels[i],xValuesInPixels[i+1],canvasHeight-yValuesInPixels[i+1],mPaint);
		}
		
		mPaint.setColor(Color.BLACK);
		//we draw the x and y axis
		//FROM ANDROID DOCUMENTATION
		//public void drawLine (float startX, float startY, float stopX, float stopY, Paint mPaint)
		canvas.drawLine(0,canvasHeight-locxAxisInPixels,canvasWidth,canvasHeight-locxAxisInPixels,mPaint);
		canvas.drawLine(locyAxisInPixels,0,locyAxisInPixels,canvasHeight,mPaint);
		
		//n controls the number of axes labels
		if (axes!=0){
			float temp = 0.0f;
			int n=10;
			mPaint.setTextAlign(Paint.Align.CENTER);
			mPaint.setTextSize(15.0f);
			for (int i=1;i<=n;i++){
				//FROM ANDROID DOCUMENTATION
				//public void drawText (String text, float x, float y, Paint mPaint)
				temp = Math.round(10*(minx+(i-1)*(maxx-minx)/n))/10;
				canvas.drawText(""+temp, (float)toPixelInt(canvasWidth, minx, maxx, temp),canvasHeight-locxAxisInPixels+20, mPaint);
				temp = Math.round(10*(miny+(i-1)*(maxy-miny)/n))/10;
				canvas.drawText(""+temp, locyAxisInPixels+20,canvasHeight-(float)toPixelInt(canvasHeight, miny, maxy, temp), mPaint);
			}
			//we write the max and the min value of our array
			canvas.drawText(""+maxx, (float)toPixelInt(canvasWidth, minx, maxx, maxx),canvasHeight-locxAxisInPixels+20, mPaint);
			canvas.drawText(""+maxy, locyAxisInPixels+20,canvasHeight-(float)toPixelInt(canvasHeight, miny, maxy, maxy), mPaint);
			//now we have to write what the axis represent
			mPaint.setTextSize(20.0f);
			mPaint.setColor(Color.RED);
			mPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			canvas.drawText(xAxis, canvasWidth/2,canvasHeight-locxAxisInPixels+45, mPaint);
			mPaint.setTextSize(16.0f);
			canvas.drawText(yAxis, locyAxisInPixels-50,canvasHeight/2, mPaint);
		}
		
		
	}
	
	//function to transform our array of float in pixel
	private int[] toPixel(float pixels, float min, float max, float[] value) {
		
		double[] p = new double[value.length];
		int[] pint = new int[value.length];
		
		for (int i = 0; i < value.length; i++) {
			p[i] = .1*pixels+((value[i]-min)/(max-min))*.8*pixels;
			pint[i] = (int)p[i];
		}
		
		return (pint);
	}
	
	//we control all our value and we set the axis in a good way
	private void getAxes(float[] xValues, float[] yValues) {
		
		//we need the max and the min value of x and y
		minx=getMin(xValues);
		miny=getMin(yValues);
		maxx=getMax(xValues);
		maxy=getMax(yValues);
		
		if (minx>=0)
			locyAxis=minx;
		else if (minx<0 && maxx>=0)
			locyAxis=0;
		else
			locyAxis=maxx;
		
		if (miny>=0)
			locxAxis=miny;
		else if (miny<0 && maxy>=0)
			locxAxis=0;
		else
			locxAxis=maxy;
		
	}
	
	//function which transform only a float in pixel
	private int toPixelInt(float pixels, float min, float max, float value) {
		
		double p;
		int pint;
		p = .1*pixels+((value-min)/(max-min))*.8*pixels;
		pint = (int)p;
		return (pint);
	}
    
	//return the max value of the array
	private float getMax(float[] v) {
		float largest = v[0];
		for (int i = 0; i < v.length; i++)
			if (v[i] > largest)
				largest = v[i];
		return largest;
	}
    
	//return the min value of the array
	private float getMin(float[] v) {
		float smallest = v[0];
		for (int i = 0; i < v.length; i++)
			if (v[i] < smallest)
				smallest = v[i];
		return smallest;
	}

}