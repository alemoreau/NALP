package com.enseirb.alemoreau.nalp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Alexandre on 11/04/2015.
 */
public class Graph extends View {


    private final int nZones;
    private final int nCouleurs;

    boolean mShowText;
    int mTextPos;

    private Paint mBluePolyPaint;
    private Paint mPinkPolyPaint;
    private Paint mGreenPolyPaint;
    private Paint mOrangePolyPaint;
    private Paint mYellowPolyPaint;
    private Paint mLinesPaint;
    private Paint mLinesPaintIncorrect;

    private Paint[] mC;
    private Path[] mP;
    private double[] mX;
    private double[] mY;
    private Voronoi mVoronoi;
    private List<GraphEdge> mLines;
    private int viewWidth;
    private int viewHeight;
    private int mScore;
    private int mLast;
    private Paint[] mL;
    private float minArea;
    private boolean mComplete;

    public Graph(Context context, AttributeSet attrs, int nZones, int nCouleurs) {
        super(context, attrs);
        init();
        this.nZones = nZones;
        this.nCouleurs = nCouleurs;
        System.out.println("----> " + nCouleurs);
        this.mScore = 0;
        this.mLast = -1;
        this.mComplete = false;
        this.minArea = 5000; //todo : screen size dependent
        mC = new Paint[nZones];
        for (int i = 0; i < nZones; i++) {
            mC[i] = mYellowPolyPaint;
        }
        mP = new Path[nZones];
        mL = new Paint[nZones];
        mX = new double[nZones];
        mY = new double[nZones];

    }
    public Graph(Context context, AttributeSet attrs, int nZones, float minArea) {
        super(context, attrs);
        init();
        this.nZones = nZones;
        this.minArea = minArea;
        this.nCouleurs = 4;
        this.mScore = 0;
        this.mLast = -1;

        mC = new Paint[nZones];
        for (int i = 0; i < nZones; i++) {
            mC[i] = mYellowPolyPaint;
            System.out.println(mC[i]);
        }
        mP = new Path[nZones];
        mL = new Paint[nZones];
        mX = new double[nZones];
        mY = new double[nZones];
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Graph,
                0, 0);

        try {
            mTextPos = a.getInteger(R.styleable.Graph_labelPosition, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
        if (mVoronoi == null){
            buildVoronoi();
        }

        //else
          //  updateVoronoi();//todo ?

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < nZones; i++){
            if (mC[i] != null && mP[i] != null)
                canvas.drawPath(mP[i], mC[i]);
        }
        for (int i = 0; i < nZones; i++){
            if (mP[i] != null) {
                canvas.drawPath(mP[i], mL[i]);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mComplete)
            return false;
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                float min = Float.MAX_VALUE;
                int indice_min = 0;
                for (int i = 0; i < nZones; i++){
                    if (mP[i] != null) {
                        float distance = (float) ((x - mX[i]) * (x - mX[i]) + (y - mY[i]) * (y - mY[i]));
                        if (distance < min) {
                            min = distance;
                            indice_min = i;
                        }
                    }
                }
                mC[indice_min] = nextColor(mC[indice_min]);
                if (mLast != indice_min){
                    mLast = indice_min;
                    mScore++;
                }
                invalidate();
                break;
            }

            case MotionEvent.ACTION_MOVE: { // todo
                /*
                final float x = event.getX();
                final float y = event.getY();

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                mScaleFactor += Math.signum(dx) * Math.max(Math.abs(dx), Math.abs(dy)) / 100;
                if (mScaleFactor < 0.1f)
                    mScaleFactor = 0.1f;
                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
                System.out.println("Scale : " + mScaleFactor);
                // Invalidate to request a redraw
                invalidate();
                */
                break;
            }
        }

        return true;

    }


    private Paint nextColor(Paint p){
        if (p == null || p.equals(mYellowPolyPaint)){
            return mBluePolyPaint;
        }
        if (p.equals(mBluePolyPaint)){
            return mGreenPolyPaint;
        }
        if (p.equals(mGreenPolyPaint)){
            return mOrangePolyPaint;
        }
        if (p.equals(mOrangePolyPaint)){
            if (nCouleurs == 4)
                return mPinkPolyPaint;
            else
                return mBluePolyPaint;
        }
        if (p.equals(mPinkPolyPaint)){
            return mBluePolyPaint;
        }
        return mBluePolyPaint;
    }
    private void buildVoronoi() {
        double min_x = getPaddingLeft();
        double min_y = getPaddingTop();
        double max_x = viewWidth - getPaddingRight();
        double max_y = viewHeight - getPaddingBottom();
        mVoronoi = new Voronoi(0);
        initRandom(mX, mY, min_x, max_x, min_y, max_y);
        mLines = mVoronoi.generateVoronoi(mX, mY, min_x, max_x, min_y, max_y);

        ArrayList<ArrayList<Point>> mPoints = new ArrayList<ArrayList<Point>>();
        for (int i = 0; i < nZones; i++)
            mPoints.add(new ArrayList<Point>());
        int[] adj = new int[nZones];
        for (GraphEdge e : mLines)
        {
            Point p1 = new Point();
            Point p2 = new Point();
            p1.setPoint(e.x1, e.y1);
            p2.setPoint(e.x2, e.y2);
            if (e.x1 > min_x && e.x2 < max_x && e.y1 > min_y && e.y2 < max_y) {
                mPoints.get(e.site1).add(p1);
                mPoints.get(e.site2).add(p1);
                mPoints.get(e.site1).add(p2);
                mPoints.get(e.site2).add(p2);
            }
        }

        ConvexHull c = new ConvexHull();
        for (int i = 0; i < nZones; i++) {
                mPoints.set(i, c.ConvexHull(mPoints.get(i)));
                if (mPoints.get(i).size() > 2) {
                    float area = 0;
                    int n = mPoints.get(i).size();
                    for( int j = 1; j <= n; ++j )
                        area += mPoints.get(i).get(j%n).x*( mPoints.get(i).get((j+1)%n).y - mPoints.get(i).get((j-1)%n).y );
                    area /= 2;
                    double minAngle = Double.MAX_VALUE;
                    for (int j = 0; j < n; j++){
                        double X12 = (mPoints.get(i).get(j).x - mPoints.get(i).get((n+j-1)%n).x);
                        double Y12 = (mPoints.get(i).get(j).y - mPoints.get(i).get((n+j-1)%n).y);
                        double X13 = (mPoints.get(i).get(j).x - mPoints.get(i).get((j+1)%n).x);
                        double Y13 = (mPoints.get(i).get(j).y - mPoints.get(i).get((j+1)%n).y);
                        double X23 = (mPoints.get(i).get((n+j-1)%n).x - mPoints.get(i).get((j+1)%n).x);
                        double Y23 = (mPoints.get(i).get((n+j-1)%n).y - mPoints.get(i).get((j+1)%n).y);

                        double P12 = Math.sqrt(X12 * X12 + Y12 * Y12);
                        double P13 = Math.sqrt(X13 * X13 + Y13 * Y13);
                        double P23 = Math.sqrt(X23 * X23 + Y23 * Y23);

                        double angle = Math.abs(Math.acos((P12*P12 + P13*P13 - P23*P23) / (2 * P12 * P13)));
                        if (angle < minAngle){
                            minAngle = angle;
                        }
                    }


                    System.out.println("Site : " + i + " Area : " + Math.abs(area));
                    System.out.println("Site : " + i + " Angle : " + Math.abs(minAngle));
                    if (Math.abs(area) > minArea && Math.abs(minAngle) > Math.PI / 6) {
                        mP[i] = new Path();
                        mL[i] = mLinesPaint;
                        boolean first = true;
                        for (Point p : mPoints.get(i)) {
                            if (first) {
                                mP[i].moveTo((float) p.x, (float) p.y);
                                first = false;
                            }
                            else
                                mP[i].lineTo((float) p.x, (float) p.y);
                        }
                        mP[i].close();
                    }
                    else
                        mP[i] = null;
                }
                else
                    mP[i] = null;
            //}
        }
    }

    protected boolean isComplete(){
        if (mComplete)
            return true;
        for (int i = 0; i < nZones; i++){
            mL[i] = mLinesPaint;
        }
        boolean r = true;
        for (GraphEdge e : mLines){
            if (mP[e.site1] != null && mP[e.site2] != null){
                if (mC[e.site1] != null && !(mC[e.site1].equals(mYellowPolyPaint)) && mC[e.site2] != null && !(mC[e.site2].equals(mYellowPolyPaint))) {
                    if (mC[e.site1].equals(mC[e.site2])){
                        mL[e.site1] = mLinesPaintIncorrect;
                        mL[e.site2] = mLinesPaintIncorrect;
                        r = false;
                    }
                    if (mC[e.site1].equals(mYellowPolyPaint) || mC[e.site2].equals(mYellowPolyPaint))
                        r = false;
                }
                else{
                    //System.out.println(e.site1 + " -- " + e.site2);
                    r = false;
                }

            }
        }
        if (r){
            mComplete = true;
        }
        return r;
    }

    private void initRandom(double x[], double y[], double min_x, double max_x, double min_y, double max_y){
        int length = x.length;
        Random r=new Random();

        for (int i = 0; i < length; i++){
            x[i] = min_x + (max_x - min_x) * r.nextDouble();
            y[i] = min_y + (max_y - min_y) * r.nextDouble();
        }
    }

    private void init(){
        mLinesPaint = new Paint();
        mLinesPaint.setColor(Color.BLACK);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeWidth(5);

        mLinesPaintIncorrect = new Paint();
        mLinesPaintIncorrect.setColor(Color.RED);
        mLinesPaintIncorrect.setStyle(Paint.Style.STROKE);
        mLinesPaintIncorrect.setStrokeWidth(5);


        mYellowPolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mYellowPolyPaint.setStyle(Paint.Style.FILL);
        mYellowPolyPaint.setColor(0xffffffd3);

        mBluePolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBluePolyPaint.setStyle(Paint.Style.FILL);
        mBluePolyPaint.setColor(0xff24aae1);

        mPinkPolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPinkPolyPaint.setStyle(Paint.Style.FILL);
        mPinkPolyPaint.setColor(0xffed028c);

        mOrangePolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOrangePolyPaint.setStyle(Paint.Style.FILL);
        mOrangePolyPaint.setColor(0xfff7931b);

        mGreenPolyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGreenPolyPaint.setStyle(Paint.Style.FILL);
        mGreenPolyPaint.setColor(0xff8bc63e);

    }


    public int getScore() {
        return mScore;
    }
    public String complexity()
    {
        if (this.nZones <= 20){
            return "easy";
        }
        if (this.nZones <= 50){
            return "medium";
        }
        return "hard";
    }
/*
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            super.onScaleBegin(detector);
            return true;
        }
    }
    */
}

