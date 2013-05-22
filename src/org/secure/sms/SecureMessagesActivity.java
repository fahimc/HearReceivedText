package org.secure.sms;

import java.util.ArrayList;
import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import org.secure.sms.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SecureMessagesActivity extends Activity implements OnClickListener, OnItemClickListener, TextToSpeech.OnInitListener
{
    private TextToSpeech tts;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setTheme( android.R.style.Theme_Light );
        setContentView(R.layout.main);
        tts = new TextToSpeech(this, this);
        /**
         * You can also register your intent filter here.
         * And here is example how to do this.
         *
         * IntentFilter filter = new IntentFilter( "android.provider.Telephony.SMS_RECEIVED" );
         * filter.setPriority( IntentFilter.SYSTEM_HIGH_PRIORITY );
         * registerReceiver( new SmsReceiver(), filter );
        **/
        
        this.findViewById( R.id.UpdateList ).setOnClickListener( this );
        getMessages();
    }

    ArrayList<String> smsList = new ArrayList<String>();
    
	public void onItemClick( AdapterView<?> parent, View view, int pos, long id ) 
	{
        String msg =  smsList.get( pos ).toString();
        speakOut(msg);
        /*
		try
		{
		    	String[] splitted = smsList.get( pos ).split("\n");
			String sender = splitted[0];
			String encryptedData = "";
			for ( int i = 1; i < splitted.length; ++i )
			{
			    encryptedData += splitted[i];
			}
			String data = sender + "\n" + StringCryptor.decrypt( new String(SmsReceiver.PASSWORD), encryptedData );
			Toast.makeText( this, data, Toast.LENGTH_SHORT ).show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		*/
	}

	public void onClick( View v ) 
	{
        getMessages();

	}
    private void getMessages()
    {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query( Uri.parse( "content://sms/inbox" ), null, null, null, null);

        int indexBody = cursor.getColumnIndex( SmsReceiver.BODY );
        int indexAddr = cursor.getColumnIndex( SmsReceiver.ADDRESS );

        if ( indexBody < 0 || !cursor.moveToFirst() ) return;

        smsList.clear();

        do
        {
            String str = "Sender: " + cursor.getString( indexAddr ) + "\n" + cursor.getString( indexBody );
            smsList.add( str );
        }
        while( cursor.moveToNext() );


        ListView smsListView = (ListView) findViewById( R.id.SMSList );
        smsListView.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, smsList) );
        smsListView.setOnItemClickListener( this );
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {


            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
    private void speakOut(String msg) {



        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
    }
}