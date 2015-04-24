package com.example.atest;

import java.io.InputStream;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

public class AnimImageView extends ImageView implements
	android.view.View.OnClickListener {

	private Movie movie;
	private Bitmap btn;// 播放或暂停按钮的图片
	private long movieStart;// 播放开始的时间
	private int imageWidth;// 图片宽度
	private int imageHeight;// 图片高度
	private boolean isPlaying;// 是否在播放
	private boolean isAutoPlay;// 是否允许自动播放
	
	public AnimImageView(Context context) {
		super(context);
	}

	public AnimImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	
	}
	
	public AnimImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PowerImageView);
		int resourceId = getResourceId(a, context, attrs);
		if (resourceId != 0) {
			// 当资源id不等于0时，就去获取该资源的流
			InputStream is = getResources().openRawResource(resourceId);
			// 使用Movie类对流进行解码
			movie = Movie.decodeStream(is);
			if (movie != null) {
				// 如果返回值不等于null，就说明这是一个GIF图片，下面获取是否自动播放的属性
//				isAutoPlay = a.getBoolean(R.styleable.PowerImageView_auto_play,
//						false);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				imageWidth = bitmap.getWidth();
				imageHeight = bitmap.getHeight();
				bitmap.recycle();
//				if (!isAutoPlay) {
//					// 当不允许自动播放的时候，得到开始播放按钮的图片，并注册点击事件
//					btn = BitmapFactory.decodeResource(getResources(),
//							R.drawable.play);
//					setOnClickListener(this);
//				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
	// TODO Auto-generated method stub
		if (v.getId() == getId()) {
			isPlaying = true;
			invalidate();
			
		}
	
	}
	@Override  
	protected void onDraw(Canvas canvas) {  
	    if (movie == null) {  
	        // mMovie等于null，说明是张普通的图片，则直接调用父类的onDraw()方法  
	        super.onDraw(canvas);  
	    } else {  
	        // mMovie不等于null，说明是张GIF图片  
	        if (isAutoPlay) {  
	            // 如果允许自动播放，就调用playMovie()方法播放GIF动画  
	            playMovie(canvas);  
	            invalidate();  
	        } else {  
	            // 不允许自动播放时，判断当前图片是否正在播放  
	            if (isPlaying) {  
	                // 正在播放就继续调用playMovie()方法，一直到动画播放结束为止  
	                if (playMovie(canvas)) {  
	                    isPlaying = false;  
	                }  
	                invalidate();  
	            } else {  
	                // 还没开始播放就只绘制GIF图片的第一帧，并绘制一个开始按钮  
	                movie.setTime(0);  
	                movie.draw(canvas, 0, 0);  
	                int offsetW = (imageWidth - btn.getWidth()) / 2;  
	                int offsetH = (imageHeight - btn.getHeight()) / 2;  
	                canvas.drawBitmap(btn, offsetW, offsetH, null);  
	            }  
	        }  
	    }  
	}  
	
	@Override  
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
	    if (movie != null) {  
	        // 如果是GIF图片则重写设定PowerImageView的大小  
	        setMeasuredDimension(imageWidth, imageHeight);  
	    }  
	}  
	
	/** 
	 * 开始播放GIF动画，播放完成返回true，未完成返回false。 
	 *  
	 * @param canvas 
	 * @return 播放完成返回true，未完成返回false。 
	 */  
	private boolean playMovie(Canvas canvas) {  
	    long now = SystemClock.uptimeMillis();  
	    if (movieStart == 0) {  
	        movieStart = now;  
	    }  
	    int duration = movie.duration();  
	    if (duration == 0) {  
	        duration = 1000;  
	    }  
	    int relTime = (int) ((now - movieStart) % duration);  
	    movie.setTime(relTime);  
	    movie.draw(canvas, 0, 0);  
	    if ((now - movieStart) >= duration) {  
	        movieStart = 0;  
	        return true;  
	    }  
	    return false;  
	}  
	/** 
	* 通过Java反射，获取到src指定图片资源所对应的id。 
	*  
	* @param a 
	* @param context 
	* @param attrs 
	* @return 返回布局文件中指定图片资源所对应的id，没有指定任何图片资源就返回0。 
	*/  
	private int getResourceId(TypedArray a, Context context, AttributeSet attrs) {  
		try {  
			    Field field = TypedArray.class.getDeclaredField("mValue");  
			    field.setAccessible(true);  
			    TypedValue typedValueObject = (TypedValue) field.get(a);  
			    return typedValueObject.resourceId;  
			} 
		catch (Exception e) {  
		    e.printStackTrace();  
		} finally {  
		    if (a != null) {  
		        a.recycle();  
		    }  
		}  
		return 0;  
		
		}
	} 
