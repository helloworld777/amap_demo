package com.amap.map3d.demo.basic;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ViewAnimator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.map3d.demo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


/**
 * AMapV2地图中介绍如何显示一个基本地图
 */
public class BasicMapActivity extends Activity implements OnClickListener{
	private MapView mapView;
	private AMap aMap;
	private Button basicmap;
	private Button rsmap;
	private Button nightmap;
	private Button navimap;

	private CheckBox mStyleCheckbox;

	private View rlContent;
	private View btnShowMap;
	private View content_overlay;
	private Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basicmap_activity);
	    /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
		//Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		//  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		rlContent=findViewById(R.id.rlContent);

		content_overlay=findViewById(R.id.content_overlay);
		init();

		btnShowMap=findViewById(R.id.btnShowMap);
		btnShowMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMap();
			}
		});

		findViewById(R.id.hidMap).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hidMap();
			}
		});

		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		 width = wm.getDefaultDisplay().getWidth();
		 height = wm.getDefaultDisplay().getHeight();

		mapView.getMap().setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
			@Override
			public void onMapLoaded() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mapScreenShot();
					}
				},500);
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bitmap bitmap = Bitmap.createBitmap(rlContent.getWidth(),
						rlContent.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				rlContent.draw(canvas);
				hidMapBitmap = bitmap;
			}
		},100);

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
//		Bitmap bitmap = Bitmap.createBitmap(rlContent.getWidth(),
//				rlContent.getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas canvas = new Canvas(bitmap);
//		rlContent.draw(canvas);
//		hidMapBitmap = bitmap;
	}

	private void mapScreenShot(){
		mapView.getMap().getMapScreenShot(new AMap.OnMapScreenShotListener() {
			@Override
			public void onMapScreenShot(Bitmap bitmap) {
				Log.d("mapView","onMapScreenShot");
			}

			@Override
			public void onMapScreenShot(Bitmap b, int i) {
				Log.d("mapView","i:"+i);
				if (bitmap!=null&&!bitmap.isRecycled()){
					bitmap.recycle();
				}
				bitmap=b;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					FileOutputStream fos = new FileOutputStream(
							Environment.getExternalStorageDirectory() + "/test_"
									+ sdf.format(new Date()) + ".png");
					boolean b2 = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

				}catch (Exception e){

				}
			}
		});
	}
	int width;
	int height;
	private Bitmap hidMapBitmap;
	private void showMap(){

		int finalRadius = height;
		int centerX=btnShowMap.getLeft()+btnShowMap.getWidth()/2;
		int centerY=btnShowMap.getTop();
		SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mapView, centerX, centerY, 0, finalRadius);
		animator.setInterpolator(new AccelerateInterpolator());

//		image_content.setBackgroundResource(R.drawable.content_films);
		animator.setDuration(1000);
//		content_overlay.setBackgroundColor(getColor(R.color.blue));
		content_overlay.setBackgroundDrawable(new BitmapDrawable(hidMapBitmap));
		animator.start();
		rlContent.setVisibility(View.GONE);
	}
	private void hidMap(){

		int finalRadius = height;
		int centerX=btnShowMap.getLeft()+btnShowMap.getWidth()/2;
		int centerY=btnShowMap.getTop();
		SupportAnimator animator = ViewAnimationUtils.createCircularReveal(mapView, centerX, centerY, finalRadius, 0);
		animator.setInterpolator(new AccelerateInterpolator());
//		image_content.setBackgroundResource(R.drawable.content_films);
		animator.setDuration(1000);

//		content_overlay.setBackgroundColor(getColor(R.color.blue));
		content_overlay.setBackgroundDrawable(new BitmapDrawable(hidMapBitmap));
		animator.addListener(new SupportAnimator.AnimatorListener() {
			@Override
			public void onAnimationStart() {
			}
			@Override
			public void onAnimationEnd() {
				rlContent.setVisibility(View.VISIBLE);
			}
			@Override
			public void onAnimationCancel() {
			}
			@Override
			public void onAnimationRepeat() {
			}

		});
		animator.start();

	}
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		setMapCustomStyleFile(this);
		basicmap = (Button)findViewById(R.id.basicmap);
		basicmap.setOnClickListener(this);
		rsmap = (Button)findViewById(R.id.rsmap);
		rsmap.setOnClickListener(this);
		nightmap = (Button)findViewById(R.id.nightmap);
		nightmap.setOnClickListener(this);
		navimap = (Button)findViewById(R.id.navimap);
		navimap.setOnClickListener(this);

		mStyleCheckbox = (CheckBox) findViewById(R.id.check_style);

		mStyleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				aMap.setMapCustomEnable(b);
			}
		});

	}

	private void setMapCustomStyleFile(Context context) {
		String styleName = "style_json.json";
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		String filePath = null;
		try {
			inputStream = context.getAssets().open(styleName);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);

			filePath = context.getFilesDir().getAbsolutePath();
			File file = new File(filePath + "/" + styleName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			outputStream.write(b);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();

				if (outputStream != null)
					outputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		aMap.setCustomMapStylePath(filePath + "/" + styleName);

		aMap.showMapText(false);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.basicmap:
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
				break;
			case R.id.rsmap:
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
				break;
			case R.id.nightmap:
				aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
				break;
			case R.id.navimap:
				aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
				break;
		}

		mStyleCheckbox.setChecked(false);

	}

}
