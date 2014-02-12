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

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * This class takes care of the fragment UI lifecycle and allows us to override them.
 * More info about the lifecycle and fragments can be found at
 * <a href="developer.android.com/guide/components/fragments.html">Fragments Guide</a>
 * and
 * <a href="http://developer.android.com/training/basics/fragments/index.html">Fragments Training</a>
 *
 * @author Vishal Disawar
 */
public class CardFragment extends Fragment {

    private boolean mCreated;
    private boolean mEdited;

    public CardFragment() {
        // required default constructor
    }

    /**
     * Our activity that adds this fragment will call this method itself to inflate the view of the
     * fragment created in fragment_card.xml
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    /*
     * Handle this event. We choose onStart because we want to modify the fragment UI with users
     * values before it shows in the activity.
     */
    @Override
    public void onStart() {
        super.onStart();
        // retrieve information stored via Shared Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEdited = prefs.getBoolean(CardEdit.CARD_EDITED_KEY, false);

        if (mEdited) {
            // reference to TextViews in activity_card_swap.xml
            final TextView sampleTitle = (TextView) getActivity().findViewById(R.id.sample_title);
            final TextView instructions = (TextView) getActivity().findViewById(R.id.instructions);
            // User doesn't need instructions anymore and the title
            sampleTitle.setVisibility(View.GONE);
            instructions.setVisibility(View.GONE);

            //get a reference to all the TextViews in fragment_card.xml
            final TextView personNameTView = (TextView) getActivity().findViewById(R.id.person_name);
            final TextView businessNameTView = (TextView) getActivity().findViewById(R.id.business_name);
            final TextView addressTView = (TextView) getActivity().findViewById(R.id.person_address);
            final TextView cityStateZipTView = (TextView) getActivity().findViewById(R.id.person_citystate);
            final TextView numberTView = (TextView) getActivity().findViewById(R.id.person_number);
            final TextView emailTView = (TextView) getActivity().findViewById(R.id.person_email);
            final TextView websiteTView = (TextView) getActivity().findViewById(R.id.person_website);

            //get the values from Shared preferences and replace default values with values
            //the user entered
            final String name = prefs.getString(CardEdit.NAME_PREFERENCES_KEY, "");
            final String business = prefs.getString(CardEdit.BUSINESS_PREFERENCES_KEY, "");
            final String address = prefs.getString(CardEdit.ADDRESS_PREFERENCES_KEY, "");
            final String city = prefs.getString(CardEdit.CITY_PREFERENCES_KEY, "");
            final String state = prefs.getString(CardEdit.STATE_PREFERENCES_KEY, "");
            final String zipCode = prefs.getString(CardEdit.ZIPCODE_PREFERENCES_KEY, "");
            final String number = prefs.getString(CardEdit.NUMBER_PREFERENCES_KEY, "");
            final String email = prefs.getString(CardEdit.EMAIL_PREFERENCES_KEY, "");
            final String website = prefs.getString(CardEdit.WEBSITE_PREFERENCES_KEY, "");
            final String cityStateZip = city + ", " + state + " " + zipCode;

            personNameTView.setText(name);
            businessNameTView.setText(business);
            addressTView.setText(address);
            cityStateZipTView.setText(cityStateZip);
            numberTView.setText(number);
            emailTView.setText(email);
            websiteTView.setText(website);
        }
    }
}
