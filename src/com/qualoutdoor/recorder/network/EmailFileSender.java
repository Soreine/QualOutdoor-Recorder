package com.qualoutdoor.recorder.network;

import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Class enabling file sending using email
 * */

public class EmailFileSender {

    public static void sendFileByEmail(Context context, String dest,
            FileToUpload file) {
        // email verifying pattern
        final Pattern rfc2822 = Pattern
                .compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
        // checking given email format
        if (!rfc2822.matcher(dest).matches()) {
            Toast toast = Toast.makeText(context, "invalid email address",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            // if format is right : preparing mail intent
            Intent email = new Intent(Intent.ACTION_SEND);
            // setting destination
            email.putExtra(Intent.EXTRA_EMAIL, new String[] {
                dest
            });
            // setting subject
            email.putExtra(Intent.EXTRA_SUBJECT, "my csv file");

            // reading file content into a string
            String stats;
            java.util.Scanner s = new java.util.Scanner(file.getContent())
                    .useDelimiter("\\A");
            if (s.hasNext()) {
                stats = s.next();
            } else {
                stats = "";

            }
            // setting mail content with readed string
            email.putExtra(Intent.EXTRA_TEXT,
                    "here are my csv measures : \r\n \r\n \r\n " + stats);
            // setting mail type
            email.setType("message/rfc822");
            // launching intent
            context.startActivity(Intent.createChooser(email,
                    "Choose an Email client :"));

        }
    }

}
