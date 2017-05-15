package com;

import android.app.Application;

import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;


public class MyApp extends Application {
	public List<AndroidAppProcess> listApp;
	private static MyApp myApp = null;
	public MyApp() {
		super();
		listApp = new ArrayList<>();
		// TODO Auto-generated constructor stub
	}

	public static MyApp getInstance() {
		if (myApp == null) {
			synchronized (MyApp.class) {
				if (myApp == null) {
					myApp = new MyApp();
				}
			}
		}
		return myApp;
	}
}
