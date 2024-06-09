/*
 *  Copyright (c) 2024 Haraldo Albergaria - All Rights Reserved
 *
 *  Project       : ShutterNotes
 *  Developer     : Haraldo Albergaria
 *
 *  File          : AboutActivity.java
 *  Last modified : 6/7/24, 5:59 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.shutternotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.shutternotes.fragments.dialogs.MaterialIconsDialogFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.PrivacyPolicyDialogFragment;
import com.apps.mohb.shutternotes.fragments.dialogs.TermsOfUseDialogFragment;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // displays app version number
        TextView version = findViewById(R.id.textAppVersion);
        String versionText = getString(R.string.version_name) + Constants.SPACE + getString(R.string.version_number);
        version.setText(versionText);
    }


    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_about, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        DialogFragment dialog;

        switch (id) {

//            // Feedback
//            case R.id.action_feedback:
//                String[] feedback_address = new String[Constants.QUESTION_ARRAY_SIZE];
//                feedback_address[Constants.LIST_HEAD] = getString(R.string.info_feedback_email);
//                composeEmail(feedback_address, getString(R.string.action_feedback)
//                        + Constants.SPACE + getString(R.string.action_about_application)
//                        + Constants.SPACE + getString(R.string.info_app_name));
//                break;
//
//            // Bug report
//            case R.id.action_bug_report:
//                String[] bug_address = new String[Constants.QUESTION_ARRAY_SIZE];
//                bug_address[Constants.LIST_HEAD] = getString(R.string.info_bug_email);
//                composeEmail(bug_address, getString(R.string.action_bug_report)
//                        + Constants.SPACE + getString(R.string.info_app_name));
//                break;

            // Terms of use
            case R.id.action_terms_of_use:
                dialog = new TermsOfUseDialogFragment();
                dialog.show(getSupportFragmentManager(), "TermsOfUseDialogFragment");
                break;

            // Privacy policy
            case R.id.action_privacy_policy:
                dialog = new PrivacyPolicyDialogFragment();
                dialog.show(getSupportFragmentManager(), "PrivacyPolicyDialogFragment");
                break;

            // Icons attribution
            case R.id.action_material_icons:
                dialog = new MaterialIconsDialogFragment();
                dialog.show(getSupportFragmentManager(), "MaterialIconsDialogFragment");
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    // Compose a e-mail to send a question
    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
