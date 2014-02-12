/*
 * Copyright (c) 2014 Vishal Disawar
 *
 * Dual licensed under Apache2.0 and MIT Open Source License (included below):
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.coursera.cardswap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

/**
 * Main activity of this app. This is where the main business card is shown and where the NFC
 * sends the information. More info on NFC can be found at
 * <a href="http://developer.android.com/guide/topics/connectivity/nfc/nfc.html">NFC Basics</a>
 *
 * @author Vishal Disawar
 */
public class CardSwap extends Activity
        implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private NfcAdapter nfcAdapter;
    private static final int MESSAGE_SENT = 1;

    private static final int CARD_MADE = 1;

    private String personName;
    private String business;
    private String address;
    private String cityStateZip;
    private String number;
    private String email;
    private String website;

    /**
     * A handler runs in a background thread, and communicates with other
     * handlers and threads that send a message to this activity. In this case
     * this handler will receive a message from onNdefPushComplete
     */
    private volatile Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_swap);

        if (savedInstanceState == null) {
            // the fragment manager has methods to add and remove fragments
            // in this case we are adding a new Card Fragment our empty FrameLayout
            // in the activity_card_swap.xml file
            getFragmentManager().beginTransaction()
                    .add(R.id.card_fragment_container, new CardFragment())
                    .commit(); // all fragment transactions are completed with a call to commit()
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get a reference to all the TextViews in fragment_card.xml
        final TextView personNameTView = (TextView) findViewById(R.id.person_name);
        final TextView businessNameTView = (TextView) findViewById(R.id.business_name);
        final TextView addressTView = (TextView) findViewById(R.id.person_address);
        final TextView cityStateZipTView = (TextView) findViewById(R.id.person_citystate);
        final TextView numberTView = (TextView) findViewById(R.id.person_number);
        final TextView emailTView = (TextView) findViewById(R.id.person_email);
        final TextView websiteTView = (TextView) findViewById(R.id.person_website);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean edited = sp.getBoolean(CardEdit.CARD_EDITED_KEY, false);

        if (!edited) {
            // get the display of the window to get system orientation changes
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                    .getDefaultDisplay();
            int rotation = display.getRotation(); // get the rotation of the
            // screen
            // we want to hide the instructions for when the user goes in
            // landscape mode since we
            // want the card to take up most of the screen

            // reference to TextViews in activity_card_swap.xml
            TextView sampleTitle = (TextView) findViewById(R.id.sample_title);
            TextView instructions = (TextView) findViewById(R.id.instructions);

            if (rotation == Surface.ROTATION_90
                    || rotation == Surface.ROTATION_270) {
                sampleTitle.setVisibility(View.GONE);
                instructions.setVisibility(View.GONE);
            } else {
                sampleTitle.setVisibility(View.VISIBLE);
                instructions.setVisibility(View.VISIBLE);
            }
        }

        // only ready the activity for NFC if the user put their own info for their card
        if (edited) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this); //get the adapter for NFC for this phone

            if (nfcAdapter == null)
                Toast.makeText(this, "NFC is unavailable", Toast.LENGTH_LONG).show();
            else {
                //create the callback if NFC is present
                nfcAdapter.setNdefPushMessageCallback(this, this);
                //register callback to listen for data sent success
                nfcAdapter.setOnNdefPushCompleteCallback(this, this);
            }

            // Get the text from the card for each info to send over
            personName = personNameTView.getText().toString();
            business = businessNameTView.getText().toString();
            address = addressTView.getText().toString();
            cityStateZip = cityStateZipTView.getText().toString();
            number = numberTView.getText().toString();
            email = emailTView.getText().toString();
            website = websiteTView.getText().toString();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this); //disable NFC when activity is not in view
    }

    /*
     * Use this method to create and prepare the data and encapsulate in an NdefMessage.
     * The NdefMessage sends the data through NFC beam. This method gets called when the phones
     * touch together.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {

        String mimeType = "application/com.coursera.cardswap";
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));

        //encapsulate the data in an array of NdefRecords
        NdefRecord[] ndefRecord = new NdefRecord[]{

                // each NdefRecord has to have a tnf, type, id, and payload.
                // the tnf specifies what the data will be which we specify as a MIME.
                // the type is what the intent filter (in the manifest) for an activity will look for.
                // We give a default byte for the id. And the payload is the data in bytes.
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], personName.getBytes()),
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], business.getBytes()),
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], address.getBytes()),
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], cityStateZip.getBytes()),
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], number.getBytes()),
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], email.getBytes()),
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], website.getBytes()),

                // this creates the application record for this app, which takes the user to this app
                // in the google play store, if when the NFC happens, the receiving phone doesn't have
                // this app installed
                NdefRecord.createApplicationRecord("com.coursera.cardswap")
        };

        //An NdefMessage is what gets sent in the NFC and we pass the array of our NdefRecords
        //to encapsulate it in the NdefMessage
        return new NdefMessage(ndefRecord);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // You have to be careful with memory leaks with inner classes and
        // handlers
        // In this app we remove the handler and messages in onStop
        // To ensure the handler cannot outlive the Activity object
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // if the message 'what' matches the constant MESSAGE_SENT, it
                // means the callback
                // to onNdefPushComplete happened which means the NFC was a
                // success
                if (msg.what == MESSAGE_SENT) {
                    Toast.makeText(getApplicationContext(), "Contact Sent!",
                            Toast.LENGTH_LONG).show();
                }
            }

        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        Handler oldHandler = handler;
        handler = null;
        if (oldHandler != null) {
            oldHandler.removeMessages(MESSAGE_SENT);
        }
    }

    /**
     * This method handles the event for when the NFC beam gets completed
     *
     * @param nfcEvent the NfcEvent
     */
    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        //A handler is needed to send a message to this activity
        //because NFC happens in a Binder thread.
        //The target is created by the OS, so we just have to send it.
        //We give obtainMessage the parameter MESSAGE_SENT so we can check when we handle
        //the message that message was sent from this method
        Handler h = handler;
        if (h != null)
            h.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_swap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_card_edit) {
            // start activity for result starts another activity expecting to get something back
            // things that are sent back could be data or just indication of success
            startActivityForResult(new Intent(this, CardEdit.class), CARD_MADE);
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    //this method executes when the intent that started the sub-activity finished
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CARD_MADE) {
            if (resultCode == RESULT_OK)
                // if we get passed RESULT_OK it is an indication of success, so tell the user via
                // a Toast. A toast is text that appears on the screen for a short amount of time
                Toast.makeText(this, "Your changes were successfully saved", Toast.LENGTH_SHORT)
                        .show();
        }
    }

}

