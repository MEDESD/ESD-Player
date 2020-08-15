package com.medesd.esdplayer.Files.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.medesd.esdplayer.R;

public class FilesActivity extends AppCompatActivity {
	@SuppressLint("StaticFieldLeak")
	public static Context context;
	public static Handler handler;

	private Toolbar toolbar;
	private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
	private Fragment currentFragment=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		context=this;
		handler=new Handler(context.getMainLooper());
		initialCalling();
		 
	}
	
	private void initialCalling(){
		fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

		getFragment();
		attachedFragment();
	}
	
	
	
	private void attachedFragment(){
		try {
			if (currentFragment != null) {
				String title = "Video";
				if (fragmentTransaction.isEmpty()) {
					fragmentTransaction.add(R.id.fragment_container, currentFragment,"" + currentFragment.toString());
					fragmentTransaction.commit();
					toolbar.setTitle(title);
				}else {
					fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.fragment_container, currentFragment,"" + currentFragment.toString());
					fragmentTransaction.commit();
					toolbar.setTitle(title);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void getFragment(){
			currentFragment = new VideoFragment();
	}
}
