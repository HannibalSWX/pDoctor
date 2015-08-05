package com.owen.pDoctor.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.owen.pDoctor.activity.ImageShowerActivity;

public class ZoomImageView extends ImageView {
	public static final int STATUS_INIT = 1;// 常量初始化
	public static final int STATUS_ZOOM_OUT = 2;// 图片放大状态常量
	public static final int STATUS_ZOOM_IN = 3;// 图片缩小状态常量
	public static final int STATUS_MOVE = 4;// 图片拖动状态常量
	public static final int STATUS_DELETE = 5;// 删除画画常量
	public static final int STATUS_DRAW = 6;// 开始画图常量常量
	private Matrix matrix = new Matrix();// 对图片进行移动和缩放变换的矩阵
	private int currentStatus;// 记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
	private int currentStatusPaint = STATUS_DRAW;// 记录当前操作的状态，可选值为STATUS_DELETE
	private int width;// ZoomImageView控件的宽度
	private int height;// ZoomImageView控件的高度
	private float centerPointX;// 记录两指同时放在屏幕上时，中心点的横坐标值
	private float centerPointY;// 记录两指同时放在屏幕上时，中心点的纵坐标值
	private float currentBitmapWidth;// 记录当前图片的宽度，图片被缩放时，这个值会一起变动
	private float currentBitmapHeight;// 记录当前图片的高度，图片被缩放时，这个值会一起变动
	private float lastXMove = -1;// 记录上次手指移动时的横坐标
	private float lastYMove = -1;// 记录上次手指移动时的纵坐标
	private float movedDistanceX;// 记录手指在横坐标方向上的移动距离
	private float movedDistanceY;// 记录手指在纵坐标方向上的移动距离
	private float totalTranslateX;// 记录图片在矩阵上的横向偏移值
	private float totalTranslateY;// 记录图片在矩阵上的纵向偏移值
	private float totalRatio;// 记录图片在矩阵上的总缩放比例
	private float scaledRatio;// 记录手指移动的距离所造成的缩放比例
	private float initRatio;// 记录图片初始化时的缩放比例
	private double lastFingerDis;// 记录上次两指之间的距离
	/*
	 * 将待展示的图片设置进来
	 * 
	 * @param bitmap 待展示的Bitmap对象
	 */
	float preX;
	float preY;
	// 定义一个内存中的图片,该图片将作为缓冲区
	Bitmap cacheBitmap = null;

	float screenRateSize = 1;

	String screenWidth = "0";
	String screenHeight = "0";
	private float size1 = 1, size2 = 1;
	/*
	 * private float interX = 0; private float interY = 0; private float
	 * interWidth = 0; private float interHeight = 0;
	 */

	public String getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(String screenWidth) {
		this.screenWidth = screenWidth;
	}

	public String getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(String screenHeight) {
		this.screenHeight = screenHeight;
	}

	private boolean drawFlag = false;// 判断是否可以拉伸

	/**
	 * ZoomImageView构造函数，将当前操作状态设为STATUS_INIT。
	 * @param context
	 * @param attrs
	 */
	ImageShowerActivity nodeOperateActivity;
	public void setNodeOperateActivity(ImageShowerActivity nodeOperateActivity) {
		this.nodeOperateActivity = nodeOperateActivity;
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentStatus = STATUS_INIT;
	}

	public void setBitmap(Bitmap bitmap) {
		invalidate();
		cacheBitmap = bitmap.copy(Config.ARGB_8888, true);
	}
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			// 分别获取到ZoomImageView的宽度和高度
			width = getWidth();
			height = getHeight();
		}
	}

	float startX = 0.0f;
	float startY = 0.0f;
	float endX = 0.0f;
	float endY = 0.0f;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (cacheBitmap == null) {
			return false;
		}
		if (initRatio == totalRatio) {
			getParent().requestDisallowInterceptTouchEvent(false);
		} else {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN :
				startX = event.getX();
				startY = event.getY();
			case MotionEvent.ACTION_POINTER_DOWN :
				if (event.getPointerCount() == 2) {
					// 当有两个手指按在屏幕上时，计算两指之间的距离
					lastFingerDis = distanceBetweenFingers(event);
				}
				break;
			case MotionEvent.ACTION_CANCEL :
			case MotionEvent.ACTION_MOVE :
				if (event.getPointerCount() == 1) {
					// 只有单指按在屏幕上移动时，为拖动状态
					float xMove = event.getX();
					float yMove = event.getY();
					if (lastXMove == -1 && lastYMove == -1) {
						lastXMove = xMove;
						lastYMove = yMove;
					}
					currentStatus = STATUS_MOVE;
					movedDistanceX = xMove - lastXMove;
					movedDistanceY = yMove - lastYMove;
					// 进行边界检查，不允许将图片拖出边界
					if (totalTranslateX + movedDistanceX > 0) {
						movedDistanceX = 0;
					} else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth) {
						movedDistanceX = 0;
					}
					if (totalTranslateY + movedDistanceY > 0) {
						movedDistanceY = 0;
					} else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight) {
						movedDistanceY = 0;
					}
					// 调用onDraw()方法绘制图片
					invalidate();
					lastXMove = xMove;
					lastYMove = yMove;
				} else if (event.getPointerCount() == 2) {
					// 有两个手指按在屏幕上移动时，为缩放状态
					centerPointBetweenFingers(event);
					double fingerDis = distanceBetweenFingers(event);
					if (fingerDis > lastFingerDis) {
						currentStatus = STATUS_ZOOM_OUT;
					} else {
						currentStatus = STATUS_ZOOM_IN;
					}
					// 进行缩放倍数检查，最大只允许将图片放大4倍，最小可以缩小到初始化比例
					if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 3 * initRatio)
							|| (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
						scaledRatio = (float) (fingerDis / lastFingerDis);
						totalRatio = totalRatio * scaledRatio;
						if (totalRatio > 3 * initRatio) {
							totalRatio = 3 * initRatio;
						} else if (totalRatio < initRatio) {
							totalRatio = initRatio;
						}
						// 调用onDraw()方法绘制图片
						invalidate();
						lastFingerDis = fingerDis;
					}
				}
				break;
			case MotionEvent.ACTION_POINTER_UP :
				if (event.getPointerCount() == 2) {
					// 手指离开屏幕时将临时值还原
					lastXMove = -1;
					lastYMove = -1;
				}
				break;
			case MotionEvent.ACTION_UP :
				// 手指离开屏幕时将临时值还原
				lastXMove = -1;
				lastYMove = -1;
				// 图片左上角的坐标
				float[] f = new float[9];
				matrix.getValues(f);
				float leftTopX = f[0] * 0 + f[1] * 0 + f[2];
				float leftTopY = f[3] * 0 + f[4] * 0 + f[5];
				endX = event.getX();
				endY = event.getY();
				if ((startX - endX) == 0 && (startY - endY) == 0) {
					// 原图与现图比例
					if (!screenWidth.equals("") && !screenHeight.equals("")) {
						size2 = Float.parseFloat(screenWidth)
								/ currentBitmapWidth;
					}
				}
				break;
			default :
				break;
		}
		invalidate();
		return true;
	}

	/* 根据currentStatus的值来决定对图片进行什么样的绘制操作。 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (cacheBitmap == null) {
			return;
		}
		if (drawFlag == false) {
			switch (currentStatus) {
				case STATUS_ZOOM_OUT :
				case STATUS_ZOOM_IN :
					zoom(canvas);
					break;
				case STATUS_MOVE :
					move(canvas);
					break;
				case STATUS_INIT :
					initBitmap(canvas);
				default :
					if (cacheBitmap != null) {
						canvas.drawBitmap(cacheBitmap, matrix, null);
					}
					break;
			}
		} else if (drawFlag == true) {
			switch (currentStatusPaint) {
				case STATUS_DRAW :
					Paint bmpPaint = new Paint();
					// 将cacheBitmap绘制到该View组件上
					canvas.drawBitmap(cacheBitmap, matrix, bmpPaint);
					// 沿着path绘制
					break;
				case STATUS_DELETE :
					if (cacheBitmap != null) {
						canvas.drawBitmap(cacheBitmap, matrix, null);
					}
					break;
			}
		}
	}

	/**
	 * 对图片进行缩放处理。
	 * @param canvas
	 */
	private void zoom(Canvas canvas) {
		matrix.reset();
		// 将图片按总缩放比例进行缩放
		matrix.postScale(totalRatio, totalRatio);
		float scaledWidth = cacheBitmap.getWidth() * totalRatio;
		float scaledHeight = cacheBitmap.getHeight() * totalRatio;
		float translateX = 0f;
		float translateY = 0f;
		// 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
		if (currentBitmapWidth < width) {
			translateX = (width - scaledWidth) / 2f;
		} else {
			translateX = totalTranslateX * scaledRatio + centerPointX
					* (1 - scaledRatio);
			// 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
			if (translateX > 0) {
				translateX = 0;
			} else if (width - translateX > scaledWidth) {
				translateX = width - scaledWidth;
			}
		}
		// 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
		if (currentBitmapHeight < height) {
			translateY = (height - scaledHeight) / 2f;
		} else {
			translateY = totalTranslateY * scaledRatio + centerPointY
					* (1 - scaledRatio);
			// 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
			if (translateY > 0) {
				translateY = 0;
			} else if (height - translateY > scaledHeight) {
				translateY = height - scaledHeight;
			}
		}
		// 缩放后对图片进行偏移，以保证缩放后中心点位置不变
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		currentBitmapWidth = scaledWidth;
		currentBitmapHeight = scaledHeight;
		canvas.drawBitmap(cacheBitmap, matrix, null);
	}

	/**
	 * 对图片进行平移处理
	 * @param canvas
	 */
	private void move(Canvas canvas) {
		matrix.reset();
		// 根据手指移动的距离计算出总偏移值
		float translateX = totalTranslateX + movedDistanceX;
		float translateY = totalTranslateY + movedDistanceY;
		// 先按照已有的缩放比例对图片进行缩放
		matrix.postScale(totalRatio, totalRatio);
		// 再根据移动距离进行偏移
		matrix.postTranslate(translateX, translateY);
		totalTranslateX = translateX;
		totalTranslateY = translateY;
		canvas.drawBitmap(cacheBitmap, matrix, null);
	}

	/**
	 * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
	 * @param canvas
	 */
	private void initBitmap(Canvas canvas) {
		if (cacheBitmap != null) {
			matrix.reset();
			int bitmapWidth = cacheBitmap.getWidth();
			int bitmapHeight = cacheBitmap.getHeight();
			if (bitmapWidth > width || bitmapHeight > height) {
				if (bitmapWidth - width > bitmapHeight - height) {
					// 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
					float ratio = width / (bitmapWidth * 1.0f);
					matrix.postScale(ratio, ratio);
					float translateY = (height - (bitmapHeight * ratio)) / 2f;
					// 在纵坐标方向上进行偏移，以保证图片居中显示
					matrix.postTranslate(0, translateY);
					totalTranslateY = translateY;
					totalRatio = initRatio = ratio;
				} else {
					// 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
					float ratio = height / (bitmapHeight * 1.0f);
					matrix.postScale(ratio, ratio);
					float translateX = (width - (bitmapWidth * ratio)) / 2f;
					// 在横坐标方向上进行偏移，以保证图片居中显示
					matrix.postTranslate(translateX, 0);
					totalTranslateX = translateX;
					totalRatio = initRatio = ratio;
				}
				currentBitmapWidth = bitmapWidth * initRatio;
				currentBitmapHeight = bitmapHeight * initRatio;
			} else {
				// 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
				float translateX = (width - cacheBitmap.getWidth()) / 2f;
				float translateY = (height - cacheBitmap.getHeight()) / 2f;
				float ratio = width / (bitmapWidth * 1.0f);
				matrix.postScale(ratio, ratio);
				matrix.postTranslate(0, translateY);
				totalTranslateX = translateX;
				totalTranslateY = translateY;
				totalRatio = initRatio = 1f;
				currentBitmapWidth = bitmapWidth;
				currentBitmapHeight = bitmapHeight;
			}
			canvas.drawBitmap(cacheBitmap, matrix, null);
		}
	}

	/**
	 * 计算两个手指之间的距离。
	 * @param event
	 * @return 两个手指之间的距离
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return FloatMath.sqrt(disX * disX + disY * disY);
	}

	/**
	 * 计算两个手指之间中心点的坐标。
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		centerPointX = (xPoint0 + xPoint1) / 2;
		centerPointY = (yPoint0 + yPoint1) / 2;
	}

	public int dip2px(float dipValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}