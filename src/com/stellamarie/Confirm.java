package com.stellamarie;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class Confirm extends Activity implements OnClickListener, OnInitListener {
	
	protected static final String TAG = null;
	private AdapterView<SpinnerAdapter> fromLanguages;
	private AdapterView<SpinnerAdapter> toLanguages;
	private String[] langsNames;
	private String[] langCodes;
	private EditText inputTextView;
	private TypedArray Flags;
	private String[] fromLangs;
	private String[] fromCodes;
	private String[] locs;
	private TypedArray fromFlags;
	private int defaultPosition = 47;  // 47 Spanish
	private int toPosition = defaultPosition; 
	private int defaultFrom = 4; //Japanese
	private int fromPosition = defaultFrom;
	private View retryBtn;
	private View resetButton;
	private TextToSpeech talker;
	private String finalString;
	private Activity confirm;
	private String TAG1 = "CONFIRM";
	private int row = 0;

	public Confirm(int fp, int tp, SayWhat SW) {
		fromPosition = fp;
		toPosition = tp;
		defaultPosition = tp;
		defaultFrom = fp;
		confirm = SW;
		LayoutInflater inflater = (LayoutInflater) SW.getSystemService(
	      Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.confirm, null);
		Log.d(TAG1, "layout confirm");
		SW.setContentView(R.layout.confirm);
		Log.d(TAG1, "set layout confirm");
	}

	@Override
    public void onClick(View v) {
		String TAG = "ON CLICK";
		try {
		switch (v.getId()) {
	  		case (R.id.languages): 
	  			toPosition = toLanguages.getSelectedItemPosition();
			break;

	  		case (R.id.from_langs): 
	  			fromPosition = fromLanguages.getSelectedItemPosition();
	  			break;
	  			
	  		case (R.id.reset): 
			new Bundle();
	  	    	if (talker != null) {
	  	    		talker.stop();
	  	    		talker.shutdown();
	  	    	}
	  			toPosition = defaultPosition;
				inputTextView.setText("");	
				ImageView speakButton = (ImageView) confirm.findViewById(R.id.mic);
		        speakButton.setImageResource(R.drawable.redmicrophone);
		        speakButton.setOnClickListener(this);
		        speakButton.setEnabled(true);
				Intent i = confirm.getPackageManager()
					.getLaunchIntentForPackage( confirm.getBaseContext().getPackageName() );
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				confirm.startActivity(i);
			break;
	  		
	  		case (R.id.retry): 
				inputTextView.setText("");
			if (talker != null) {
		    		talker.stop();
		    		talker.shutdown();
		    	}
		        SayWhat sw1 = new SayWhat();
		        sw1.initialView();
	  			break;
	  			
	  		case (R.id.audio):
	  			say(finalString);
	  			break;
	  			
	  	    default:
	  			toPosition = defaultPosition;
				inputTextView.setText("");
				confirmView(null, row);
			break;
		} } catch (Exception e) {
			Log.d(TAG, e.toString());
		}
    }

	void confirmView(List<String> text, int row) { 
		String TAG = "CONFIRM VIEW";
		String str = "";
		row = row;
		
		Resources cp = confirm.getApplicationContext().getResources();
		
		langsNames = cp.getStringArray(R.array.langs);
		langCodes = cp.getStringArray(R.array.codes);
		Flags = cp.obtainTypedArray(R.array.flags);
		Log.d(TAG, "got langNames, langCodes, Flags");
		
		fromLangs = cp.getStringArray(R.array.from_langs);
		fromCodes = cp.getStringArray(R.array.from_codes);
		fromFlags = cp.obtainTypedArray(R.array.from_flags);
		locs = cp.getStringArray(R.array.locs);
		Log.d(TAG, "got from adapter resources");
		
		try {
			//inputTextView = (EditText)
			inputTextView = (EditText) confirm.findViewById(R.id.input);
			Log.d(TAG, "got default input " + inputTextView.getText());
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		
		Arrays.asList(fromCodes);
		Arrays.asList(fromLangs); 
		Arrays.asList(langCodes);
		
		Spinner toLanguages2 = (Spinner) confirm.findViewById(R.id.languages);
		Spinner fromLanguages2 = (Spinner) confirm.findViewById(R.id.from_langs);
		fromPosition = defaultFrom;
		toPosition = defaultPosition;
		Log.d(TAG, "got from adapter spinners");
		
		try {
			//toAdapter = ArrayAdapter.createFromResource(this, R.array.langs, android.R.layout.simple_spinner_item);
			ArrayAdapter<?> fromAdapter = new LanguageAdaptor(confirm.getApplicationContext(), row, fromLangs, fromFlags);
			ArrayAdapter<?> toAdapter = new LanguageAdaptor(confirm.getApplicationContext(), row, langsNames, Flags);
			Log.d(TAG, "got array adapters");
			fromLanguages2.setAdapter(fromAdapter);
			fromLanguages2.setSelection(fromPosition);
			toLanguages2.setAdapter(toAdapter);	
			toLanguages2.setSelection(toPosition);
			Log.d(TAG, "got array adapters");
			//toLanguages.setSelection(randomNumber());  //just for fun, just to test if worked
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		
		fromPosition = fromLanguages2.getSelectedItemPosition();
		toPosition = toLanguages2.getSelectedItemPosition();
		Log.d(TAG, "From " + fromLangs[fromPosition] + " To " + langsNames[toPosition]);
		
		ImageView speakButton = (ImageView) confirm.findViewById(R.id.audio);
        speakButton.setImageResource(R.drawable.audio);
		speakButton.setOnClickListener(this);
		retryBtn = (Button) confirm.findViewById(R.id.retry);
		resetButton = (Button) confirm.findViewById(R.id.reset);
		retryBtn.setOnClickListener(this);
		resetButton.setOnClickListener(this);
		Log.d(TAG, "set buttons");

		int end = 5;  //target amount of recognized items
		List<String> top = null;

        if (text.size() > 0) {
        	speakButton.setEnabled(true);
        	
        	if (text.size() >= 5) {
        		top = text.subList(0,end);  //copy top recognized target words
        	} else {
        		top = text;
        	}
        	
            //str = "top " + String.valueOf(end) + " recognized words are: " + top.toString().replaceAll("\\W+",". ") + "\n";
        	str = fromLangs[fromPosition] + ". to " + langsNames[toPosition] + ". \n";
        	String translatedWord = "";
            
			try {
				if ( langsNames[toPosition].contains("Chinese") ) { 
					// array wouldn't take hyphen in simplified chinese, but need it for translation
					translatedWord = TranslateM.translate(top.get(0), fromCodes[fromPosition], "zh-CN");
					Log.d(TAG, "chinese word");
				} else {
					translatedWord = TranslateM.translate(top.get(0), fromCodes[fromPosition], langCodes[toPosition]);
				}
				translatedWord.replace(langsNames[toPosition], "");
				str += top.get(0) + ". \n" + " in " + langsNames[toPosition] + " is .\n " + translatedWord + ".\n";
				inputTextView.setText(str);
			} catch (Exception e) {
				translatedWord = "";
				str += "no translation available";
				inputTextView.setText(e.toString());				
			}
			
			finalString = str;
			//say(finalString);
            
        } else {
            speakButton.setEnabled(false);
            inputTextView.setText("Speech Recognizer not present");
        }
	}
    
    public void onDestroy() {
    	if (talker != null) {
    		talker.stop();
    		talker.shutdown();
    	}
		toPosition = defaultPosition;
		inputTextView.setText("");
		((SayWhat) confirm).initialView();
		super.onDestroy();
    }

	public void say(String text2say){
		talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
		
    }
    
    public void onInit(int status) {
    	say(finalString);	 
    } 
    
    public void onPause() {
    	super.onPause();
    	if (talker != null) {
    		talker.stop();
    	}
    }	
 
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == confirm.RESULT_OK) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                talker = new TextToSpeech(Confirm.this, Confirm.this);
                say(finalString);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                    TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    
}