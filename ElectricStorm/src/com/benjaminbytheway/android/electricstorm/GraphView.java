/**
 * 
 */
package com.benjaminbytheway.android.electricstorm;

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
public class GraphView extends SurfaceView implements Callback, OnTouchListener
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
		/** X position for the circle */
		private Float mCircleX = 50f;

		/** Y position for the circle */
		private Float mCircleY = 50f;

		/** flag for checking if the circle has changed */
		private boolean mCircleChanged = false;

		private Float mCircle1X = 50f;

		private Float mCircle1Y = 50f;
		
		private Float mCircle2X = 50f;

		private Float mCircle2Y = 50f;
		
		private Float mCircle3X = 50f;

		private Float mCircle3Y = 50f;
		
		//other stuff
		
		private Float mCircleVelX = 0f;
		
		private Float mCircleVelY = 0f;
		
		private Float mCircle1VelX = 0f;

		private Float mCircle1VelY = 0f;
		
		private Float mCircle2VelX = 0f;

		private Float mCircle2VelY = 0f;
		
		private Float mCircle3VelX = 0f;

		private Float mCircle3VelY = 0f;
		
		//acceleration
		
		private Float mCircleAccX = 0f;
		
		private Float mCircleAccY = 0f;
		
		private Float mCircle1AccX = 0f;

		private Float mCircle1AccY = 0f;
		
		private Float mCircle2AccX = 0f;

		private Float mCircle2AccY = 0f;
		
		private Float mCircle3AccX = 0f;

		private Float mCircle3AccY = 0f;

		//canvas dimentions
		
		private int canvasHeight;

		private int canvasWidth;

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
					if (mCircleChanged || circlesMoving())
					{
						synchronized (mSurfaceHolder)
						{
							c = mSurfaceHolder.lockCanvas(null);
							
							canvasHeight = c.getHeight();
							canvasWidth = c.getWidth();
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
			if ((mCircleVelX != 0 ||	mCircleVelY != 0) || 
				(mCircle1VelX != 0 ||	mCircle1VelY != 0) || 
				(mCircle2VelX != 0 ||	mCircle2VelY != 0) || 
				(mCircle3VelX != 0 ||	mCircle3VelY != 0))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		private Float decrementVelocity(Float velocity, Float acceleration)
		{
			if ((velocity > 1) || (velocity < -1))
			{
				velocity -= acceleration;
			}
			else
			{
				velocity = 0f;
			}
			
			return velocity;
		}
		
		private void determineXBounds(Float position, Float velocity, Float acceleration)
		{
			if ((position < 0 || position > canvasWidth) && velocity != 0)
			{
				velocity = -velocity;
				acceleration = -acceleration;
			}
		}
		
		private void determineYBounds(Float position, Float velocity, Float acceleration)
		{
			if ((position < 0 || position > canvasHeight) && velocity != 0)
			{
				velocity = -velocity;
				acceleration = -acceleration;
			}
		}
		
		private void calculate()
		{
			mCircleVelX = decrementVelocity(mCircleVelX, mCircleAccX);
			mCircleVelY = decrementVelocity(mCircleVelY, mCircleAccY); 
			mCircle1VelX = decrementVelocity(mCircle1VelX, mCircle1AccX);
			mCircle1VelY = decrementVelocity(mCircle1VelY, mCircle1AccY);
			mCircle2VelX = decrementVelocity(mCircle2VelX, mCircle2AccX);
			mCircle2VelY = decrementVelocity(mCircle2VelY, mCircle2AccY);
			mCircle3VelX = decrementVelocity(mCircle3VelX, mCircle3AccX);
			mCircle3VelY = decrementVelocity(mCircle3VelY, mCircle3AccY);
			
			mCircleX += mCircleVelX;
			mCircleY += mCircleVelY;
			mCircle1X += mCircle1VelX;
			mCircle1Y += mCircle1VelY;
			mCircle2X += mCircle2VelX;
			mCircle2Y += mCircle2VelY;
			mCircle3X += mCircle3VelX;
			mCircle3Y += mCircle3VelY;
			
			if ((mCircleX < 0 || mCircleX > canvasWidth) && mCircleVelX != 0)
			{
				if (mCircleX < 0)
				{
					mCircleX = 0f;
				}
				else
				{
					mCircleX = (float) canvasWidth;
				}
				mCircleVelX = -mCircleVelX;
				mCircleAccX = -mCircleAccX;
			}
			//determineXBounds(mCircleX, mCircleVelX, mCircleAccX);
			
			if ((mCircleY < 0 || mCircleY > canvasHeight) && mCircleVelY != 0)
			{
				if (mCircleY < 0)
				{
					mCircleY = 0f;
				}
				else
				{
					mCircleY = (float) canvasHeight;
				}
				mCircleVelY = -mCircleVelY;
				mCircleAccY = -mCircleAccY;
			}
			//determineYBounds(mCircleY, mCircleVelY, mCircleAccY);
			
			if ((mCircle1X < 0 || mCircle1X > canvasWidth) && mCircle1VelX != 0)
			{
				if (mCircle1X < 0)
				{
					mCircle1X = 0f;
				}
				else
				{
					mCircle1X = (float) canvasWidth;
				}
				mCircle1VelX = -mCircle1VelX;
				mCircle1AccX = -mCircle1AccX;
			}
			//determineXBounds(mCircle1X, mCircle1VelX, mCircle1AccX);
			
			if ((mCircle1Y < 0 || mCircle1Y > canvasHeight) && mCircle1VelY != 0)
			{
				if (mCircle1Y < 0)
				{
					mCircle1Y = 0f;
				}
				else
				{
					mCircle1Y = (float) canvasHeight;
				}
				mCircle1VelY = -mCircle1VelY;
				mCircle1AccY = -mCircle1AccY;
			}
			//determineYBounds(mCircle1Y, mCircle1VelY, mCircle1AccY);
			
			if ((mCircle2X < 0 || mCircle2X > canvasWidth) && mCircle2VelX != 0)
			{
				if (mCircle2X < 0)
				{
					mCircle2X = 0f;
				}
				else
				{
					mCircle2X = (float) canvasWidth;
				}
				mCircle2VelX = -mCircle2VelX;
				mCircle2AccX = -mCircle2AccX;
			}
			//determineXBounds(mCircle2X, mCircle2VelX, mCircle2AccX);
			
			if ((mCircle2Y < 0 || mCircle2Y > canvasHeight) && mCircle2VelY != 0)
			{
				if (mCircle2Y < 0)
				{
					mCircle2Y = 0f;
				}
				else
				{
					mCircle2Y = (float) canvasHeight;
				}
				mCircle2VelY = -mCircle2VelY;
				mCircle2AccY = -mCircle2AccY;
			}
			//determineYBounds(mCircle2Y, mCircle2VelY, mCircle2AccY);
			
			if ((mCircle3X < 0 || mCircle3X > canvasWidth) && mCircle3VelX != 0)
			{
				if (mCircle3X < 0)
				{
					mCircle3X = 0f;
				}
				else
				{
					mCircle3X = (float) canvasWidth;
				}
				mCircle3VelX = -mCircle3VelX;
				mCircle3AccX = -mCircle3AccX;
			}
			//determineXBounds(mCircle3X, mCircle3VelX, mCircle3AccX);
			
			if ((mCircle3Y < 0 || mCircle3Y > canvasHeight) && mCircle3VelY != 0)
			{
				if (mCircle3Y < 0)
				{
					mCircle3Y = 0f;
				}
				else
				{
					mCircle3Y = (float) canvasHeight;
				}
				mCircle3VelY = -mCircle3VelY;
				mCircle3AccY = -mCircle3AccY;
			}
			//determineYBounds(mCircle3Y, mCircle3VelY, mCircle3AccY);
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
			
			if (mPointersDown)
			{
				c.drawLine(mCircleX, mCircleY, mCircle1X, mCircle1Y, paint);
				c.drawLine(mCircleX, mCircleY, mCircle2X, mCircle2Y, paint);
				c.drawLine(mCircleX, mCircleY, mCircle3X, mCircle3Y, paint);
				c.drawLine(mCircle1X, mCircle1Y, mCircle2X, mCircle2Y, paint);
				c.drawLine(mCircle1X, mCircle1Y, mCircle3X, mCircle3Y, paint);
				c.drawLine(mCircle2X, mCircle2Y, mCircle3X, mCircle3Y, paint);
			}
			
			//All the circles
			Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			mLinePaint.setShader(new RadialGradient(mCircleX, mCircleY, 40, Color.argb(255, 255, 0, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP));
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 255, 0, 255);
			c.drawCircle(mCircleX, mCircleY, 40, mLinePaint);
			
			Paint mLinePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			mLinePaint1.setShader(new RadialGradient(mCircle1X, mCircle1Y, 40, Color.argb(255, 0, 255, 0), Color.TRANSPARENT, Shader.TileMode.CLAMP));
			mLinePaint1.setAntiAlias(true);
			mLinePaint1.setARGB(255, 0, 255, 0);
			c.drawCircle(mCircle1X, mCircle1Y, 40, mLinePaint1);
			
			Paint mLinePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			mLinePaint2.setShader(new RadialGradient(mCircle2X, mCircle2Y, 40, Color.argb(255, 255, 0, 0), Color.TRANSPARENT, Shader.TileMode.CLAMP));
			mLinePaint2.setAntiAlias(true);
			mLinePaint2.setARGB(255, 255, 0, 0);
			c.drawCircle(mCircle2X, mCircle2Y, 40, mLinePaint2);
			
			Paint mLinePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
			mLinePaint3.setShader(new RadialGradient(mCircle3X, mCircle3Y, 40, Color.argb(255, 0, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP));
			mLinePaint3.setAntiAlias(true);
			mLinePaint3.setARGB(255, 0, 255, 255);
			c.drawCircle(mCircle3X, mCircle3Y, 40, mLinePaint3);
			
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
		
		public void setCircleVelocity(Float x, Float y)
		{
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircleVelX = x;
				mCircleVelY = y;
				mCircleAccX = x/accelerationCoeffiecient;
				mCircleAccY = y/accelerationCoeffiecient;
			}
		}
		
		public void setCircle1Velocity(Float x, Float y)
		{
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircle1VelX = x;
				mCircle1VelY = y;
				mCircle1AccX = x/accelerationCoeffiecient;
				mCircle1AccY = y/accelerationCoeffiecient;
			}
		}
		
		public void setCircle2Velocity(Float x, Float y)
		{
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircle2VelX = x;
				mCircle2VelY = y;
				mCircle2AccX = x/accelerationCoeffiecient;
				mCircle2AccY = y/accelerationCoeffiecient;
			}
		}
		
		public void setCircle3Velocity(Float x, Float y)
		{
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircle3VelX = x;
				mCircle3VelY = y;
				mCircle3AccX = x/accelerationCoeffiecient;
				mCircle3AccY = y/accelerationCoeffiecient;
			}
		}
		
		public void setCirclePosition(Float x, Float y)
		{
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircleX = x;
				mCircleY = y;
			}
		}

		public void setCircle1Position(Float x, Float y)
		{
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircle1X = x;
				mCircle1Y = y;
			}
		}
		
		public void setCircle2Position(Float x, Float y)
		{
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircle2X = x;
				mCircle2Y = y;
			}
		}
		
		public void setCircle3Position(Float x, Float y)
		{
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder)
			{
				mCircleChanged = true;
				
				mCircle3X = x;
				mCircle3Y = y;
			}
		}

	}
	
	public int velocityConstant = 20;

	/**
	 * @param context
	 */
	public GraphView(Context context)
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
	public GraphView(Context context, AttributeSet attrs)
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
	public GraphView(Context context, AttributeSet attrs, int defStyle)
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
			default:
				return action + "";
		}
	}

	public boolean onTouch(View v, MotionEvent event)
	{
		//final int historySize = event.getHistorySize();
		//final int pointerCount = event.getPointerCount();
		//Log.d(LOG_TAG, "HISTORY SIZE:"+historySize+":");
		
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
				
				Log.d(LOG_TAG, pointerIndex + "");
				Log.d(LOG_TAG, event.getPointerId(pointerIndex) + "");
				
				switch (event.getPointerId(pointerIndex))
				{
					case 0:
						mThread.setCircleVelocity(velocityTracker.getXVelocity(0), velocityTracker.getYVelocity(0));
						break;
					case 1:
						mThread.setCircle1Velocity(velocityTracker.getXVelocity(1), velocityTracker.getYVelocity(1));
						break;
					case 2:
						mThread.setCircle2Velocity(velocityTracker.getXVelocity(2), velocityTracker.getYVelocity(2));
						break;
					case 3:
						mThread.setCircle3Velocity(velocityTracker.getXVelocity(3), velocityTracker.getYVelocity(3));
						break;
					default:
						break;
				}
				
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
				
				Log.d(LOG_TAG, pointerIndex + "");
				Log.d(LOG_TAG, event.getPointerId(pointerIndex) + "");
				
				switch (event.getPointerId(pointerIndex))
				{
					case 0:
						mThread.setCircleVelocity(velocityTracker.getXVelocity(0), velocityTracker.getYVelocity(0));
						break;
					case 1:
						mThread.setCircle1Velocity(velocityTracker.getXVelocity(1), velocityTracker.getYVelocity(1));
						break;
					case 2:
						mThread.setCircle2Velocity(velocityTracker.getXVelocity(2), velocityTracker.getYVelocity(2));
						break;
					case 3:
						mThread.setCircle3Velocity(velocityTracker.getXVelocity(3), velocityTracker.getYVelocity(3));
						break;
					default:
						break;
				}
				
				
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
			
			switch(pointerId)
			{
				case 0:
					mThread.setCirclePosition(event.getX(p), event.getY(p));
					break;
				case 1:
					mThread.setCircle1Position(event.getX(p), event.getY(p));
					break;
				case 2:
					mThread.setCircle2Position(event.getX(p), event.getY(p));
					break;
				case 3:
					mThread.setCircle3Position(event.getX(p), event.getY(p));
					break;
				case 4:
					//mThread.setCircle1Position(event.getX(p), event.getY(p));
					break;
				case 5:
					//mThread.setCircle1Position(event.getX(p), event.getY(p));
					break;
				default:
					break;
			}
		}
		
		return true;
	}

}