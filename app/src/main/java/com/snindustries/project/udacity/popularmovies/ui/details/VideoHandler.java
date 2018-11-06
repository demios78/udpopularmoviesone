package com.snindustries.project.udacity.popularmovies.ui.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.snindustries.project.udacity.popularmovies.repository.webservice.Video;

/**
 * @author Shaaz Noormohammad
 * (c) 11/5/18
 */
public class VideoHandler {
    public void onClick(View view, Video video) {
        if (video != null && !TextUtils.isEmpty(video.key) && !TextUtils.isEmpty(video.site) && video.site.equalsIgnoreCase("YouTube")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.key));
            try {
                view.getContext().startActivity(intent);
            } catch (ActivityNotFoundException exception) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.key));
                view.getContext().startActivity(intent);
            }
        }
    }
}
