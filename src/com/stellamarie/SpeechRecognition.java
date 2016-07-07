package com.stellamarie;

import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import com.stellamarie.R;

public class SpeechRecognition extends SayWhat {
	
	private String TAG = "SPEECH RECOGNITION";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private Activity speech;
	private String[] langsNames;
	private String[] langCodes;
	private TypedArray Flags;
	private String[] fromLangs;
	private String[] fromCodes;
	private TypedArray fromFlags;
	private String[] locs;
	private AdapterView<SpinnerAdapter> fromLanguages;
	private AdapterView<SpinnerAdapter> toLanguages;
	private int fromPosition;
	private int toPosition;
	private Context context;
	private Intent speechIntent;
	
	
	public SpeechRecognition(SayWhat sayWhat, AdapterView<SpinnerAdapter> fa2, AdapterView<SpinnerAdapter> ta2) {
		this.speech = sayWhat;
		this.fromLanguages = fa2;
		this.toLanguages = ta2;
		startVoiceRecognitionActivity();
	}

	protected void startVoiceRecognitionActivity() {
		Log.d(TAG, "Intent Speech started");
		Resources res = speech.getResources();
		locs = res.getStringArray(R.array.locs);
		fromCodes = res.getStringArray(R.array.from_codes);
		langsNames = res.getStringArray(R.array.langs);
		fromLangs = res.getStringArray(R.array.from_langs);
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        fromPosition = this.fromLanguages.getSelectedItemPosition();
        Log.d(TAG, "from languages " + fromLangs[fromPosition]);
		Locale loc = new Locale(fromLangs[fromPosition].toString());
  		String location = loc.getISO3Language();
  		Locale.setDefault(loc);
  		//Log.d(TAG, RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS.toString());
  		Log.d(TAG, "set location " + locs[fromPosition]);
  		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, fromCodes[fromPosition]);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SayWhat (i.e. Where is the kitchen? Spanish)");
        Log.d(TAG, "Intent Speech added");
        Toast t = Toast.makeText(speech, "Preparing to launch voice recognition one moment...", Toast.LENGTH_LONG);
        t.show();
        try {
        	speech.startActivityFromChild(this, intent, VOICE_RECOGNITION_REQUEST_CODE);
        	Log.d(TAG, "Intent Speech startActivityForResult");
            
        } catch(ActivityNotFoundException e) {
	        Log.e("Voice Recognition Error - not found",e.toString());
	        t = Toast.makeText(speech, "No voice recognizer - Please install Voice Search", Toast.LENGTH_SHORT);
	        t.show();
        }
    }
	
}
