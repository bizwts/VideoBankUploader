package com.wings.videobankuploader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lcsky.SVProgressHUD;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.wings.videobankuploader.adapter.GalleryAdapter;
import com.wings.videobankuploader.database_helper.DataBase_Helper;
import com.wings.videobankuploader.globals.Action;
import com.wings.videobankuploader.globals.Constant;
import com.wings.videobankuploader.globals.GlobalMethods;
import com.wings.videobankuploader.models.CustomGallery;

public class CustomGalleryActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;
	ImageView imgNoMedia;
	TextView tv_done;
	private ImageView iv_galleryback;
	String action;
	private ImageLoader imageLoader;
	private DataBase_Helper dbh;
	private SharedPreferences sharedpreferences;
	int count = 0;
	String imagesorvideo = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery);

		action = getIntent().getAction();
		if (action == null) {
			finish();
		}
		imagesorvideo = getIntent().getExtras().getString("imageandvideo");
		initImageLoader();
		init();
	}

	private void initImageLoader() {
		try {
			String CACHE_DIR = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/.temp_tmp";
			new File(CACHE_DIR).mkdirs();

			File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
					CACHE_DIR);

			DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true).cacheOnDisc(true)
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565).build();
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
					getBaseContext())
					.defaultDisplayImageOptions(defaultOptions)
					.discCache(new UnlimitedDiscCache(cacheDir))
					.memoryCache(new WeakMemoryCache());

			ImageLoaderConfiguration config = builder.build();
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);
		} catch (Exception e) {

		}
	}

	private void init() {

		dbh = new DataBase_Helper(this.getApplicationContext());
		sharedpreferences = CustomGalleryActivity.this.getSharedPreferences(
				Constant.PrefName, 0);

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		iv_galleryback = (ImageView) findViewById(R.id.iv_galleryback);
		tv_done = (TextView) findViewById(R.id.tv_done);
		gridGallery.setScrollingCacheEnabled(true);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(CustomGalleryActivity.this, imageLoader);
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
				true, true);

		gridGallery.setOnScrollListener(listener);

		if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {

			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.setMultiplePick(true);

		} else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {

			gridGallery.setOnItemClickListener(mItemSingleClickListener);
			adapter.setMultiplePick(false);

		}

		iv_galleryback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});

		gridGallery.setAdapter(adapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

		tv_done.setOnClickListener(mOkClickListener);

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						getValue();
					}
				});
				Looper.loop();
			};

		}.start();

	}

	private void checkImageStatus() {
		if (adapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<CustomGallery> selected = adapter.getSelected();

			String[] allPath = new String[selected.size()];
			boolean[] isvideo = new boolean[selected.size()];

			for (int i = 0; i < allPath.length; i++) {
				allPath[i] = selected.get(i).sdcardPath;
				isvideo[i] = selected.get(i).isVideo;
			}

			Intent data = new Intent().putExtra("all_path", allPath);
			data.putExtra("isvideo", isvideo);
			setResult(RESULT_OK, data);
			finish();

		}
	};
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			adapter.changeSelection(v, position);

		}
	};

	AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			CustomGallery item = adapter.getItem(position);
			Intent data = new Intent().putExtra("single_path", item.sdcardPath);
			setResult(RESULT_OK, data);
			finish();
		}
	};

	private class MyTask extends
			AsyncTask<Void, Void, ArrayList<CustomGallery>> {

		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			SVProgressHUD.showInView(CustomGalleryActivity.this,
					"Fetching Media...", true);
		}

		@Override
		protected ArrayList<CustomGallery> doInBackground(Void... params) {
			// do stuff and return the value you want

			if (!imagesorvideo.equals("")) {
				if (imagesorvideo.equals("yes")) {
					try {

						final String[] columns = {
								MediaStore.Images.Media.DATA,
								MediaStore.Images.Media._ID };
						final String orderBy = MediaStore.Images.Media._ID;

						Cursor imagecursor = managedQuery(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								columns, null, null, orderBy);

						if (imagecursor != null && imagecursor.getCount() > 0) {

							while (imagecursor.moveToNext()) {
								CustomGallery item = new CustomGallery();

								int dataColumnIndex = imagecursor
										.getColumnIndex(MediaStore.Images.Media.DATA);

								item.isVideo = false;
								item.isDoc = false;
								item.sdcardPath = imagecursor
										.getString(dataColumnIndex);
								galleryList.add(item);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					// Fetch Video from the SD card

					try {
						final String[] columns = { MediaStore.Video.Media.DATA,
								MediaStore.Video.Media._ID };
						final String orderBy = MediaStore.Video.Media._ID;

						Cursor imagecursor = managedQuery(
								MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
								columns, null, null, orderBy);

						if (imagecursor != null && imagecursor.getCount() > 0) {

							while (imagecursor.moveToNext()) {
								CustomGallery item = new CustomGallery();

								int dataColumnIndex = imagecursor
										.getColumnIndex(MediaStore.Video.Media.DATA);

								item.isVideo = true;
								item.isDoc = false;
								Log.e("isvideo", item.isVideo + "");
								item.sdcardPath = imagecursor
										.getString(dataColumnIndex);

								if (sharedpreferences.getBoolean(
										"firsttime_video_thumb", true)) {

									Log.e("FirstTime", "FirstTime Loop");

									Log.e("sdcardPath", item.sdcardPath + "");
									item.bmThumbnail = ThumbnailUtils
											.createVideoThumbnail(
													item.sdcardPath,
													Thumbnails.MICRO_KIND);

									dbh.addVideoThumb(
											"ThumbVideo" + count,
											GlobalMethods
													.ConvertBitmaptoByte(item.bmThumbnail));
									count++;
								} else {

									Log.e("SecondTime", "SecondTime Loop");

									item.bmThumbnail = GlobalMethods
											.ConvertBytetoBitmap(dbh
													.Select_ThumbImage("ThumbVideo"
															+ count));
									count++;
								}

								galleryList.add(item);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					Collections.reverse(galleryList);
					sharedpreferences.edit()
							.putBoolean("firsttime_video_thumb", false)
							.commit();

				}

				else {
					// fetch The other type of the document
					ContentResolver cr = getContentResolver();
					Uri uri = MediaStore.Files.getContentUri("external");
					String[] projection = { MediaStore.MediaColumns.DATA,
							MediaStore.MediaColumns.MIME_TYPE };
					String sortOrder = null; // unordered

					String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE
							+ "=? OR "
							+ MediaStore.Files.FileColumns.MIME_TYPE
							+ "=? OR "
							+ MediaStore.Files.FileColumns.MIME_TYPE
							+ "=? ";

					String[] selectionArgsPdf = new String[3];

					selectionArgsPdf[0] = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension("txt");

					selectionArgsPdf[1] = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension("pdf");

					selectionArgsPdf[2] = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension("doc");
					Cursor allPdfFiles = cr.query(uri, projection,
							selectionMimeType, selectionArgsPdf, sortOrder);

					if (allPdfFiles.moveToFirst()) {
						do {

							String data = allPdfFiles.getString(allPdfFiles
									.getColumnIndex("_data"));

							CustomGallery item = new CustomGallery();

							item.isVideo = false;
							item.isDoc = true;
							item.sdcardPath = data;
							galleryList.add(item);
							Log.d("DAta", data);

						} while (allPdfFiles.moveToNext());
					}
					allPdfFiles.close();

					Collections.reverse(galleryList);
				}
			}
			return galleryList;
		}

		@Override
		protected void onPostExecute(ArrayList<CustomGallery> result) {
			// Call activity method with results
			SVProgressHUD.dismiss(CustomGalleryActivity.this);
			processValue(result);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		SVProgressHUD.dismiss(CustomGalleryActivity.this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adapter.isEmpty()) {
			SVProgressHUD.showInView(CustomGalleryActivity.this,
					"Fetching Media...", true);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SVProgressHUD.dismiss(CustomGalleryActivity.this);
	}

	private void getValue() {
		new MyTask().execute();
	}

	private void processValue(ArrayList<CustomGallery> myValue) {
		// handle value
		// Update GUI, show toast, etc..
		adapter.addAll(myValue);
		checkImageStatus();
	}

}
