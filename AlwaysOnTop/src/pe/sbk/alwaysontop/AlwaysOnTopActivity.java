package pe.sbk.alwaysontop;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AlwaysOnTopActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	//private ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.start).setOnClickListener(this);		//���۹�ư
		findViewById(R.id.end).setOnClickListener(this);			//�߽ù�ư
/*		
		if(isServiceRunning("pe.sbk.alwaysontop.AlwaysOnTopService")){
			if (am.getRunningTasks(1).get(0).topActivity.getPackageName().equals("com.android.launcher")) {
				Log.d("minju", "startservice");
			}else{
				stopService(new Intent(this, AlwaysOnTopService.class));
			}
		}*/
		
	}
    
	@Override
	public void onClick(View v) {
		int view = v.getId();
		if(view == R.id.start){
			final Intent i = new Intent(this, AlwaysOnTopService.class);
			startService(i);	//���� ����
			bindService(i, mConnection, Context.BIND_AUTO_CREATE);
		}
		else
			unbindService(mConnection);
			stopService(new Intent(this, AlwaysOnTopService.class));	//���� ����
			
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			
		}
	};
	/*public boolean isServiceRunning(String serviceClassName){
		
		//final ActivityManager activityManager = (ActivityManager)Application.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
     }*/
	

/*	//List<RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
	
	//String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
	//Log.d("minju", "packageName "+packageName);
	
	
	
	List<ActivityManager.RunningTaskInfo> alltasks = am.getRunningTasks(1);
	for (ActivityManager.RunningTaskInfo aTask : alltasks) {
		Log.d("minju", "packageName "+aTask.topActivity.getClassName());
		if(aTask.topActivity.getClassName().equals("com.android.launcher.Launcher")){
			//mWindowManager.removeView(mImageView);
			Log.d("minju", "packageName "+aTask.topActivity.getClassName());
			//mWindowManager.addView(mImageView, mParams);
		};
	}*/
	
}