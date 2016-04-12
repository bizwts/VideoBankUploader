package com.wings.videobankuploader.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.wings.videobankuploader.R;
import com.wings.videobankuploader.models.CustomGallery;

public class GalleryAdapter extends BaseAdapter {

	private Activity mContext;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
	ImageLoader imageLoader;

	private boolean isActionMultiplePick;
	private DisplayImageOptions options;

	public GalleryAdapter(Activity c, ImageLoader imageLoader) {

		mContext = c;
		this.imageLoader = imageLoader;
		// clearCache();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.no_media)
				.showImageForEmptyUri(R.drawable.no_media)
				.showImageOnFail(R.drawable.no_media).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true).build();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSeleted = selection;

		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public ArrayList<CustomGallery> getSelected() {
		ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<CustomGallery> files) {

		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(View v, int position) {

		if (data.get(position).isSeleted) {
			data.get(position).isSeleted = false;
		} else {
			data.get(position).isSeleted = true;
		}

		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
				.get(position).isSeleted);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		View v;
		if (convertView == null) {

			LayoutInflater inflater = mContext.getLayoutInflater();

			v = inflater.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) v.findViewById(R.id.imgQueue);

			holder.imgQueueMultiSelected = (ImageView) v
					.findViewById(R.id.imgQueueMultiSelected);

			holder.imgvideoIcon = (ImageView) v.findViewById(R.id.iv_videoicon);
			if (isActionMultiplePick) {
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			} else {
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
			}

			v.setTag(holder);

		} else {
			v = convertView;

			holder = (ViewHolder) v.getTag();
		}

		holder.imgQueue.setTag(position);
		// holder.imgQueue.setImageResource(R.drawable.avatarimage);
		try {

			boolean isvideo = data.get(position).isVideo;
			boolean isDoc = data.get(position).isDoc;

			if (isDoc) {
				holder.imgvideoIcon.setVisibility(View.INVISIBLE);
				String extension = data.get(position).sdcardPath;
				if (extension.endsWith(".pdf")) {
					holder.imgQueue.setImageResource(R.drawable.pdf_icon);
				} else if (extension.endsWith("txt")) {
					holder.imgQueue.setImageResource(R.drawable.doc_icon);
				} else {
					holder.imgQueue.setImageResource(R.drawable.doc_icon);
				}
				holder.imgQueue.setBackgroundColor(Color.parseColor("#0398f7"));
				Log.d("In Adapter Path", data.get(position).sdcardPath);
			}

			else if (isvideo) {

				holder.imgvideoIcon.setVisibility(View.VISIBLE);
				Log.d("videopath", "file://" + data.get(position).sdcardPath);

				holder.imgQueue.setImageBitmap(data.get(position).bmThumbnail);
				// holder.imgQueue.setImageResource(R.drawable.avatarimage);
			} else {
				holder.imgvideoIcon.setVisibility(View.INVISIBLE);

				imageLoader.displayImage("file://"
						+ data.get(position).sdcardPath, holder.imgQueue,
						options, new SimpleImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								holder.imgQueue
										.setImageResource(R.drawable.no_media);
								super.onLoadingStarted(imageUri, view);
							}
						});
			}

			if (isActionMultiplePick) {

				holder.imgQueueMultiSelected
						.setSelected(data.get(position).isSeleted);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return v;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		ImageView imgvideoIcon;
	}

	public void clearCache() {
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}
