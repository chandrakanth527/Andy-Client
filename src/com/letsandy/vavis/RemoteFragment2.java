package com.letsandy.vavis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RemoteFragment2 extends Fragment {
	public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
	
	public static final RemoteFragment2 newInstance(String message)
	{
		RemoteFragment2 f = new RemoteFragment2();
		Bundle bdl = new Bundle(1);
	    bdl.putString(EXTRA_MESSAGE, message);
	    f.setArguments(bdl);
	    return f;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
		@SuppressWarnings("unused")
		String message = getArguments().getString(EXTRA_MESSAGE);
		View v = inflater.inflate(R.layout.remote2, container, false);
		
		
        return v;
    }
	
}
