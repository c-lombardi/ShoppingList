package com.example.christopher.shopping_list_app;

/**
 * Created by Christopher on 10/25/2015.
 */

import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;

public abstract class DelayedTextWatcher implements TextWatcher {

    private long delayTime;
    private WaitTask lastWaitTask;

    public DelayedTextWatcher(final long delayTime) {
        super();
        this.delayTime = delayTime;
    }

    @Override
    public void afterTextChanged(final Editable s) {
        synchronized (this) {
            if (lastWaitTask != null) {
                lastWaitTask.cancel(true);
            }
            lastWaitTask = new WaitTask();
            lastWaitTask.execute(s);
        }
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        // TODO Auto-generated method stub

    }

    public abstract void afterTextChangedDelayed(final Editable s);

    private class WaitTask extends AsyncTask<Editable, Void, Editable> {

        @Override
        protected Editable doInBackground(final Editable... params) {
            try {
                Thread.sleep(delayTime);
            } catch (final InterruptedException ignored) {
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(final Editable result) {
            super.onPostExecute(result);
            afterTextChangedDelayed(result);
        }
    }

}
