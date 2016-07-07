package com.stellamarie;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;
import com.stellamarie.R;

class Language {

		private String   _name;
		private Drawable _img;
		private String   _val;
		private Button button;
		private int _no;
		private int select;
		private static final int RESOURCE = R.layout.row;

		public Language( Drawable img, String name, String val, int no, Button b ) {
			init(img, name, val, no, b);
		}

		public String getName() {
			return _name;
		}

		public Drawable getImg() {
			return _img;
		}

		public String getVal() {
			return _val;
		}
		
		public Button getButton() {
			return button;
		}
		
        public void configureButton(Button b) {
        	try {
        		Drawable flag = null;
        		b.setTag(this);
        		Log.d("Languages button name", "set tag");
        		b.setText(getName());
        		Log.d("Languages button name", getName());
        		flag = getImg();
        		Log.d("Languages button flag", "flag");
        		b.setCompoundDrawables(flag, null, null, null);
        		b.setCompoundDrawablePadding(5);
       			Log.d("Languages button set", b.toString());
       			b.setId(_no);
        		Log.d("Languages button ID", "number " + _no);
        		button = b;
        	} catch (Exception e) {
        		Log.d("Langauges button exception", e.toString());
        	}
        }
        
        private void init(Drawable i, String s, String v, int n, Button but) {
        	_name = s;
			_img = i;
			_val = v;
			_no = n;  
			configureButton(but);
			Log.d("Languages method", _name + " " + _val);
        }


	}
