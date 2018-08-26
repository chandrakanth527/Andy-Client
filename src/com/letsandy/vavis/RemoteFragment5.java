package com.letsandy.vavis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RemoteFragment5 extends Fragment {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

	public static final RemoteFragment5 newInstance(String message) {
		RemoteFragment5 f = new RemoteFragment5();
		Bundle bdl = new Bundle(1);
		bdl.putString(EXTRA_MESSAGE, message);
		f.setArguments(bdl);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		@SuppressWarnings("unused")
		String message = getArguments().getString(EXTRA_MESSAGE);
		View v = inflater.inflate(R.layout.remote5, container, false);		
		return v;
	}

}
