package com.benjaminbytheway.android.electricstorm;

import android.app.Activity;
import android.os.Bundle;

public class ElectricStormActivity extends Activity {
	
	private GraphView graphView = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		graphView = (GraphView) findViewById(R.id.graph_view);
		
		setContentView(R.layout.main);
		
	}
}