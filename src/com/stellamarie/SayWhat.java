package com.stellamarie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SayWhat extends Activity implements OnClickListener, OnInitListener {
	
	// setting all the default settings
	protected static final String TAG = null;
	private AdapterView<SpinnerAdapter> fromLanguages;
	private AdapterView<SpinnerAdapter> toLanguages;
	private String[] langsNames;
	private String[] langCodes;
	private TextView inputTextView;
	private TypedArray Flags;
	private String[] fromLangs;
	private String[] fromCodes;
	private String[] locs;
	private TypedArray fromFlags;
	private int defaultPosition = 47;  // 47 Spanish
	private int toPosition = defaultPosition; 
	private int defaultFrom = 3; // 3 US English
	private int fromPosition = defaultFrom;
	private String defaultString = "";
	private Locale defaultLocale = Locale.UK;
	private ImageView speakButton = null;
	private View retryBtn;
	private View resetButton;
	private int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private String finalString;
	
	// audrey is the voice flag, with audrey set to 1, there is recognized and translated speech to say back
	private int audrey = 0;
	private int selected;
	private SpeechRecognition speechreg = null;
	private TextToSpeech talker;
	
    // Saved preferences
    private static final String FROM = "from";
    private static final String TO = "to";
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";


	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        Intent intent = getIntent();
        String path = intent.getStringExtra("com.stellamarie.saywhat.Path");
        if (path == null) {
            path = "";
        }      
		initialView();
    }

	@Override
    public void onClick(View v) {
		String TAG = "ON CLICK";
		try {
		switch (v.getId()) {
	  		case (R.id.languages):
	  			toPosition = toLanguages.getSelectedItemPosition();
	  			selected = 1;
				speakButton.setOnClickListener(this);
				speakButton.setEnabled(true);
	  			break;

	  		case (R.id.reset): 
	  			audrey = 0;
				selected = 0;
				Intent i = getPackageManager()
					.getLaunchIntentForPackage( this.getBaseContext().getPackageName() );
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				initialView();
	  			break;
	  		
	  		case (R.id.retry): 
	  			audrey = 0;
	  			inputTextView.setText("");
	  			fromPosition = fromLanguages.getSelectedItemPosition();
	  			toPosition = toLanguages.getSelectedItemPosition();
				fromLanguages.setSelection(fromPosition);	
				toLanguages.setSelection(toPosition);
				speakButton.setImageResource(R.drawable.redmicrophone);
				speakButton.setOnClickListener(this);
				speakButton.setEnabled(true);
				selected = 1;
				if (talker != null) {
		    		talker.stop();
		    		talker.shutdown();
		    	}
	  			break;
	  			
	  		case (R.id.mic):
	  			if ( audrey > 0 ) {
	  				say(finalString);
	  			} else {
	  				speechreg = new SpeechRecognition(SayWhat.this, fromLanguages, toLanguages); 
	  			}
	  			break;
	  			
	  	    default:
	  			toPosition = defaultPosition;
				inputTextView.setText("");
				initialView();
				selected = 0;
				audrey = 0;
	  			break;
		} } catch (Exception e) {
			Log.d(TAG, e.toString());
		}
    }
		
	public void initialView() {
		setContentView(R.layout.main);
		if (talker != null) {
    		talker.stop();
    		talker.shutdown();
    	}
		String TAG = "INITIAL VIEW";
		this.getApplicationContext();
		//Locale[] locations = Locale.getAvailableLocales();
		//String lt = "";
		//for (int n =  0; n < locations.length; n++) {
		//	lt += locations[n].toString() + ". \n";
		//}
		
		// setting all the array values
		// the array index is the same for each matched sets
		// all the to arrays the same
		// test - see if it caught strings
		langsNames = getResources().getStringArray(R.array.langs);
		langCodes = getResources().getStringArray(R.array.codes);
		Flags = getResources().obtainTypedArray(R.array.flags);
		
		// all the from arrays have the same index
		fromLangs = getResources().getStringArray(R.array.from_langs);
		fromCodes = getResources().getStringArray(R.array.from_codes);
		fromFlags = getResources().obtainTypedArray(R.array.from_flags);
		locs = getResources().getStringArray(R.array.locs);
		
		//setting all the initial buttons
		speakButton = (ImageView) findViewById(R.id.mic);
        speakButton.setImageResource(R.drawable.redmicrophone);
        inputTextView = (TextView) findViewById(R.id.input);
		toLanguages = (Spinner) findViewById(R.id.languages);
		fromLanguages = (Spinner) findViewById(R.id.from_langs);
		retryBtn = (Button) findViewById(R.id.retry);
		resetButton = (Button) findViewById(R.id.reset);
		
		//filling the spinner adapter
		try {
			//toAdapter = ArrayAdapter.createFromResource(this, R.array.langs, android.R.layout.simple_spinner_item);
			ArrayAdapter<?> fromAdapter = new LanguageAdaptor(SayWhat.this, R.layout.row, fromLangs, fromFlags);
			ArrayAdapter<?> toAdapter = new LanguageAdaptor(SayWhat.this, R.layout.row, langsNames, Flags);
			fromLanguages.setAdapter(fromAdapter);
			fromLanguages.setSelection(defaultFrom);
			toLanguages.setAdapter(toAdapter);	
			toLanguages.setSelection(defaultPosition);
			//toLanguages.setSelection(randomNumber());  //just for fun, just to test if worked
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		
		retryBtn.setOnClickListener(this);
		resetButton.setOnClickListener(this);
		defaultString = getString(R.id.input);
		speakButton.setOnClickListener(this);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(true);
        }
	}
    
    private int randomNumber() {
    	int Min = 0;
    	int Max = langsNames.length;
    	float dr = (float) (Math.random() * ( Max - Min ));
    	int rand = Math.round(dr);
    	return rand;
    }
 
	private void confirmView(List<String> text) {
		String str = "";
		String TAG = "CONFIRM VIEW";
		String top = "";
		toPosition = toLanguages.getSelectedItemPosition();
		fromPosition = fromLanguages.getSelectedItemPosition();
		
		try {
			talker = new TextToSpeech(this, this);
			Log.d(TAG, "set talker");
		} catch (Exception t) {
			Log.d(TAG, t.toString());
		}
		
		retryBtn = (Button) findViewById(R.id.retry);
		resetButton = (Button) findViewById(R.id.reset);
		retryBtn.setOnClickListener(this);
		resetButton.setOnClickListener(this);
		Log.d(TAG, "set buttons");
		
		try {
			speakButton = (ImageView) findViewById(R.id.mic);
	        speakButton.setImageResource(R.drawable.audio);
	        speakButton.setOnClickListener(this);
	        speakButton.setEnabled(true);
	        audrey = 1;
			Log.d(TAG, "set speak button");
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
		
        if (text.size() > 0) {
        	speakButton.setEnabled(true);
        	
        	top = text.toString().replaceAll("\\W+", ". ");
        	Log.d(TAG, top);
        	
        	str = fromLangs[fromPosition] + " to " + langsNames[toPosition] + " \n";
            String translatedWord = "";
            
			try {
				if ( langsNames[toPosition].contains("Chinese") ) { 
					// array wouldn't take hyphen in simplified chinese, but need it for translation
					translatedWord = TranslateM.translate(top, fromCodes[fromPosition], "zh-CN");
					Log.d(TAG, "chinese word");
				} else {
					translatedWord = TranslateM.translate(top, fromCodes[fromPosition], langCodes[toPosition]);
				}
				translatedWord = translatedWord.replaceAll(" ", ". ");
				str += top + ". \n" + " in " + langsNames[toPosition] + " is .\n " + translatedWord + "\n";
	        	Log.d(TAG, str);
				inputTextView.setText(str.replaceAll("\\.", ""));
			} catch (Exception e) {
				translatedWord = "";
				str += "no translation available";
				finalString = str;
				inputTextView.setText(e.toString());				
			}
			
			finalString = str;
			say(finalString);
            
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
		fromPosition = defaultFrom;
		inputTextView.setText("");
    	super.onDestroy();
    }
	
	public void onPause() {
		super.onPause();
		audrey = 1;
		selected = 1;
		if (talker != null) {
    		talker.stop();
    	}
		fromPosition = fromLanguages.getSelectedItemPosition();
		toPosition = toLanguages.getSelectedItemPosition();
		String f = fromLangs[fromPosition];
		String t = langsNames[toPosition];
		Editor edit = getPrefs(this).edit();
        String input = inputTextView.getText().toString();
        savePreferences(edit, f, t, input);
        edit.commit();
	}

	private void savePreferences(Editor edit, String from, String to, String input) {
        Log.d("SAVING PREFERENCES", "Saving preferences " + from + " " + to + " " + input);
        edit.putString(FROM, from);
        edit.putString(TO, to);
        edit.putString(INPUT, input);		
	}
	
    static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }


	public void say(String text2say){
		toPosition = toLanguages.getSelectedItemPosition();
		Locale loc = new Locale(langCodes[toPosition].toString());
  		String location = loc.getISO3Language();
  		Locale.setDefault(loc);
  		talker.setLanguage(loc);
    	talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    public void onInit(int status) {
    	say(finalString);	 
    }
    
    // this will take the spoken results, and set the language if there is one
    // it will send the rest of the words - language to the confirm view
    // won't return the speech results in a sub module
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	String TAG = "SPEECH RECOGNIZED";
    	String targetString = "";
    	String lang = null; 
    	List<String> words = null;
        List<String> langs = Arrays.asList(langsNames);
        String[] firstWords = null;
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
	        //Fill the list view with the strings the recognizer thought it could have heard
	        List<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Log.d(TAG, TAG);
	        try {
		        firstWords = matches.get(0).split(" ");
		        Log.d(TAG, firstWords.toString());
		        lang = firstWords[firstWords.length - 1];
		        fromPosition = fromLanguages.getSelectedItemPosition();
		        String transLang = TranslateM.translate(lang, fromCodes[fromPosition], "en");
		        transLang = "dummy " + transLang;
		        String[] tLang = transLang.split(" ");
		        lang = tLang[tLang.length - 1];
		        Log.d(TAG, transLang);
		        Log.d(TAG, tLang.toString());
		        // make recognized language title case for language search
		        lang = lang.substring(0, 1).toUpperCase() + lang.substring(1, lang.length()).toLowerCase();
		        Log.d(TAG, lang);
		        //setting to language to go to
			        if (lang.contains("En") || lang.contains("Eng") ) {
				    	lang = "English";
				    }
			        if (lang.contains("Gaelic") ) {
				    	lang = "Irish Gaelic";
				    }
			        Log.d(TAG, lang.toString());
			        if (langs.contains(lang)) {
			        	int langIndex = langs.indexOf(lang);
			        	toLanguages.setSelection(langIndex);
				        //get the first match - language
			        	words = Arrays.asList(firstWords);
				        words = words.subList(0, words.size() - 1);
				        if ( lang.contains("Gaelic") ) {
				        	words = words.subList(0, words.size() - 1);
				        }
				        Log.d(TAG, words.toString());
			        } else {
			        	//if no language, just the first match
			        	words = Arrays.asList(firstWords);
			        }		        
	        } catch (Exception e){
	        	Log.d(TAG, e.toString());
	        }
	        if(words != null && words.size() > 0) {
	    		//String regExp = "\\b\\w+\\b(?= (in|en)(\\s?\\w+){0,2}\\z)";//match target word based on 'in xxxlanguage'
	        	String regExp = "\\b\\w+\\b(?=(\\s?\\w+){0,2}\\z)";//match target word based on 'in xxxlanguage'
	    		List<String> targetWords = new ArrayList<String>();
	    		String targetWord;
	    		Pattern p = Pattern.compile(regExp);
	    		Matcher m;
	    		for(String tranStr : matches){
	    			m = p.matcher(tranStr);
	    			if(m.find()){
		    			targetWord = tranStr.substring(m.start(), m.end());
		    		}else{
		    			targetWord = tranStr.split(" ")[tranStr.split(" ").length - 2];
		    		}
	    			targetWords.add(targetWord);
	    		}
	            //String voiceToText = matches.get(0);
	            //return all match
	            //confirmView(targetWords);
	    		this.confirmView(words);
    	     }
    	super.onActivityResult(requestCode, resultCode, data);
        }
	}
 

}