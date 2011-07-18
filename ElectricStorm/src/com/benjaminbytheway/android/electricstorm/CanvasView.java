/**
 * 
 */
package com.benjaminbytheway.android.electricstorm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author benjaminbytheway
 * 
 */
public class CanvasView extends SurfaceView implements Callback, OnTouchListener
{;
	/** Log Tag for GraphView */
	private static final String LOG_TAG = "GraphView";

	/** The thread that actually draws the animation */
	private GraphThread mThread;

	class GraphThread extends Thread
	{
		/** Handle to the surface manager object we interact with */
		private SurfaceHolder mSurfaceHolder = null;

		/** Message handler used by thread to interact with TextView */
		private Handler mHandler = null;

		/** Handle to the application context, used to e.g. fetch Drawables. */
		private Context mContext = null;

		/** Variable set for determining whether to run the thread or not */
		private boolean mRun = false;

		/**
		 * Current height of the surface/canvas.
		 * 
		 * @see #setSurfaceSize
		 */
		private int mCanvasHeight = 1;

		/**
		 * Current width of the surface/canvas.
		 * 
		 * @see #setSurfaceSize
		 */
		private int mCanvasWidth = 1;

		// *******************************************************
		// This is a test
		// *******************************************************
		

		/** flag for checking if the circle has changed */
		private boolean mCircleChanged = false;

		/** List of circles */
		private List<Circle> mCircles = new ArrayList<Circle>();
		
		/*
		private Float mCircleX = 50f;

		private Float mCircleY = 50f;
		
		private Float mCircle1X = 50f;

		private Float mCircle1Y = 50f;
		
		private Float mCircle2X = 50f;

		private Float mCircle2Y = 50f;
		
		private Float mCircle3X = 50f;

		private Float mCircle3Y = 50f;
		
		private Float mCircleVelX = 0f;
		
		private Float mCircleVelY = 0f;
		
		private Float mCircle1VelX = 0f;

		private Float mCircle1VelY = 0f;
		
		private Float mCircle2VelX = 0f;

		private Float mCircle2VelY = 0f;
		
		private Float mCircle3VelX = 0f;

		private Float mCircle3VelY = 0f;
		
		private Float mCircleAccX = 0f;
		
		private Float mCircleAccY = 0f;
		
		private Float mCircle1AccX = 0f;

		private Float mCircle1AccY = 0f;
		
		private Float mCircle2AccX = 0f;

		private Float mCircle2AccY = 0f;
		
		private Float mCircle3AccX = 0f;

		private Float mCircle3AccY = 0f;
		*/

		public GraphThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler)
		{
			// get handles to some important objects
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			while (mRun)
			{
				Canvas c = null;
				try
				{
					synchronized (mSurfaceHolder)
					{
						//if (mCircleChanged || circlesMoving())
						{
							c = mSurfaceHolder.lockCanvas(null);
							
							mCanvasHeight = c.getHeight();
							mCanvasWidth = c.getWidth();
							calculate();
							doDraw(c);
							mCircleChanged = false;
						}
					}
				}
				finally
				{
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null)
					{
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
		
		private boolean circlesMoving()
		{
			for (Circle circle : mCircles)
			{
				if (circle.getxVelocity() != 0 || circle.getyVelocity() != 0)
				{
					return true;
				}
			}

			// if we get to this point, we know that there isn't anything 
			// that is moving because it hasn't returned yet
			return false;
		}
		
		private void calculate()
		{
			for (Circle circle : mCircles)
			{
				// we make the calculations for the velocity
				// calculate x velocity
				if ((circle.getxVelocity() > 1) || (circle.getxVelocity() < -1))
				{
					circle.setxVelocity(circle.getxVelocity() - circle.getxAcceleration());
				}
				else
				{
					circle.setxVelocity(0f);
				}
				
				// calculate y velocity
				if ((circle.getyVelocity() > 1) || (circle.getyVelocity() < -1))
				{
					circle.setyVelocity(circle.getyVelocity() - circle.getyAcceleration());
				}
				else
				{
					circle.setyVelocity(0f);
				}
				
				// then we make the calculations on the position
				circle.setX(circle.getX() + circle.getxVelocity());
				circle.setY(circle.getY() + circle.getyVelocity());
				
				// then we determine if the object is outside of the canvas area
				// calculate the x position
				if ((circle.getX() < 0 || circle.getX() > mCanvasWidth) && circle.getxVelocity() != 0)
				{
					if (circle.getX() < 0)
					{
						circle.setX(0f);
					}
					else
					{
						circle.setX((float) mCanvasWidth);
					}
					
					circle.setxVelocity(-circle.getxVelocity());
					circle.setxAcceleration(-circle.getxAcceleration());
				}
				
				// calculate the y position
				if ((circle.getY() < 0 || circle.getY() > mCanvasHeight) && circle.getyVelocity() != 0)
				{
					if (circle.getY() < 0)
					{
						circle.setY(0f);
					}
					else
					{
						circle.setY((float) mCanvasHeight);
					}
					
					circle.setyVelocity(-circle.getyVelocity());
					circle.setyAcceleration(-circle.getyAcceleration());
				}
			}
		}

		private void doDraw(Canvas c)
		{
			Paint mBackgroundPaint = new Paint();
			mBackgroundPaint.setAntiAlias(true);
			mBackgroundPaint.setColor(Color.BLACK);
			c.drawRect(new Rect(0, 0, c.getWidth(), c.getHeight()), mBackgroundPaint);
			
			//This is a test for pinching
			//Paint mPaint = new Paint();
			//mPaint.setAntiAlias(true);
			//mPaint.setARGB(255, 0, 0, 255);
			//c.drawRect(mCircleX, mCircleY, mCircle1X, mCircle1Y, mPaint);
			
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			paint.setColor(Color.WHITE);
			//paint.setShader(new LinearGradient(0, mCanvasHeight - mMarginBottom, 0, 0, Color.TRANSPARENT, Color.BLUE, Shader.TileMode.CLAMP));
			//Path path = new Path();
			//path.moveTo(mCircleX, mCircleY);
			//path.lineTo(mCircle1X, mCircle1Y);
			//c.drawPath(path, paint);
			
			List<CirclePair> alreadyDone = new ArrayList<CirclePair>();
			
			// Draw the circles
			for (Circle circle : mCircles)
			{
				for (Circle nearbyCircle : mCircles)
				{
					// We know the order of the CirclePair matters (nearbyCircle first and circle later)
					// because that is the only way it would have been created.
					CirclePair pair = new CirclePair(nearbyCircle, circle);
					
					// Check to see if we have already done this circle, and if so, we skip the rest of the calculations
					if (alreadyDone.contains(pair))
					{
						continue;
					}
					
					CirclePair circlePair = new CirclePair(circle, nearbyCircle);
					alreadyDone.add(circlePair);
					
					double distance = circlePair.getDistance();
					
					// if we get a distance that is greater than a certain amount, we draw the electric
					// storm circles 
					if (distance < 150)
					{
						float xPosition = circlePair.getLowX();
						float yPosition = circlePair.getLowY();
						float xNewPosition = 0;
						float yNewPosition = 0;
						//draw the series of lines that will get us the electric effect.
						while (xPosition < circlePair.getHighX() && yPosition < circlePair.getHighY())
						{
							double xDistance = (Math.random() * 15) + 2;
							double yDistance = (Math.random() * 15) + 2;
							
							xNewPosition = (float) (xPosition + xDistance);
							yNewPosition = (float) (yPosition + yDistance);
							
							if (xNewPosition > circlePair.getHighX())
							{
								xNewPosition = circlePair.getHighX();
							}
							if (yNewPosition > circlePair.getHighY())
							{
								yNewPosition = circlePair.getHighY();
							}
							
							c.drawLine(xPosition, yPosition, xNewPosition, yNewPosition, paint);
							
							xPosition = xNewPosition;
							yPosition = yNewPosition;
						}
					}
				}
			}
			
			// Draw the circles
			for (Circle circle : mCircles)
			{
				Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
				mLinePaint.setShader(new RadialGradient(circle.getX(), circle.getY(), 40, Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP));
				mLinePaint.setAntiAlias(true);
				mLinePaint.setColor(Color.WHITE);
				c.drawCircle(circle.getX(), circle.getY(), 40, mLinePaint);
			}
		}

		/** Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height)
		{
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder)
			{
				mCanvasWidth = width;
				mCanvasHeight = height;
			}
		}

		public void setRunning(boolean b)
		{
			mRun = b;
		}
		
		Float accelerationCoeffiecient = 80f;
		
		public void setCircleVelocity(int index, float x, float y)
		{
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				if (mCircles.size() == 0 || index >= mCircles.size() || mCircles.get(index) == null)
				{
					Circle circle = new Circle();
					circle.setxVelocity(x);
					circle.setyVelocity(y);
					circle.setxAcceleration(x/accelerationCoeffiecient);
					circle.setyAcceleration(y/accelerationCoeffiecient);
					if (index >= mCircles.size())
					{
						for (int i=mCircles.size(); i<=index; i++)
						{
							mCircles.add(i, new Circle());
						}
					}
					mCircles.set(index, circle);
				}
				else
				{
					Circle circle = mCircles.get(index);
					circle.setxVelocity(x);
					circle.setyVelocity(y);
					circle.setxAcceleration(x/accelerationCoeffiecient);
					circle.setyAcceleration(y/accelerationCoeffiecient);
				}
			}
		}
		
		public void setCirclePosition(int index, float x, float y)
		{
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				if (mCircles.size() == 0 || index >= mCircles.size() || mCircles.get(index) == null)
				{
					Circle circle = new Circle();
					circle.setX(x);
					circle.setY(y);
					if (index >= mCircles.size())
					{
						for (int i=mCircles.size(); i<=index; i++)
						{
							mCircles.add(i, new Circle());
						}
					}
					mCircles.set(index, circle);
				}
				else
				{
					Circle circle = mCircles.get(index);
					circle.setX(x);
					circle.setY(y);
				}
			}
		}

		/**
		 * Clears all of the circles
		 */
		public void clearPointers() 
		{
			mCircles = new ArrayList<Circle>();
		}

	}
	
	public int velocityConstant = 20;

	/**
	 * @param context
	 */
	public CanvasView(Context context)
	{
		super(context);
		this.setOnTouchListener(this);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		mThread = new GraphThread(holder, context, new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{
				// TODO: handle any UI stuff.
			}
		});
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CanvasView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setOnTouchListener(this);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		mThread = new GraphThread(holder, context, new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{
				// TODO: handle any UI stuff.
			}
		});
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CanvasView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		mThread = new GraphThread(holder, context, new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{
				// TODO: handle any UI stuff.
			}
		});
	}

	/**
	 * 
	 * @return thread
	 */
	public GraphThread getThread()
	{
		return mThread;
	}
	
	/**
	 * 
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		mThread.setSurfaceSize(width, height);
	}
	
	/**
	 * 
	 */
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread.setRunning(true);
		mThread.start();
	}
	
	/**
	 * 
	 */
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		boolean retry = true;
		mThread.setRunning(false);
		while (retry)
		{
			try
			{
				mThread.join();
				retry = false;
			}
			catch (InterruptedException e)
			{

			}
		}
	}
	
	VelocityTracker velocityTracker = VelocityTracker.obtain();

	private boolean mPointersDown = false;
	
	public String getActionName(int action)
	{
		switch (action)
		{
			case (MotionEvent.ACTION_DOWN): 
			{
				return "ACTION_DOWN";
			}
			case (MotionEvent.ACTION_MOVE): 
			{
				return "ACTION_MOVE";
			}
			case (MotionEvent.ACTION_POINTER_UP): 
			{
				return "ACTION_POINTER_UP";
			}
			case (MotionEvent.ACTION_UP): 
			{
				return "ACTION_UP";
			}
			case (MotionEvent.ACTION_POINTER_DOWN):
			{
				return "ACTION_POINTER_DOWN";
			}
			default:
				return action + "";
		}
	}

	public boolean onTouch(View v, MotionEvent event)
	{
		//final int historySize = event.getHistorySize();
		//final int pointerCount = event.getPointerCount();
		//Log.d(LOG_TAG, "HISTORY SIZE:"+historySize+":");
		
		/*
		for (int h = 0; h < event.getHistorySize(); h++)
		{
			//System.out.printf(" History - At time %d:", event.getHistoricalEventTime(h));
			//Log.d(LOG_TAG, " History - At time "+event.getHistoricalEventTime(h)+":");
			for (int p = 0; p < event.getPointerCount(); p++) 
			{
				//System.out.printf("  H-pointer %d: (%f,%f)", event.getPointerId(p), event.getHistoricalX(p, h), event.getHistoricalY(p, h));
				//Log.d(LOG_TAG, "  H-pointer "+event.getPointerId(p)+": ("+event.getHistoricalX(p, h)+","+event.getHistoricalY(p, h)+")");
			}
		}
		*/
		if (event.getAction() != 2)
		{
			Log.d(LOG_TAG, getActionName(event.getAction() & MotionEvent.ACTION_MASK) + "");
		}
		
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) 
		{
			case (MotionEvent.ACTION_DOWN): 
			{
				velocityTracker.clear();
				velocityTracker.addMovement(event);
				
				mThread.clearPointers();
				
				break;
			}
			case (MotionEvent.ACTION_MOVE): 
			{
				velocityTracker.addMovement(event);
				mPointersDown  = true;
				break;
			}
			case (MotionEvent.ACTION_POINTER_UP): 
			{
				mPointersDown = false;
				
				int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				
				velocityTracker.addMovement(event);
				velocityTracker.computeCurrentVelocity(velocityConstant);
				
				//Log.d(LOG_TAG, pointerIndex + "");
				//Log.d(LOG_TAG, event.getPointerId(pointerIndex) + "");
				
				int id = event.getPointerId(pointerIndex);
				mThread.setCircleVelocity(id, velocityTracker.getXVelocity(id), velocityTracker.getYVelocity(id));
				
				break;
			}
			case (MotionEvent.ACTION_UP): 
			{
				//velocityTracker.addMovement(event);
				//velocityTracker.computeCurrentVelocity(10);
				//mThread.setCircleVelocity(velocityTracker.getXVelocity(), velocityTracker.getYVelocity());
				
				
				int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				velocityTracker.addMovement(event);
				velocityTracker.computeCurrentVelocity(velocityConstant);
				
				//Log.d(LOG_TAG, pointerIndex + "");
				//Log.d(LOG_TAG, event.getPointerId(pointerIndex) + "");
				
				int id = event.getPointerId(pointerIndex);
				mThread.setCircleVelocity(id, velocityTracker.getXVelocity(id), velocityTracker.getYVelocity(id));
				
				break;
			}
		}
		
		//System.out.printf("At time %d:", event.getEventTime());
		//Log.d(LOG_TAG, "At time "+event.getEventTime()+":");
		for (int p = 0; p < event.getPointerCount(); p++) 
		{
			//System.out.printf("  pointer %d: (%f,%f)", event.getPointerId(p), event.getX(p), event.getY(p));
			//Log.d(LOG_TAG, "  pointer "+event.getPointerId(p)+": ("+event.getX(p)+","+event.getY(p)+")");
			int pointerId = event.getPointerId(p);
			
			mThread.setCirclePosition(pointerId, event.getX(p), event.getY(p));
		}
		
		return true;
	}

}