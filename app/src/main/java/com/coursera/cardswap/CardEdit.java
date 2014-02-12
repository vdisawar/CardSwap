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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * This activity takes care of creating a new card and editing it in the future
 *
 * @author Vishal Disawar
 */
public class CardEdit extends Activity {

    private EditText nameEditText;
    private EditText businessEditText;
    private EditText addressEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText zipCodeEditText;
    private EditText numberEditText;
    private EditText emailEditText;
    private EditText websiteEditText;

    // Have the keys for the SharedPreferences as constants so it makes it easier to refer to later
    static final String NAME_PREFERENCES_KEY = "name";
    static final String BUSINESS_PREFERENCES_KEY = "business";
    static final String ADDRESS_PREFERENCES_KEY = "address";
    static final String CITY_PREFERENCES_KEY = "city";
    static final String STATE_PREFERENCES_KEY = "state";
    static final String ZIPCODE_PREFERENCES_KEY = "zipcode";
    static final String NUMBER_PREFERENCES_KEY = "number";
    static final String EMAIL_PREFERENCES_KEY = "email";
    static final String WEBSITE_PREFERENCES_KEY = "website";
    static final String CARD_CREATED_KEY = "created";
    static final String CARD_EDITED_KEY = "edited";

    // use these variables to determine if the user is creating the
    // card for the first time or is editing it
    private boolean modeCreate;
    private boolean modeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_edit);

        // get a reference to all the xml EditTexts in activity_card_edit.xml
        nameEditText = (EditText) findViewById(R.id.input_name);
        businessEditText = (EditText) findViewById(R.id.input_business);
        addressEditText = (EditText) findViewById(R.id.input_address);
        cityEditText = (EditText) findViewById(R.id.input_city);
        stateEditText = (EditText) findViewById(R.id.input_state);
        zipCodeEditText = (EditText) findViewById(R.id.input_zipcode);
        numberEditText = (EditText) findViewById(R.id.input_number);
        emailEditText = (EditText) findViewById(R.id.input_email);
        websiteEditText = (EditText) findViewById(R.id.input_website);

        // retrieve information stored via Shared Preferences
        // this sets the mode of this activity based on if the user
        // is creating the card for the first time or is editing it
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // the first parameter gets the value of the key, otherwise if it doesn't
        // exist, it sets the value of the variable to a default value which
        // is specified in the second parameter
        modeCreate = prefs.getBoolean(CARD_CREATED_KEY, true);
        modeEdit = prefs.getBoolean(CARD_EDITED_KEY, false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // if user is editing, show the previous values they filled in so they don't have
        // re-do everything
        if (modeEdit) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            nameEditText.setText(prefs.getString(NAME_PREFERENCES_KEY, ""));
            businessEditText.setText(prefs.getString(BUSINESS_PREFERENCES_KEY, ""));
            addressEditText.setText(prefs.getString(ADDRESS_PREFERENCES_KEY, ""));
            cityEditText.setText(prefs.getString(CITY_PREFERENCES_KEY, ""));
            stateEditText.setText(prefs.getString(STATE_PREFERENCES_KEY, ""));
            zipCodeEditText.setText(prefs.getString(ZIPCODE_PREFERENCES_KEY, ""));
            numberEditText.setText(prefs.getString(NUMBER_PREFERENCES_KEY, ""));
            emailEditText.setText(prefs.getString(EMAIL_PREFERENCES_KEY, ""));
            websiteEditText.setText(prefs.getString(WEBSITE_PREFERENCES_KEY, ""));
        }
    }

    /**
     * Handle this event for when the user presses the button
     * This method was defined in the activity_card_edit.xml
     * file with the onClick attribute
     * <p/>
     * Commit the changes the user made
     *
     * @param view the View
     */
    public void isDone(View view) {

        //reset errors
        nameEditText.setError(null);
        businessEditText.setError(null);
        addressEditText.setError(null);
        cityEditText.setError(null);
        stateEditText.setError(null);
        zipCodeEditText.setError(null);
        numberEditText.setError(null);
        emailEditText.setError(null);
        websiteEditText.setError(null);

        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameEditText.setError("Please Enter a name");
            return; //exit out of the method
        }
        if (TextUtils.isEmpty(businessEditText.getText())) {
            businessEditText.setError("Please Enter a business, title, and/or job");
            return; //exit out of the method
        }
        if (TextUtils.isEmpty(addressEditText.getText())) {
            addressEditText.setError("Please Enter an address");
            return;
        }
        if (TextUtils.isEmpty(cityEditText.getText())) {
            cityEditText.setError("Please Enter a city");
            return;
        }
        if (TextUtils.isEmpty(stateEditText.getText())) {
            stateEditText.setError("Please Enter a state");
            return;
        }
        if (TextUtils.isEmpty(zipCodeEditText.getText())) {
            zipCodeEditText.setError("Please Enter a zip code");
            return;
        }
        if (TextUtils.isEmpty(numberEditText.getText())) {
            numberEditText.setError("Please Enter a number");
            return;
        }
        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailEditText.setError("Please Enter an email");
            return;
        }
        if (TextUtils.isEmpty(websiteEditText.getText())) {
            websiteEditText.setError("Please Enter a personal website or LinkedIn profile website");
            return;
        }

        //Save the users changes in SharedPreferences, which is a way of storing data locally
        //More info on SharedPreferences can be found in
        //the android docs
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (modeCreate) {
            // store the settings for which mode this activity will be in
            sp.edit().putBoolean(CARD_CREATED_KEY, false).apply(); // card has been initially created now
            sp.edit().putBoolean(CARD_EDITED_KEY, true).apply(); //make this activity in edit mode from now on
            modeCreate = false;
            modeEdit = true;
        }

        //retrieve the information from each of the EditTexts and store them
        sp.edit().putString(NAME_PREFERENCES_KEY, nameEditText.getText().toString())
                .putString(BUSINESS_PREFERENCES_KEY, businessEditText.getText().toString())
                .putString(ADDRESS_PREFERENCES_KEY, addressEditText.getText().toString())
                .putString(CITY_PREFERENCES_KEY, cityEditText.getText().toString())
                .putString(STATE_PREFERENCES_KEY, stateEditText.getText().toString())
                .putString(ZIPCODE_PREFERENCES_KEY, zipCodeEditText.getText().toString())
                .putString(NUMBER_PREFERENCES_KEY, numberEditText.getText().toString())
                .putString(EMAIL_PREFERENCES_KEY, emailEditText.getText().toString())
                .putString(WEBSITE_PREFERENCES_KEY, websiteEditText.getText().toString()).apply();

        setResult(RESULT_OK);//send back to the activity that started this activity an indication of success
        finish(); // returns to the previous CardSwap activity
    }
}
