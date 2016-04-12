package com.wings.videobankuploader.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.wings.videobankuploader.R;
import com.wings.videobankuploader.database_helper.DataBase_Helper;
import com.wings.videobankuploader.globals.Constant;
import com.wings.videobankuploader.models.LocalBinModel;

public class DownloadService extends IntentService {

	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_ERROR = 2;
	public static final int STATUS_SOMEFILES = 3;

	private static final String TAG = "DownloadService";
	private final String mainFolder = "VideoBank Locals";
	ResultReceiver receiver;
	private Bundle bundle;
	private LocalBinModel model_vboFolder;
	private DataBase_Helper dbh;
	private SharedPreferences sharedpreferences;
	private String UserName;
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	int id = 11111;

	public DownloadService() {
		super(DownloadService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "DownloadService Started!");
		dbh = new DataBase_Helper(this);

		sharedpreferences = this.getSharedPreferences(Constant.PrefName, 0);
		UserName = sharedpreferences.getString("UserID", "");

		receiver = intent.getParcelableExtra("receiver");
		String VMRConnectHostname = intent.getStringExtra("VMRConnectHostname");
		String[] mStringArray = intent.getStringArrayExtra("mStringArray");
		String LocalBinName = intent.getStringExtra("LocalBinName");

		bundle = new Bundle();

		if (!TextUtils.isEmpty(VMRConnectHostname)) {
			receiver.send(STATUS_RUNNING, Bundle.EMPTY);

			try {
				mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mBuilder = new NotificationCompat.Builder(DownloadService.this);
				mBuilder.setContentText("Download in progress").setSmallIcon(
						R.drawable.ic_local_cloud);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new DownloadAssetToMobile(VMRConnectHostname, mStringArray,
							LocalBinName).executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
					Log.d("Indownload service", "if");
				} else {
					new DownloadAssetToMobile(VMRConnectHostname, mStringArray,
							LocalBinName).execute(null, null, null);
					Log.d("Indownload service", "else");

				}

			} catch (Exception e) {
				bundle.putString(Intent.EXTRA_TEXT, e.toString());
				receiver.send(STATUS_ERROR, bundle);
			}
		}
		Log.d(TAG, "DownloadService Stopping!");
		this.stopSelf();
	}

	class DownloadAssetToMobile extends AsyncTask<Void, Integer, Integer> {

		private String sOAPhost;
		private String[] dwnldimagepath;
		private FileInputStream is;
		private String LocalBinName;

		public DownloadAssetToMobile(String sOAPhost, String[] mStringArray,
				String LocalBinName) {
			// TODO Auto-generated constructor stub
			this.sOAPhost = sOAPhost;
			this.dwnldimagepath = mStringArray;
			this.LocalBinName = LocalBinName;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mBuilder.setProgress(100, 0, false);
			mNotifyManager.notify(id, mBuilder.build());
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			mBuilder.setProgress(100, values[0], false);
			mNotifyManager.notify(id, mBuilder.build());
			super.onProgressUpdate(values);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			long activityinsertid = dbh.VBU_Activities_Insert(UserName,
					"File Downloaded from VMR to Local", 13, 1,
					dateFormat.format(cal.getTime()));

			for (int i = 0; i < dwnldimagepath.length; i++) {
				int count;

				try {
					URL url = new URL(dwnldimagepath[i]);
					URLConnection conexion = url.openConnection();
					conexion.connect();
					String filename = dwnldimagepath[i]
							.substring(dwnldimagepath[i].lastIndexOf("/") + 1);
					mBuilder.setContentTitle(filename);
					String extension = dwnldimagepath[i]
							.substring(dwnldimagepath[i].lastIndexOf("."));
					int randomint = randInt(0, 1000000000);
					if (dwnldimagepath[i].endsWith(".pdf")
							|| dwnldimagepath[i].endsWith(".doc")
							|| dwnldimagepath[i].endsWith(".txt")
							|| dwnldimagepath[i].endsWith(".docx")
							|| dwnldimagepath[i].endsWith(".ppt")
							|| dwnldimagepath[i].endsWith(".pptx")
							|| dwnldimagepath[i].endsWith(".xls")
							|| dwnldimagepath[i].endsWith(".xlsx")) {
						String targetFileName = "DocumentVB" + randomint
								+ extension;
						int lenghtOfFile = conexion.getContentLength();
						String PATH = Environment.getExternalStorageDirectory()
								+ "/" + mainFolder + "/" + LocalBinName + "/";
						File folder = new File(PATH);
						if (!folder.exists()) {
							folder.mkdir();
						}
						InputStream input = new BufferedInputStream(
								url.openStream());
						OutputStream output = new FileOutputStream(PATH
								+ targetFileName);
						byte data[] = new byte[1024];
						long total = 0;
						while ((count = input.read(data)) != -1) {
							total += count;
							publishProgress((int) (total * 100 / lenghtOfFile));
							output.write(data, 0, count);
						}

						model_vboFolder = new LocalBinModel(LocalBinName);
						model_vboFolder = dbh
								.Folder_SelectByFolderName(LocalBinName);
						int vfid = model_vboFolder.getItem_id();
						int vfdoccount = model_vboFolder.getDocumentCount();
						dbh.VBU_Files_Insert(vfid, PATH + targetFileName, 0, 2);
						vfdoccount++;
						model_vboFolder.setDocumentCount(vfdoccount);
						model_vboFolder.setItem_id(vfid);
						dbh.Update_Doccount(model_vboFolder);

						dbh.VBU_ActivityDetails_Insert(Integer.parseInt(String
								.valueOf(activityinsertid)), filename,
								dwnldimagepath[i], PATH + targetFileName, 1, 0);

						output.flush();
						output.close();
						input.close();
					} else if (dwnldimagepath[i].endsWith(".jpg")
							|| dwnldimagepath[i].endsWith(".png")
							|| dwnldimagepath[i].endsWith(".jpeg")
							|| dwnldimagepath[i].endsWith(".gif")
							|| dwnldimagepath[i].endsWith(".bmp")
							|| dwnldimagepath[i].endsWith(".tif")
							|| dwnldimagepath[i].endsWith(".giff")) {
						String targetFileName = "Image" + randomint + extension;
						int lenghtOfFile = conexion.getContentLength();
						String PATH = Environment.getExternalStorageDirectory()
								+ "/" + mainFolder + "/" + LocalBinName + "/";
						File folder = new File(PATH);
						if (!folder.exists()) {
							folder.mkdir();
						}
						InputStream input = new BufferedInputStream(
								url.openStream());
						OutputStream output = new FileOutputStream(PATH
								+ targetFileName);
						byte data[] = new byte[1024];
						long total = 0;
						while ((count = input.read(data)) != -1) {
							total += count;
							publishProgress((int) (total * 100 / lenghtOfFile));
							output.write(data, 0, count);
						}

						model_vboFolder = new LocalBinModel(LocalBinName);
						model_vboFolder = dbh
								.Folder_SelectByFolderName(LocalBinName);
						int vfid = model_vboFolder.getItem_id();
						int vfimgcount = model_vboFolder.getImageCount();
						dbh.VBU_Files_Insert(vfid, PATH + targetFileName, 0, 0);
						vfimgcount++;
						model_vboFolder.setImageCount(vfimgcount);
						model_vboFolder.setItem_id(vfid);
						dbh.Update_Imagecount(model_vboFolder);

						dbh.VBU_ActivityDetails_Insert(Integer.parseInt(String
								.valueOf(activityinsertid)), filename,
								dwnldimagepath[i], PATH + targetFileName, 1, 0);

						output.flush();
						output.close();
						input.close();
					} else if (dwnldimagepath[i].endsWith(".m4v")
							|| dwnldimagepath[i].endsWith(".3gp")
							|| dwnldimagepath[i].endsWith(".mp4")
							|| dwnldimagepath[i].endsWith(".mov")
							|| dwnldimagepath[i].endsWith(".wmv")) {
						String targetFileName = "Video" + randomint + extension;
						int lenghtOfFile = conexion.getContentLength();
						String PATH = Environment.getExternalStorageDirectory()
								+ "/" + mainFolder + "/" + LocalBinName + "/";
						File folder = new File(PATH);
						if (!folder.exists()) {
							folder.mkdir();
						}
						InputStream input = new BufferedInputStream(
								url.openStream());
						OutputStream output = new FileOutputStream(PATH
								+ targetFileName);
						byte data[] = new byte[1024];
						long total = 0;
						while ((count = input.read(data)) != -1) {
							total += count;
							publishProgress((int) (total * 100 / lenghtOfFile));
							output.write(data, 0, count);
						}

						model_vboFolder = new LocalBinModel(LocalBinName);
						model_vboFolder = dbh
								.Folder_SelectByFolderName(LocalBinName);
						int vfid = model_vboFolder.getItem_id();
						int vfvideocount = model_vboFolder.getVideoCount();

						dbh.VBU_Files_Insert(vfid, PATH + targetFileName, 0, 1);
						vfvideocount++;
						model_vboFolder.setVideoCount(vfvideocount);
						model_vboFolder.setItem_id(vfid);
						dbh.Update_Videocount(model_vboFolder);

						dbh.VBU_ActivityDetails_Insert(Integer.parseInt(String
								.valueOf(activityinsertid)), filename,
								dwnldimagepath[i], PATH + targetFileName, 1, 0);

						output.flush();
						output.close();
						input.close();
					} else {
						receiver.send(STATUS_SOMEFILES, bundle);
					}

				} catch (Exception e) {
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mBuilder.setContentText("Download complete");
			// Removes the progress bar
			mBuilder.setProgress(0, 0, false);
			mNotifyManager.notify(id, mBuilder.build());
			mNotifyManager.cancel(id);
			receiver.send(STATUS_FINISHED, bundle);
		}
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
