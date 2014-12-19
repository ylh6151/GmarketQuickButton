package pe.sbk.alwaysontop;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class AlwaysOnTopService extends Service {
	//private TextView mPopupView;							//항상 보이게 할 뷰
	private View mImageView;
	private WindowManager.LayoutParams mParams;		//layout params 객체. 뷰의 위치 및 크기를 지정하는 객체
	private WindowManager mWindowManager;			//윈도우 매니저
	private SeekBar mSeekBar;								//투명도 조절 seek bar
	private Boolean _enable = true;
	private LayoutInflater inflater;
	private final IBinder mBinder = new LocalBinder();
	ArrayList<String> quickmenu;
	List quickmenulist;
	
	private float START_X, START_Y;							//움직이기 위해 터치한 시작 점
	private int PREV_X, PREV_Y;								//움직이기 이전에 뷰가 위치한 점
	private int MAX_X = -1, MAX_Y = -1;					//뷰의 위치 최대 값
	
	/*private OnTouchListener mViewTouchListener = new OnTouchListener() {
		@Override public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:				//사용자 터치 다운이면
					if(MAX_X == -1)
						setMaxPosition();
					START_X = event.getRawX();					//터치 시작 점
					START_Y = event.getRawY();					//터치 시작 점
					PREV_X = mParams.x;							//뷰의 시작 점
					PREV_Y = mParams.y;							//뷰의 시작 점
					break;
				case MotionEvent.ACTION_MOVE:
					int x = (int)(event.getRawX() - START_X);	//이동한 거리
					int y = (int)(event.getRawY() - START_Y);	//이동한 거리
					
					//터치해서 이동한 만큼 이동 시킨다
					mParams.x = PREV_X + x;
					mParams.y = PREV_Y + y;
					
					optimizePosition();		//뷰의 위치 최적화
					mWindowManager.updateViewLayout(mPopupView, mParams);	//뷰 업데이트
					break;
			}
			
			return true;
		}
	};*/
	
	public class LocalBinder extends Binder {
		AlwaysOnTopService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AlwaysOnTopService.this;
        }
    }

	
	@Override
	public IBinder onBind(Intent arg0) { return mBinder; }
	
	@Override
	public void onCreate() {
		super.onCreate();
		/*
		quickmenu = new ArrayList<String>();

		quickmenu.add("HomeMain");
		quickmenu.add("Best");
		quickmenu.add("Search");
		quickmenu.add("MyG");
		
		quickmenulist = new ArrayList();
		for(int i=0 ; i<quickmenu.size() ; ++i) {
			quickmenulist.add(quickmenu.get(i));
		}

		mPopupView = new TextView(this);																//뷰 생성
		mPopupView.setText("이 뷰는 항상 위에 있다.\n갤럭시 & 옵티머스 팝업 뷰와 같음");	//텍스트 설정
		mPopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);								//텍스트 크기 18sp
		mPopupView.setTextColor(Color.BLUE);															//글자 색상
		mPopupView.setBackgroundColor(Color.argb(127, 0, 255, 255));								//텍스트뷰 배경 색
		
		mPopupView.setOnTouchListener(mViewTouchListener);										//팝업뷰에 터치 리스너 등록
		//최상위 윈도우에 넣기 위한 설정
		mParams = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. 
																					//포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
			PixelFormat.TRANSLUCENT);										//투명
		mParams.gravity = Gravity.LEFT | Gravity.TOP;						//왼쪽 상단에 위치하게 함.
		
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);	//윈도우 매니저 불러옴.
		mWindowManager.addView(mPopupView, mParams);		//최상위 윈도우에 뷰 넣기. *중요 : 여기에 permission을 미리 설정해 두어야 한다. 매니페스트에
*/		
		//addOpacityController();		//팝업 뷰의 투명도 조절하는 컨트롤러 추가
		
		inflater = LayoutInflater.from(this);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);	//윈도우 매니저 불러옴.
		addGmarketQuickButton();
		
	}
	
	/**
	 * 뷰의 위치가 화면 안에 있게 최대값을 설정한다
	 */
	private void setMaxPosition() {
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서
		
		MAX_X = matrix.widthPixels - mImageView.getWidth();			//x 최대값 설정
		MAX_Y = matrix.heightPixels - mImageView.getHeight();			//y 최대값 설정
	}
	
	/**
	 * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
	 */
	private void optimizePosition() {
		//최대값 넘어가지 않게 설정
		if(mParams.x > MAX_X) mParams.x = MAX_X;
		if(mParams.y > MAX_Y) mParams.y = MAX_Y;
		if(mParams.x < 0) mParams.x = 0;
		if(mParams.y < 0) mParams.y = 0;
	}
	
	/**
	 * 알파값 조절하는 컨트롤러를 추가한다
	 */
	/*private void addOpacityController() {
		mSeekBar = new SeekBar(this);		//투명도 조절 seek bar
		mSeekBar.setMax(100);					//맥스 값 설정.
		mSeekBar.setProgress(100);			//현재 투명도 설정. 100:불투명, 0은 완전 투명
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onStopTrackingTouch(SeekBar seekBar) {}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override public void onProgressChanged(SeekBar seekBar, int progress,	boolean fromUser) {
				mParams.alpha = progress / 100.0f;			//알파값 설정
				mWindowManager.updateViewLayout(mPopupView, mParams);	//팝업 뷰 업데이트
			}
		});
		
		//최상위 윈도우에 넣기 위한 설정
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. 
																					//포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
			PixelFormat.TRANSLUCENT);										//투명
		params.gravity = Gravity.LEFT | Gravity.TOP;							//왼쪽 상단에 위치하게 함.
		
		mWindowManager.addView(mSeekBar, params);
	}*/

	private void addGmarketQuickButton(){
		//mImageView = new View(this);							
		//mImageView.setImageResource(R.drawable.q_icon);
		//LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(40,40);		
		//mImageView.setLayoutParams(layoutParams);
		//mImageView.setAdjustViewBounds(true);
		
		mImageView = inflater.inflate(R.layout.quick_button, null);
		//최상위 윈도우에 넣기 위한 설정
		mParams = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. //포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
			PixelFormat.TRANSLUCENT);										//투명
		mParams.gravity = Gravity.LEFT | Gravity.TOP;							//왼쪽 상단에 위치하게 함.
		mWindowManager.addView(mImageView, mParams);
		
		try {	
			mImageView.setOnTouchListener(mButtonTouchListener);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		

	}
	
	private OnTouchListener mButtonTouchListener = new OnTouchListener() {
		@Override public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:				//사용자 터치 다운이면
					if(MAX_X == -1)
						setMaxPosition();
					START_X = event.getRawX();					//터치 시작 점
					START_Y = event.getRawY();					//터치 시작 점
					PREV_X = mParams.x;							//뷰의 시작 점
					PREV_Y = mParams.y;							//뷰의 시작 점
					break;
				case MotionEvent.ACTION_MOVE:
					int x = (int)(event.getRawX() - START_X);	//이동한 거리
					int y = (int)(event.getRawY() - START_Y);	//이동한 거리
					
					//터치해서 이동한 만큼 이동 시킨다
					mParams.x = PREV_X + x;
					mParams.y = PREV_Y + y;
					
					optimizePosition();		//뷰의 위치 최적화
					mWindowManager.updateViewLayout(mImageView, mParams);	//뷰 업데이트
					break;
				case MotionEvent.ACTION_UP:
					/*Display display = mWindowManager.getDefaultDisplay();
					if(mParams.x > display.getWidth()/2){
						mParams.x =display.getWidth();*/
					
					if(mParams.x > MAX_X/2){
						mParams.x = MAX_X;
					}else{
						mParams.x = 0;
					}
					
					
					mWindowManager.updateViewLayout(mImageView, mParams);	//뷰 업데이트
					
					//initiatePopupWindow(mImageView);
					
					/*if(mParams.x == PREV_X && mParams.y == PREV_Y){
						initiateMenuWindow(mParams);
					}*/
					
					
					if(Math.abs(PREV_X - mParams.x) <= 1 && Math.abs(PREV_Y - mParams.y)<= 1){
						initiateMenuWindow(mParams);
					}
					break;
			}
			
			return true;
		}
	};

/*	private void initiatePopupWindow(View anchor) {
		try {
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			ListPopupWindow popup = new ListPopupWindow(this);
			popup.setAnchorView(anchor);
			popup.setWidth((int) (display.getWidth()/(1.5)));
			popup.setAdapter(new CustomAdapter(getApplicationContext(), R.layout.row, quickmenulist));
			popup.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id3) {
				}
			});
			popup.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public void setMenuClickEvent(String url){
		
		final View circleMenu = inflater.inflate(R.layout.circle_menu, null);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url) );
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			mWindowManager.removeView(circleMenu);
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			return;
		}
		
	}	
	
	private void initiateMenuWindow(final LayoutParams params){
		try{
			final View circleMenu = inflater.inflate(R.layout.circle_menu, null);
			
			circleMenu.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mWindowManager.removeView(circleMenu);
					mWindowManager.addView(mImageView, params);
				}
			});
			
			DisplayMetrics matrix = new DisplayMetrics();
			mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서
		
			pe.sbk.alwaysontop.CircleLayout circleMenuView = (pe.sbk.alwaysontop.CircleLayout) circleMenu.findViewById(R.id.main_circle_layout);
			
			WindowManager.LayoutParams cParams = new WindowManager.LayoutParams(matrix.widthPixels, matrix.heightPixels,WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. //포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
					PixelFormat.TRANSLUCENT);
			
			Resources r = getApplicationContext().getResources();
			int px = (int) TypedValue.applyDimension(
			        TypedValue.COMPLEX_UNIT_DIP,
			        250, 
			        r.getDisplayMetrics()
			);
			
			RelativeLayout.LayoutParams mp = (RelativeLayout.LayoutParams)circleMenuView.getLayoutParams();			
			if(params.x == 0){
				mp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				mp.leftMargin = -(px/2);
			}else{
				mp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mp.rightMargin = -(px/2);
			}
						
			mp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			mp.topMargin = params.y - (px/2);
			
			circleMenuView.setLayoutParams(mp);
			
			//cParams.x = cParams.x - (circleMenu.getWidth()/2);
			//cParams.y = cParams.y + (circleMenu.getHeight()/2);
			mWindowManager.removeView(mImageView);
			mWindowManager.addView(circleMenu, cParams);
			
			
			/*pe.sbk.alwaysontop.CircleImageView homeBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_home_image);
			
			homeBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://main");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView attendanceBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_attendance_image);
			
			attendanceBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://main?pluszone");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView bestBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_best_image);
			
			bestBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://main?best");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView ecouponBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_ecoupon_image);
			
			ecouponBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://main?gift");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView searchBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_search_image);
			
			searchBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://search");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView eventBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_event_image);
			
			eventBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://openwindow?title=cart&targetUrl=http://mobile.gmarket.co.kr/Display/SpecialShoppingList");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView orderBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_order_image);
			
			orderBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://openwindow?title=mmyg&targetUrl=http://mmyg.gmarket.co.kr");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView favoriteBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_favorite_image);
			
			favoriteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://openwindow?title=cart&targetUrl=http://mmyg.gmarket.co.kr/Favorite/MyFavoriteItems");
				}
			});
			
			pe.sbk.alwaysontop.CircleImageView cartBtn = (pe.sbk.alwaysontop.CircleImageView)circleMenuView.findViewById(R.id.q_cart_image);
			
			cartBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setMenuClickEvent(circleMenu, "gmarket://openwindow?title=cart&targetUrl=http://mescrow.gmarket.co.kr/ko/cart");
				}
			});*/
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 가로 / 세로 모드 변경 시 최대값 다시 설정해 주어야 함.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setMaxPosition();		//최대값 다시 설정
		optimizePosition();		//뷰 위치 최적화
	}
	
	@Override
	public void onDestroy() {
		if(mWindowManager != null) {		//서비스 종료시 뷰 제거. *중요 : 뷰를 꼭 제거 해야함.
			//if(mPopupView != null) mWindowManager.removeView(mPopupView);
			if(mImageView != null) mWindowManager.removeView(mImageView);
			if(mSeekBar != null) mWindowManager.removeView(mSeekBar);
		}
		super.onDestroy();
	}
}