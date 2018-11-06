package com.snindustries.project.udacity.popularmovies.ui.details;


import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.snindustries.project.udacity.popularmovies.R;
import com.snindustries.project.udacity.popularmovies.repository.webservice.ImdbClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Images are loaded using Picasso library.
 *
 * @author Shaaz Noormohammad
 * (c) 2018
 */
public class PicassoDatabindingAdapter {

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            Picasso.get()
                    .load(R.drawable.ic_broken_image_black_24dp)
                    .into(view);
            return;
        }
        imageUrl = ImdbClient.get().getPosterURL(imageUrl);
        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(view, new Callback() {
                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onSuccess() {

                            }
                        }
                );
    }

}
