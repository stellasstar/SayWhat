package com.stellamarie;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.stellamarie.R;

/* Definition of the list adapter...uses the View Holder pattern to
* optimize performance.
*/

class LanguageAdaptor extends ArrayAdapter<String> {

	private static final String TAG = "Language Adaptor";
	private TypedArray flags;
	private String[] languages = null;
	private Context context;
	private LayoutInflater inflater;
	private int RESOURCE = 0;

	public LanguageAdaptor(Context con, int layout, String[] objects, TypedArray f) {
		super(con, layout, objects);
		RESOURCE = layout;
		languages = objects;
		flags = f;
		context = con;
		inflater = LayoutInflater.from(context);
		Log.d( TAG, "started" );
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		Drawable img = null;
		View row = inflater.inflate(RESOURCE, parent, false);
		
		TextView label=(TextView) row.findViewById(R.id.lang);
		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		
		String txt = languages[position];
		Log.d(TAG, txt + " " + img);
		label.setText(txt);
		Log.d(TAG, "set text " + txt);
		
		img = flags.getDrawable(position);
		Log.d(TAG, "got image " + img);
		
		icon.setImageDrawable(img);
	    Log.d(TAG, "set image tag");
				
		return row;
	}	

}