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
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity launches when NFC occurs. It shows it as a dialog box.
 * @author vishal disawar
 */
public class ContactReceive extends Activity {

    private String personName;
    private String business;
    private String address;
    private String cityStateZip;
    private String number;
    private String email;
    private String website;
    private String fullAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_receive);
    }

    /*
     * Called when this activity gets started by an Intent, which in this case would be when
     * the NFC launches an intent for this activity
     */
    @Override
    protected void onNewIntent(Intent intent) {
        // update the intent for getIntent() in processNFC() in onResume()
        this.setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            // only process the NFC if the activity was started by an NFC intent as
            // specified via an ACTION_NDEF_DISCOVERED
            processNFC(getIntent());
        }
    }

    /**
     * Process and handle the NFC beam and parse the data
     *
     * @param intent
     */
    private void processNFC(Intent intent) {

        // receive the data from the intent and store in an array
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        //The NdefMessage that was sent from the NFC is the first thing stored in the rawMessages array
        NdefMessage ndefMessage = (NdefMessage) rawMessages[0];

        // parse the data that is stored as bytes into a String
        // The ndefMessage has the array of records of the person's data, so we get
        // each data and store it into the appropriate variable
        personName = new String(ndefMessage.getRecords()[0].getPayload());
        business = new String(ndefMessage.getRecords()[1].getPayload());
        address = new String(ndefMessage.getRecords()[2].getPayload());
        cityStateZip = new String(ndefMessage.getRecords()[3].getPayload());
        number = new String(ndefMessage.getRecords()[4].getPayload());
        email = new String(ndefMessage.getRecords()[5].getPayload());
        website = new String(ndefMessage.getRecords()[6].getPayload());
        fullAddress = address + ", " + cityStateZip;

        // Add the name of the person in the dialog box to tell the receiver of the card from whom
        // they are receiving the contact from
        final TextView tv = (TextView) findViewById(R.id.contact_receive);
        tv.append(" " + personName);
    }

    /**
     * Adds the contacts to contacts list if user presses accept button.
     * This launches the contacts app on the phone and allows the user to edit
     * or add a new contact. The data is filled into the fields itself view the intent
     * and the extras we send to the contacts app.
     *
     * @param view the View
     */
    public void accept(View view) {
        // creates a new intent to insert or edit a contact
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
        //set the MIME type to match the contacts provider
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

        // insert each data into the appropriate field
        intent.putExtra(ContactsContract.Intents.Insert.NAME, personName);
        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, business);
        intent.putExtra(ContactsContract.Intents.Insert.POSTAL, fullAddress);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        intent.putExtra(ContactsContract.Intents.Insert.NOTES, website);

        startActivity(intent);
        finish();
    }

    /**
     * Dismisses the app if user presses decline button
     *
     * @param view the View
     */
    public void decline(View view) {
        Toast.makeText(this, "Contact Declined", Toast.LENGTH_LONG).show();
        finish();
    }
}
