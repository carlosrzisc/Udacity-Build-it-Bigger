package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;

import com.builditbigger.jokes.backend.jokesApi.JokesApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by carlos on 7/30/15.
 */
public class JokeRetriever extends AsyncTask<Void, Void, String> {
    private JokesApi jokesService = null;
    private OnTaskCompleted listener;

    public JokeRetriever(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        if(jokesService == null) {  // Only do this once
            JokesApi.Builder builder = new JokesApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            jokesService = builder.build();
        }
        try {
            return jokesService.sayHi().execute().getData();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onTaskCompleted(result);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }

}