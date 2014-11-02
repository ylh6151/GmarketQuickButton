package pe.sbk.alwaysontop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AlwaysOnTopActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewById(R.id.start).setOnClickListener(this);		//시작버튼
		findViewById(R.id.end).setOnClickListener(this);			//중시버튼
	}
    
	@Override
	public void onClick(View v) {
		int view = v.getId();
		if(view == R.id.start)
			startService(new Intent(this, AlwaysOnTopService.class));	//서비스 시작
		else
			stopService(new Intent(this, AlwaysOnTopService.class));	//서비스 종료
	}
}