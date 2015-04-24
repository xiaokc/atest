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
	private Bitmap btn;// ���Ż���ͣ��ť��ͼƬ
	private long movieStart;// ���ſ�ʼ��ʱ��
	private int imageWidth;// ͼƬ���
	private int imageHeight;// ͼƬ�߶�
	private boolean isPlaying;// �Ƿ��ڲ���
	private boolean isAutoPlay;// �Ƿ������Զ�����
	
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
			// ����Դid������0ʱ����ȥ��ȡ����Դ����
			InputStream is = getResources().openRawResource(resourceId);
			// ʹ��Movie��������н���
			movie = Movie.decodeStream(is);
			if (movie != null) {
				// �������ֵ������null����˵������һ��GIFͼƬ�������ȡ�Ƿ��Զ����ŵ�����
//				isAutoPlay = a.getBoolean(R.styleable.PowerImageView_auto_play,
//						false);
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				imageWidth = bitmap.getWidth();
				imageHeight = bitmap.getHeight();
				bitmap.recycle();
//				if (!isAutoPlay) {
//					// ���������Զ����ŵ�ʱ�򣬵õ���ʼ���Ű�ť��ͼƬ����ע�����¼�
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
	        // mMovie����null��˵��������ͨ��ͼƬ����ֱ�ӵ��ø����onDraw()����  
	        super.onDraw(canvas);  
	    } else {  
	        // mMovie������null��˵������GIFͼƬ  
	        if (isAutoPlay) {  
	            // ��������Զ����ţ��͵���playMovie()��������GIF����  
	            playMovie(canvas);  
	            invalidate();  
	        } else {  
	            // �������Զ�����ʱ���жϵ�ǰͼƬ�Ƿ����ڲ���  
	            if (isPlaying) {  
	                // ���ڲ��žͼ�������playMovie()������һֱ���������Ž���Ϊֹ  
	                if (playMovie(canvas)) {  
	                    isPlaying = false;  
	                }  
	                invalidate();  
	            } else {  
	                // ��û��ʼ���ž�ֻ����GIFͼƬ�ĵ�һ֡��������һ����ʼ��ť  
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
	        // �����GIFͼƬ����д�趨PowerImageView�Ĵ�С  
	        setMeasuredDimension(imageWidth, imageHeight);  
	    }  
	}  
	
	/** 
	 * ��ʼ����GIF������������ɷ���true��δ��ɷ���false�� 
	 *  
	 * @param canvas 
	 * @return ������ɷ���true��δ��ɷ���false�� 
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
	* ͨ��Java���䣬��ȡ��srcָ��ͼƬ��Դ����Ӧ��id�� 
	*  
	* @param a 
	* @param context 
	* @param attrs 
	* @return ���ز����ļ���ָ��ͼƬ��Դ����Ӧ��id��û��ָ���κ�ͼƬ��Դ�ͷ���0�� 
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
