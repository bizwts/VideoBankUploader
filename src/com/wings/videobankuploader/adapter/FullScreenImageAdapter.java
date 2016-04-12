package com.wings.videobankuploader.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.wings.videobankuploader.R;
import com.wings.videobankuploader.globals.TouchImageView;

public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<String> _imagePaths;
	private LayoutInflater inflater;
	private DisplayImageOptions options;

	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<String> imagePaths) {
		this._activity = activity;
		this._imagePaths = imagePaths;
	}

	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		TouchImageView imgDisplay;
		final ProgressBar progress_doc_image;

		ImageLoader imageLoader = ImageLoader.getInstance();

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.viewdoc_fullscreen_image,
				container, false);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.no_media)
				.showImageOnLoading(R.drawable.no_media)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.showImageOnFail(R.drawable.ic_launcher)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		imgDisplay = (TouchImageView) viewLayout
				.findViewById(R.id.imgDoc_Display);
		progress_doc_image = (ProgressBar) viewLayout
				.findViewById(R.id.progress_doc_image);

		Log.e("_imagePaths", _imagePaths.get(position));

		imageLoader.displayImage(_imagePaths.get(position), imgDisplay,
				options, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						progress_doc_image.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason failReason) {
						progress_doc_image.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						progress_doc_image.setVisibility(View.GONE);

					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}
				});

		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}
}
