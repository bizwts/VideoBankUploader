package com.wings.videobankuploader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;
import org.lcsky.SVProgressHUD;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.wings.videobankuploader.database_helper.DataBase_Helper;
import com.wings.videobankuploader.globals.Constant;
import com.wings.videobankuploader.globals.GlobalMethods;
import com.wings.videobankuploader.models.ISCIBinModel;
import com.wings.videobankuploader.services.DownloadResultReceiver;
import com.wings.videobankuploader.services.DownloadService;
import com.wings.videobankuploader.services.UploadService;

public class ISCIBinActivity extends Activity implements
		DownloadResultReceiver.Receiver {

	private String ISCIName;
	private SharedPreferences sharedpreferences;
	private ImageView iv_iscibinfolderback;
	private StickyGridHeadersGridView iscibinfolder_galley_gridview;
	private LinearLayout iscibingallery_empty_emoji;
	private TextView tv_iscibinfolder;
	public ArrayList<ISCIBinModel> ISCIBinModelList = new ArrayList<ISCIBinModel>();
	public ArrayList<ISCIBinModel> DocISCIBinModelList = new ArrayList<ISCIBinModel>();
	public ArrayList<ISCIBinModel> ImageISCIBinModelList = new ArrayList<ISCIBinModel>();
	public ArrayList<ISCIBinModel> VideoISCIBinModelList = new ArrayList<ISCIBinModel>();
	int doccount = 0;
	int imagecount = 0;
	int videocount = 0;
	private TextView iscibin_select_action_gallery;
	private StickyISCIBinGalleryAdapter StickygalleryAdapter;
	private TextView move_iscibingallery_action;
	private TextView copy_iscibingallery_action;
	private TextView remove_iscibingallery_action;
	private TextView download_iscibingallery_action;
	private boolean selectOptionorCancel = true;
	private LinearLayout ll_iscibin_editoptions;
	private String[] imagevideopath;
	private String[] imagevideothumbnailpath;
	private String[] documentpath;
	private Boolean imgflag0[];
	HashMap<Integer, String> crntSelectedPaths = new HashMap<Integer, String>();
	HashMap<Integer, String> crntSelectedFileNames = new HashMap<Integer, String>();
	HashMap<Integer, Integer> crntSelectedFormatIDKeys = new HashMap<Integer, Integer>();
	ArrayList<String> selectGalleryActionsList = new ArrayList<String>();
	ArrayList<String> selectedFileNames = new ArrayList<String>();
	ArrayList<Integer> selectFormatIDKeys = new ArrayList<Integer>();
	private String VMRConnectHostname;
	private PopupWindow pwindo;
	private Button cancel_remove_file;
	private Button remove_file;
	private TextView et_removetext;
	private DownloadResultReceiver mReceiver;
	private String[] mStringArray;
	private DataBase_Helper dbh;
	private String UserName;
	private SharedPreferences sharedpreferencesPrefPortConnect;
	private StickyListHeadersListView iscibinfolder_galley_listview;
	private StickyISCIBinGalleryListAdapter StickyListgalleryAdapter;
	private FloatingActionMenu iscibin_list_grid_toggleMenu;
	private FloatingActionButton iscibin_list_toggle;
	private FloatingActionButton iscibin_grid_toggle;
	private FloatingActionButton iscibin_local_bin_share;
	private Boolean DisplayGridorList;
	private String ProductName;
	private String MoveorCopyFlag = "move";
	private FloatingActionButton iscibin_power_search;
	private String ClientName;
	private Button btn_refresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iscibin);

		sharedpreferences = this.getSharedPreferences(Constant.PrefName, 0);
		sharedpreferencesPrefPortConnect = getApplicationContext()
				.getSharedPreferences(Constant.PrefPortConnect, 0);

		DisplayGridorList = sharedpreferences.getBoolean("PrefISCIListorGrid",
				true);

		ISCIName = getIntent().getExtras().getString("ISCIName");
		ProductName = getIntent().getExtras().getString("ProductName");
		ClientName = getIntent().getExtras().getString("ClientName");

		UserName = sharedpreferences.getString("UserID", "");

		dbh = new DataBase_Helper(this);

		iscibin_list_grid_toggleMenu = (FloatingActionMenu) findViewById(R.id.iscibin_list_grid_toggleMenu);
		iscibin_list_toggle = (FloatingActionButton) findViewById(R.id.iscibin_list_toggle);
		iscibin_grid_toggle = (FloatingActionButton) findViewById(R.id.iscibin_grid_toggle);
		iscibin_local_bin_share = (FloatingActionButton) findViewById(R.id.iscibin_local_bin_share);
		iscibinfolder_galley_gridview = (StickyGridHeadersGridView) findViewById(R.id.iscibinfolder_galley_gridview);
		iscibinfolder_galley_listview = (StickyListHeadersListView) findViewById(R.id.iscibinfolder_galley_listview);
		iscibingallery_empty_emoji = (LinearLayout) findViewById(R.id.iscibingallery_empty_emoji);
		iscibin_power_search = (FloatingActionButton) findViewById(R.id.iscibin_power_search);

		tv_iscibinfolder = (TextView) findViewById(R.id.tv_iscibinfolder);
		tv_iscibinfolder.setText(ISCIName);

		iv_iscibinfolderback = (ImageView) findViewById(R.id.iv_iscibinfolderback);
		iv_iscibinfolderback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stubGetAllReviewProducts
				finish();
			}
		});

		btn_refresh = (Button) findViewById(R.id.btn_refresh);

		btn_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ISCIBinModelList.clear();
				ImageISCIBinModelList.clear();
				VideoISCIBinModelList.clear();
				DocISCIBinModelList.clear();
				if (sharedpreferencesPrefPortConnect
						.contains("VMRConnectHostname")) {
					VMRConnectHostname = sharedpreferencesPrefPortConnect
							.getString("VMRConnectHostname", "");
					if (VMRConnectHostname != null
							&& !VMRConnectHostname.equals("")) {
						new GetISCIClipsByLanguage(VMRConnectHostname).execute(
								null, null, null);
					} else {
						Toast.makeText(ISCIBinActivity.this,
								"Failed to connect..", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					Toast.makeText(ISCIBinActivity.this, "Failed to connect..",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		this.findViewById(android.R.id.content).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (iscibin_list_grid_toggleMenu.isOpened()) {
							iscibin_list_grid_toggleMenu.close(true);
						}
					}
				});

		if (sharedpreferencesPrefPortConnect.contains("VMRConnectHostname")) {
			VMRConnectHostname = sharedpreferencesPrefPortConnect.getString(
					"VMRConnectHostname", "");
			if (VMRConnectHostname != null && !VMRConnectHostname.equals("")) {
				new GetISCIClipsByLanguage(VMRConnectHostname).execute(null,
						null, null);
			} else {
				Toast.makeText(ISCIBinActivity.this, "Failed to connect..",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(ISCIBinActivity.this, "Failed to connect..",
					Toast.LENGTH_LONG).show();
		}

		ll_iscibin_editoptions = (LinearLayout) findViewById(R.id.ll_iscibin_editoptions);
		iscibin_select_action_gallery = (TextView) findViewById(R.id.iscibin_select_action_gallery);

		iscibin_select_action_gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (selectOptionorCancel == true) {
					ll_iscibin_editoptions.setVisibility(View.VISIBLE);
					iscibin_list_grid_toggleMenu.setVisibility(View.GONE);
					btn_refresh.setVisibility(View.GONE);
					if (DisplayGridorList == true) {
						StickygalleryAdapter = new StickyISCIBinGalleryAdapter(
								ISCIBinActivity.this, ISCIBinModelList,
								imagevideopath, documentpath,
								imagevideothumbnailpath, true);
						imgflag0 = new Boolean[ISCIBinModelList.size()];
						for (int i = 0; i < ISCIBinModelList.size(); i++) {
							imgflag0[i] = false;
						}

						iscibinfolder_galley_gridview
								.setAdapter(StickygalleryAdapter);
						iscibinfolder_galley_gridview
								.setEmptyView(iscibingallery_empty_emoji);
					} else {
						StickyListgalleryAdapter = new StickyISCIBinGalleryListAdapter(
								ISCIBinActivity.this, ISCIBinModelList,
								imagevideopath, documentpath,
								imagevideothumbnailpath, true);
						imgflag0 = new Boolean[ISCIBinModelList.size()];
						for (int i = 0; i < ISCIBinModelList.size(); i++) {
							imgflag0[i] = false;
						}
						iscibinfolder_galley_listview
								.setAdapter(StickyListgalleryAdapter);
						iscibinfolder_galley_listview
								.setEmptyView(iscibingallery_empty_emoji);
					}

					iscibin_select_action_gallery.setText("Cancel");
					selectOptionorCancel = false;
				} else {
					ll_iscibin_editoptions.setVisibility(View.GONE);
					iscibin_list_grid_toggleMenu.setVisibility(View.VISIBLE);
					btn_refresh.setVisibility(View.VISIBLE);

					if (DisplayGridorList == true) {
						StickygalleryAdapter = new StickyISCIBinGalleryAdapter(
								ISCIBinActivity.this, ISCIBinModelList,
								imagevideopath, documentpath,
								imagevideothumbnailpath, false);
						iscibinfolder_galley_gridview
								.setAdapter(StickygalleryAdapter);
						iscibinfolder_galley_gridview
								.setEmptyView(iscibingallery_empty_emoji);
					} else {
						StickyListgalleryAdapter = new StickyISCIBinGalleryListAdapter(
								ISCIBinActivity.this, ISCIBinModelList,
								imagevideopath, documentpath,
								imagevideothumbnailpath, false);
						iscibinfolder_galley_listview
								.setAdapter(StickyListgalleryAdapter);
						iscibinfolder_galley_listview
								.setEmptyView(iscibingallery_empty_emoji);
					}

					iscibin_select_action_gallery.setText("Select");
					selectOptionorCancel = true;
				}
			}
		});

		move_iscibingallery_action = (TextView) findViewById(R.id.move_iscibingallery_action);
		move_iscibingallery_action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectFormatIDKeys.clear();
				selectFormatIDKeys.addAll(crntSelectedFormatIDKeys.values());

				selectedFileNames.clear();
				selectedFileNames.addAll(crntSelectedFileNames.values());

				MoveorCopyFlag = "move";

				if (selectFormatIDKeys.size() > 0) {
					if (sharedpreferencesPrefPortConnect
							.contains("VMRConnectHostname")) {
						String VMRConnectHostname = sharedpreferencesPrefPortConnect
								.getString("VMRConnectHostname", "");
						if (VMRConnectHostname != null
								&& !VMRConnectHostname.equals("")) {

							Intent intent = new Intent(ISCIBinActivity.this,
									UploadtoISCIActivity.class);
							startActivityForResult(intent, 2);
						} else {
							Toast.makeText(ISCIBinActivity.this,
									"Please set the Port Connection",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(ISCIBinActivity.this,
								"Please set the Port Connection",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ISCIBinActivity.this,
							"Select atleast one file to Copy",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		copy_iscibingallery_action = (TextView) findViewById(R.id.copy_iscibingallery_action);
		copy_iscibingallery_action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectFormatIDKeys.clear();
				selectFormatIDKeys.addAll(crntSelectedFormatIDKeys.values());

				selectedFileNames.clear();
				selectedFileNames.addAll(crntSelectedFileNames.values());

				MoveorCopyFlag = "copy";

				if (selectFormatIDKeys.size() > 0) {
					if (sharedpreferencesPrefPortConnect
							.contains("VMRConnectHostname")) {
						String VMRConnectHostname = sharedpreferencesPrefPortConnect
								.getString("VMRConnectHostname", "");
						if (VMRConnectHostname != null
								&& !VMRConnectHostname.equals("")) {

							Intent intent = new Intent(ISCIBinActivity.this,
									UploadtoISCIActivity.class);
							startActivityForResult(intent, 2);
						} else {
							Toast.makeText(ISCIBinActivity.this,
									"Please set the Port Connection",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(ISCIBinActivity.this,
								"Please set the Port Connection",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ISCIBinActivity.this,
							"Select atleast one file to Copy",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		remove_iscibingallery_action = (TextView) findViewById(R.id.remove_iscibingallery_action);
		remove_iscibingallery_action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				selectFormatIDKeys.clear();
				selectFormatIDKeys.addAll(crntSelectedFormatIDKeys.values());

				selectedFileNames.clear();
				selectedFileNames.addAll(crntSelectedFileNames.values());

				if (selectedFileNames.size() > 0) {
					initiatePopupWindow(VMRConnectHostname, selectFormatIDKeys,
							selectedFileNames);
				} else {
					Toast.makeText(ISCIBinActivity.this,
							"Select atleast one file to Remove",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		download_iscibingallery_action = (TextView) findViewById(R.id.download_iscibingallery_action);
		download_iscibingallery_action
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectGalleryActionsList.clear();
						selectGalleryActionsList.addAll(crntSelectedPaths
								.values());

						mStringArray = new String[selectGalleryActionsList
								.size()];
						mStringArray = selectGalleryActionsList
								.toArray(mStringArray);

						if (mStringArray.length > 0) {
							Intent intent = new Intent(ISCIBinActivity.this,
									LocalBinDownloadActivity.class);
							intent.putExtra("upload", true);
							startActivityForResult(intent, 1);
						} else {
							Toast.makeText(ISCIBinActivity.this,
									"Select atleast one file to Download",
									Toast.LENGTH_LONG).show();
						}
					}
				});

		iscibin_local_bin_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (iscibin_list_grid_toggleMenu.isOpened()) {
					iscibin_list_grid_toggleMenu.close(true);
				}

				Intent intent = new Intent(ISCIBinActivity.this,
						LocalBinDownloadActivity.class);
				intent.putExtra("upload", false);
				intent.putExtra("ISCINametoupload", ISCIName);
				startActivity(intent);
			}
		});

		iscibin_power_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InitiatePowerSearchPopUp();
				if (iscibin_list_grid_toggleMenu.isOpened()) {
					iscibin_list_grid_toggleMenu.close(true);
				}
			}
		});

		iscibin_list_toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putBoolean("PrefISCIListorGrid", false);
				editor.commit();
				crntSelectedPaths.clear();
				crntSelectedFormatIDKeys.clear();
				crntSelectedFileNames.clear();
				DisplayGridorList = false;
				iscibinfolder_galley_gridview.setVisibility(View.GONE);
				iscibinfolder_galley_listview.setVisibility(View.VISIBLE);
				StickyListgalleryAdapter = new StickyISCIBinGalleryListAdapter(
						ISCIBinActivity.this, ISCIBinModelList, imagevideopath,
						documentpath, imagevideothumbnailpath, false);
				iscibinfolder_galley_listview
						.setAdapter(StickyListgalleryAdapter);
				iscibinfolder_galley_listview
						.setEmptyView(iscibingallery_empty_emoji);
				if (iscibin_list_grid_toggleMenu.isOpened()) {
					iscibin_list_grid_toggleMenu.close(true);
				}
			}
		});

		iscibin_grid_toggle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putBoolean("PrefISCIListorGrid", true);
				editor.commit();
				crntSelectedPaths.clear();
				crntSelectedFormatIDKeys.clear();
				crntSelectedFileNames.clear();
				DisplayGridorList = true;
				iscibinfolder_galley_gridview.setVisibility(View.VISIBLE);
				iscibinfolder_galley_listview.setVisibility(View.GONE);
				StickygalleryAdapter = new StickyISCIBinGalleryAdapter(
						ISCIBinActivity.this, ISCIBinModelList, imagevideopath,
						documentpath, imagevideothumbnailpath, false);
				iscibinfolder_galley_gridview.setAdapter(StickygalleryAdapter);
				iscibinfolder_galley_gridview
						.setEmptyView(iscibingallery_empty_emoji);
				if (iscibin_list_grid_toggleMenu.isOpened()) {
					iscibin_list_grid_toggleMenu.close(true);
				}
			}
		});
	}

	private void InitiatePowerSearchPopUp() {
		try {
			LayoutInflater inflater = (LayoutInflater) ISCIBinActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.power_search_popup,
					(ViewGroup) ISCIBinActivity.this
							.findViewById(R.id.ps_popup_element));
			final PopupWindow pwindops = new PopupWindow(layout,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			pwindops.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pwindops.setFocusable(true);
			pwindops.update();

			TextView iv_ps_popup_text1 = (TextView) layout
					.findViewById(R.id.iv_ps_popup_text1);
			iv_ps_popup_text1.setText("Tier 3 Search");
			ImageView iv_ps_popup_close = (ImageView) layout
					.findViewById(R.id.iv_ps_popup_close);
			final EditText et_ps_keyword = (EditText) layout
					.findViewById(R.id.et_ps_keyword);

			Button btn_popup_ps_search = (Button) layout
					.findViewById(R.id.btn_popup_ps_search);
			iv_ps_popup_close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pwindops.dismiss();
				}
			});

			btn_popup_ps_search.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (GlobalMethods.isEmpty(et_ps_keyword)) {
						Toast.makeText(ISCIBinActivity.this,
								"Please enter keyword to search..",
								Toast.LENGTH_LONG).show();
					} else {
						if (DisplayGridorList == true) {
							StickygalleryAdapter.getFilter().filter(
									et_ps_keyword.getText().toString());

						} else {
							StickyListgalleryAdapter.getFilter().filter(
									et_ps_keyword.getText().toString());
						}
						pwindops.dismiss();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && data != null) {
			String LocalBinName = data.getStringExtra("LocalBinName");
			Log.e("LocalBinName", LocalBinName);

			for (int i = 0; i < mStringArray.length; i++) {
				Log.e("mStringArray", mStringArray[i]);
			}

			mReceiver = new DownloadResultReceiver(new Handler());
			mReceiver.setReceiver(this);
			Intent intent = new Intent(Intent.ACTION_SYNC, null, this,
					DownloadService.class);

			intent.putExtra("VMRConnectHostname", VMRConnectHostname);
			intent.putExtra("receiver", mReceiver);
			intent.putExtra("mStringArray", mStringArray);
			intent.putExtra("LocalBinName", LocalBinName);

			startService(intent);
		} else if (requestCode == 2 && data != null) {
			String ISCIName = data.getStringExtra("ISCIName");
			String ProductName = data.getStringExtra("ProductName");

			String DestPath = ISCIName + "\\" + ProductName;

			if (MoveorCopyFlag == "move") {
				new MoveProductFiles(VMRConnectHostname, selectFormatIDKeys,
						selectedFileNames, DestPath).execute(null, null, null);
			} else if (MoveorCopyFlag == "copy") {
				new CopyProductFiles(VMRConnectHostname, selectFormatIDKeys,
						selectedFileNames, DestPath).execute(null, null, null);
			}
		} else {

		}
	}

	class CopyProductFiles extends
			AsyncTask<Void, ArrayList<String>, ArrayList<String>> {

		private String vMRConnectHostname;
		private ArrayList<Integer> crntSelectedFormatIDKeys;
		private ArrayList<String> selectedFileNames2;
		private String destPath;
		private Object resultSoap;
		private String resultSoapString;
		ArrayList<String> arlstResponse = new ArrayList<String>();

		public CopyProductFiles(String vMRConnectHostname,
				ArrayList<Integer> selectFormatIDKeys2,
				ArrayList<String> selectedFileNames2, String destPath) {
			// TODO Auto-generated constructor stub
			this.vMRConnectHostname = vMRConnectHostname;
			this.crntSelectedFormatIDKeys = selectFormatIDKeys2;
			this.selectedFileNames2 = selectedFileNames2;
			this.destPath = destPath;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(ISCIBinActivity.this,
					"Copying Selected Files..", true);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			long activityinsertid = dbh.VBU_Activities_Insert(UserName,
					"File Copied", 7, 1, dateFormat.format(cal.getTime()));
			for (int i = 0; i < crntSelectedFormatIDKeys.size(); i++) {
				String[] attributeslist2 = { "xpathSrc", "xpathDest" };
				String[] attributeslist_value = {
						ProductName + "\\" + ISCIName + "\\"
								+ crntSelectedFormatIDKeys.get(i), destPath };
				dbh.VBU_ActivityDetails_Insert(
						Integer.parseInt(String.valueOf(activityinsertid)),
						selectedFileNames2.get(i),
						ProductName + "\\" + ISCIName + "\\"
								+ crntSelectedFormatIDKeys.get(i), destPath, 0,
						1);
				Object resultDeleteProduct = GlobalMethods
						.SOAPMethodPrimitivewithVariables("CopyFiles",
								attributeslist2, attributeslist_value,
								vMRConnectHostname);
				if (resultDeleteProduct != null) {
					resultSoap = resultDeleteProduct;
					resultSoapString = resultDeleteProduct.toString();
				} else {
					resultSoapString = "Error! Not found!";
				}
				arlstResponse.add(resultSoapString);
			}
			return arlstResponse;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(ISCIBinActivity.this);

			for (int i = 0; i < result.size(); i++) {
				Log.e("result response", result.get(i));
			}

			Intent i = new Intent(ISCIBinActivity.this, ISCIBinActivity.class);
			i.putExtra("ISCIName", ISCIName);
			startActivity(i);
			finish();
		}
	}

	class MoveProductFiles extends
			AsyncTask<Void, ArrayList<String>, ArrayList<String>> {

		private String vMRConnectHostname;
		private ArrayList<Integer> crntSelectedFormatIDKeys;
		private ArrayList<String> selectedFileNames2;
		private String destPath;
		private Object resultSoap;
		private String resultSoapString;
		ArrayList<String> arlstResponse = new ArrayList<String>();

		public MoveProductFiles(String vMRConnectHostname,
				ArrayList<Integer> selectFormatIDKeys2,
				ArrayList<String> selectedFileNames2, String destPath) {
			// TODO Auto-generated constructor stub
			this.vMRConnectHostname = vMRConnectHostname;
			this.crntSelectedFormatIDKeys = selectFormatIDKeys2;
			this.selectedFileNames2 = selectedFileNames2;
			this.destPath = destPath;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(ISCIBinActivity.this,
					"Moving Selected Files..", true);
			super.onPreExecute();
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			long activityinsertid = dbh.VBU_Activities_Insert(UserName,
					"File Moved", 9, 1, dateFormat.format(cal.getTime()));
			for (int i = 0; i < crntSelectedFormatIDKeys.size(); i++) {

				String[] attributeslist2 = { "xpathSrc", "xpathDest" };
				String[] attributeslist_value = {
						ProductName + "\\" + ISCIName + "\\"
								+ crntSelectedFormatIDKeys.get(i), destPath };
				Log.e("srcpath Move", ProductName + "\\" + ISCIName + "\\"
						+ crntSelectedFormatIDKeys.get(i));
				Log.e("destPath Move", destPath);
				dbh.VBU_ActivityDetails_Insert(
						Integer.parseInt(String.valueOf(activityinsertid)),
						selectedFileNames2.get(i),
						ProductName + "\\" + ISCIName + "\\"
								+ crntSelectedFormatIDKeys.get(i), destPath, 0,
						1);
				Object resultDeleteProduct = GlobalMethods
						.SOAPMethodPrimitivewithVariables("MoveFiles",
								attributeslist2, attributeslist_value,
								vMRConnectHostname);
				if (resultDeleteProduct != null) {
					resultSoap = resultDeleteProduct;
					resultSoapString = resultDeleteProduct.toString();
				} else {
					resultSoapString = "Error! Not found!";
				}
				arlstResponse.add(resultSoapString);
			}
			return arlstResponse;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(ISCIBinActivity.this);

			for (int i = 0; i < result.size(); i++) {
				Log.e("result response", result.get(i));
			}

			Intent i = new Intent(ISCIBinActivity.this, ISCIBinActivity.class);
			i.putExtra("ISCIName", ISCIName);
			startActivity(i);
			finish();
		}
	}

	class DeleteProductFilesbyFormatIDKey extends AsyncTask<Void, Void, Void> {

		SoapObject resultSoap;

		private String vMRConnectHostname;
		private ArrayList<Integer> crntSelectedFormatIDKeys;
		private ArrayList<String> selectedFileNames2;

		public DeleteProductFilesbyFormatIDKey(String vMRConnectHostname,
				ArrayList<Integer> selectFormatIDKeys2,
				ArrayList<String> selectedFileNames2) {
			// TODO Auto-generated constructor stub
			this.vMRConnectHostname = vMRConnectHostname;
			this.crntSelectedFormatIDKeys = selectFormatIDKeys2;
			this.selectedFileNames2 = selectedFileNames2;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(ISCIBinActivity.this,
					"Removing Selected Files..", true);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			long activityinsertid = dbh.VBU_Activities_Insert(UserName,
					"File Removed", 11, 1, dateFormat.format(cal.getTime()));
			for (int i = 0; i < crntSelectedFormatIDKeys.size(); i++) {
				String[] attributeslist2 = { "formatIDKey" };
				Integer[] attributeslist_value = { crntSelectedFormatIDKeys
						.get(i) };

				dbh.VBU_ActivityDetails_Insert(
						Integer.parseInt(String.valueOf(activityinsertid)),
						selectedFileNames2.get(i), "", "", 0, 1);

				SoapObject resultGetISCIClipsByLanguage = GlobalMethods
						.RecordSOAPMethod("DeleteProductFilesbyFormatIDKey",
								attributeslist2, attributeslist_value,
								vMRConnectHostname);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(ISCIBinActivity.this);

			Intent i = new Intent(ISCIBinActivity.this, ISCIBinActivity.class);
			i.putExtra("ISCIName", ISCIName);
			startActivity(i);
			finish();
		}
	}

	private void initiatePopupWindow(final String VMRConnectHostname,
			final ArrayList<Integer> selectFormatIDKeys2,
			final ArrayList<String> selectedFileNames2) {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.removefile_popup,
					(ViewGroup) this.findViewById(R.id.remove_popup_element));
			pwindo = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

			et_removetext = (TextView) layout.findViewById(R.id.et_removetext);
			et_removetext
					.setText("Do you really want to remove these files from your VMR clip bin?");
			cancel_remove_file = (Button) layout
					.findViewById(R.id.cancel_remove_file);
			remove_file = (Button) layout.findViewById(R.id.remove_file);
			cancel_remove_file.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pwindo.dismiss();
				}
			});

			remove_file.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new DeleteProductFilesbyFormatIDKey(VMRConnectHostname,
							selectFormatIDKeys2, selectedFileNames2).execute(
							null, null, null);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class GetISCIClipsByLanguage extends AsyncTask<String, String, String> {

		SoapObject resultSoap;
		private String sOAPhost;

		public GetISCIClipsByLanguage(String sOAPhost) {
			// TODO Auto-generated constructor stub
			this.sOAPhost = sOAPhost;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(ISCIBinActivity.this,
					"Fetching ISCI Bin Media..", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			String[] attributeslist2 = { "isci", "languageId" };
			String[] attributeslist_value = { ISCIName, "0" };

			SoapObject resultGetISCIClipsByLanguage = GlobalMethods
					.MediaSOAPMethod("GetISCIClipsByLanguage", attributeslist2,
							attributeslist_value, sOAPhost);
			resultSoap = resultGetISCIClipsByLanguage;
			String result = resultGetISCIClipsByLanguage.toString();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(ISCIBinActivity.this);
			Log.e("result", result);

			for (int i = 0; i < resultSoap.getPropertyCount(); i++) {
				ISCIBinModel documentrIsciBinModel = new ISCIBinModel();
				ISCIBinModel imagerIsciBinModel = new ISCIBinModel();
				ISCIBinModel videorIsciBinModel = new ISCIBinModel();
				Object property = resultSoap.getProperty(i);
				if (property instanceof SoapObject) {
					SoapObject product_list = (SoapObject) property;

					if (product_list.getProperty("FileType").toString()
							.equalsIgnoreCase("pdf")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("doc")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("txt")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("docx")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("ppt")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("pptx")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("xls")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("xlsx")) {
						documentrIsciBinModel.setDisplayFileName("DocumentVB"
								+ product_list.getProperty("FileName")
										.toString());
						doccount = doccount + 1;
						setValuesModel(documentrIsciBinModel, product_list);
						DocISCIBinModelList.add(documentrIsciBinModel);
					} else if (product_list.getProperty("FileType").toString()
							.equalsIgnoreCase("jpg")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("png")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("jpeg")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("gif")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("bmp")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("tif")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("giff")) {
						imagerIsciBinModel.setDisplayFileName("Image"
								+ product_list.getProperty("FileName")
										.toString());
						imagecount = imagecount + 1;
						setValuesModel(imagerIsciBinModel, product_list);
						ImageISCIBinModelList.add(imagerIsciBinModel);
					} else if (product_list.getProperty("FileType").toString()
							.equalsIgnoreCase("m4v")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("3gp")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("mp4")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("mov")
							|| product_list.getProperty("FileType").toString()
									.equalsIgnoreCase("wmv")) {
						videorIsciBinModel.setDisplayFileName("Video"
								+ product_list.getProperty("FileName")
										.toString());
						videocount = videocount + 1;
						setValuesModel(videorIsciBinModel, product_list);
						VideoISCIBinModelList.add(videorIsciBinModel);
					} else {
						// do nothing
					}
				}
			}

			ISCIBinModelList.addAll(ImageISCIBinModelList);
			ISCIBinModelList.addAll(VideoISCIBinModelList);
			ISCIBinModelList.addAll(DocISCIBinModelList);

			imagevideopath = new String[imagecount + videocount];
			imagevideothumbnailpath = new String[imagecount + videocount];
			documentpath = new String[doccount];
			int imgK = 0, docK = 0;

			for (int j = 0; j < ISCIBinModelList.size(); j++) {
				Log.e("displayname", ISCIBinModelList.get(j)
						.getDisplayFileName());
				Log.e("filetype", ISCIBinModelList.get(j).getFileType());
				Log.e("getDRateFormatIDKey", ISCIBinModelList.get(j)
						.getDRateFormatIDKey() + "");
				if (ISCIBinModelList.get(j).getFileType()
						.equalsIgnoreCase("pdf")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("doc")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("txt")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("docx")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("ppt")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("pptx")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("xls")
						|| ISCIBinModelList.get(j).getFileType()
								.equalsIgnoreCase("xlsx")) {
					documentpath[docK] = ISCIBinModelList.get(j)
							.getFileWebPAth();
					docK++;
				} else {
					imagevideopath[imgK] = ISCIBinModelList.get(j)
							.getFileWebPAth();
					imagevideothumbnailpath[imgK] = ISCIBinModelList.get(j)
							.getImagePath();
					imgK++;
				}
			}

			if (DisplayGridorList == true) {
				iscibinfolder_galley_gridview.setVisibility(View.VISIBLE);
				iscibinfolder_galley_listview.setVisibility(View.GONE);
				StickygalleryAdapter = new StickyISCIBinGalleryAdapter(
						ISCIBinActivity.this, ISCIBinModelList, imagevideopath,
						documentpath, imagevideothumbnailpath, false);
				iscibinfolder_galley_gridview.setAdapter(StickygalleryAdapter);
				iscibinfolder_galley_gridview
						.setEmptyView(iscibingallery_empty_emoji);

				if (StickygalleryAdapter.isEmpty()) {
					iscibin_select_action_gallery.setVisibility(View.GONE);
				} else {
					iscibin_select_action_gallery.setVisibility(View.VISIBLE);
				}
			} else {
				iscibinfolder_galley_gridview.setVisibility(View.GONE);
				iscibinfolder_galley_listview.setVisibility(View.VISIBLE);
				StickyListgalleryAdapter = new StickyISCIBinGalleryListAdapter(
						ISCIBinActivity.this, ISCIBinModelList, imagevideopath,
						documentpath, imagevideothumbnailpath, false);
				iscibinfolder_galley_listview
						.setAdapter(StickyListgalleryAdapter);
				iscibinfolder_galley_listview
						.setEmptyView(iscibingallery_empty_emoji);

				if (StickyListgalleryAdapter.isEmpty()) {
					iscibin_select_action_gallery.setVisibility(View.GONE);
				} else {
					iscibin_select_action_gallery.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public void setValuesModel(ISCIBinModel rIsciBinModel,
			SoapObject product_list) {
		rIsciBinModel.setIsciBinClient(product_list.getProperty("Client")
				.toString());
		rIsciBinModel.setDRateFormatIDKey(Integer.parseInt(product_list
				.getProperty("DRateFormatIDKey").toString()));
		rIsciBinModel.setDateCreated(product_list.getProperty("DateCreated")
				.toString());
		rIsciBinModel.setDocumentImageRip(product_list.getProperty(
				"DocumentImageRip").toString());
		rIsciBinModel.setFtcIn(product_list.getProperty("FTCIn").toString());
		rIsciBinModel.setFtcInFrame(Integer.parseInt(product_list.getProperty(
				"FTCInFrame").toString()));
		rIsciBinModel.setFtcOut(product_list.getProperty("FTCOut").toString());
		rIsciBinModel.setFtcOutFrame(Integer.parseInt(product_list.getProperty(
				"FTCOutFrame").toString()));
		rIsciBinModel.setFileName(product_list.getProperty("FileName")
				.toString());
		rIsciBinModel.setFileType(product_list.getProperty("FileType")
				.toString());
		rIsciBinModel.setFileWebPAth(product_list.getProperty("FileWebPath")
				.toString());
		rIsciBinModel.setIsciBinName(product_list.getProperty("ISCI")
				.toString());
		rIsciBinModel.setImageId(Integer.parseInt(product_list.getProperty(
				"ImageID").toString()));
		rIsciBinModel.setImagePath(product_list.getProperty("ImagePath")
				.toString());
		rIsciBinModel.setLatitude(product_list.getProperty("Latitude")
				.toString());
		rIsciBinModel.setLoggerLocation(product_list.getProperty(
				"LoggerLocation").toString());
		rIsciBinModel.setlongitude(product_list.getProperty("Longitude")
				.toString());
		rIsciBinModel.setIsciBinPath(product_list.getProperty("Path")
				.toString());
		rIsciBinModel.setProcessed(product_list.getProperty("Processed")
				.toString());
		rIsciBinModel.setIsciBinProduct(product_list.getProperty("Product")
				.toString());
		rIsciBinModel.setUserSaved(product_list.getProperty("UserSaved")
				.toString());
		rIsciBinModel.setVideoLength(product_list.getProperty("VideoLength")
				.toString());
	}

	public class StickyISCIBinGalleryAdapter extends BaseAdapter implements
			StickyGridHeadersSimpleAdapter, Filterable {

		private LayoutInflater inflater = null;
		private ImageLoader imageLoader;
		private DisplayImageOptions options;
		private ArrayList<ISCIBinModel> arlstISCIBinModel;
		private String[] documentpath;
		private String[] imagevideopath;
		private String[] imagevideothumbnailpath;
		private boolean setSelection;

		public StickyISCIBinGalleryAdapter(Activity a,
				ArrayList<ISCIBinModel> arlstISCIBinModel,
				String[] imagevideopath, String[] documentpath,
				String[] imagevideothumbnailpath, boolean setSelection) {
			inflater = LayoutInflater.from(a);
			this.arlstISCIBinModel = arlstISCIBinModel;
			this.imagevideopath = imagevideopath;
			this.documentpath = documentpath;
			this.imagevideothumbnailpath = imagevideothumbnailpath;
			this.setSelection = setSelection;

			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.no_media)
					.showImageForEmptyUri(R.drawable.no_media)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.showImageOnFail(R.drawable.ic_launcher).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					a.getApplicationContext())
					.defaultDisplayImageOptions(options)
					.discCacheExtraOptions(400, 400, CompressFormat.PNG, 0,
							null).build();

			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arlstISCIBinModel.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arlstISCIBinModel.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View vi = convertView;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vi = inflater.inflate(R.layout.activity_iscibin_gallery_item,
						parent, false);
			} else {
				vi = (View) convertView;
			}

			RelativeLayout relative_iscibinfolder = (RelativeLayout) vi
					.findViewById(R.id.relative_iscibinfolder);

			ImageView image = (ImageView) vi
					.findViewById(R.id.iv_iscibinfolder_galleryImage);

			ImageView iv_localclipbinfolder_videoicon = (ImageView) vi
					.findViewById(R.id.iv_iscibinfolder_videoicon);

			final ImageView iv_select_image = (ImageView) vi
					.findViewById(R.id.iv_isci_select_image);

			final ProgressBar progress_iscibin_image = (ProgressBar) vi
					.findViewById(R.id.progress_iscibin_image);

			TextView tv_iscibin_filename = (TextView) vi
					.findViewById(R.id.tv_iscibin_filename);
			tv_iscibin_filename.setText(arlstISCIBinModel.get(position)
					.getFileName());

			if (arlstISCIBinModel.get(position).getDisplayFileName()
					.subSequence(0, 1).equals("V")) {
				iv_localclipbinfolder_videoicon.setVisibility(View.VISIBLE);

			} else if (arlstISCIBinModel.get(position).getDisplayFileName()
					.subSequence(0, 1).equals("I")) {
				iv_localclipbinfolder_videoicon.setVisibility(View.GONE);

			} else {

			}
			imageLoader.displayImage(arlstISCIBinModel.get(position)
					.getImagePath(), image, options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							// TODO Auto-generated method stub
							progress_iscibin_image.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason failReason) {
							// TODO Auto-generated method stub
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(ISCIBinActivity.this, message,
									Toast.LENGTH_SHORT).show();

							progress_iscibin_image.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							progress_iscibin_image.setVisibility(View.VISIBLE);
						}

					});

			String filename = arlstISCIBinModel
					.get(position)
					.getFileWebPAth()
					.substring(
							arlstISCIBinModel.get(position).getFileWebPAth()
									.lastIndexOf("/") + 1);
			boolean isExist = dbh.CheckIsDataAlreadyInDBorNot(filename, 13);
			if (setSelection == true) {
				iv_select_image.setVisibility(View.VISIBLE);
				for (int i = 0; i < arlstISCIBinModel.size(); i++) {

					if (imgflag0[position] == true) {
						iv_select_image
								.setImageResource(R.drawable.gallery_image_selected);
					} else {
						iv_select_image
								.setImageResource(R.drawable.gallery_image_toselect);
					}
				}
			} else {
				iv_select_image.setVisibility(View.GONE);
			}

			relative_iscibinfolder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (setSelection == true) {
						Log.e("finalpos", position + "");
						if (crntSelectedPaths.containsKey(position)) {
							crntSelectedPaths.remove(position);
						} else {
							Log.e("sectionPathStrings.get(finalpos)",
									arlstISCIBinModel.get(position)
											.getFileWebPAth() + "");
							crntSelectedPaths.put(position, arlstISCIBinModel
									.get(position).getFileWebPAth());
						}

						if (crntSelectedFormatIDKeys.containsKey(position)) {
							crntSelectedFormatIDKeys.remove(position);
							crntSelectedFileNames.remove(position);
						} else {
							Log.e("crntSelectedFormatIDKeys", arlstISCIBinModel
									.get(position).getDRateFormatIDKey() + "");
							crntSelectedFormatIDKeys.put(position,
									arlstISCIBinModel.get(position)
											.getDRateFormatIDKey());
							crntSelectedFileNames.put(position,
									arlstISCIBinModel.get(position)
											.getFileName());
						}

						if (imgflag0[position] == false) {
							imgflag0[position] = true;
							iv_select_image
									.setImageResource(R.drawable.gallery_image_selected);
						} else {
							imgflag0[position] = false;
							iv_select_image
									.setImageResource(R.drawable.gallery_image_toselect);
						}
					} else {
						if (arlstISCIBinModel.get(position)
								.getDisplayFileName().subSequence(0, 1)
								.equals("V")
								|| arlstISCIBinModel.get(position)
										.getDisplayFileName().subSequence(0, 1)
										.equals("I")) {
							Intent i = new Intent(ISCIBinActivity.this,
									ImageVideoViewerActivity.class);
							i.putExtra("FilePathStrings", imagevideopath);
							i.putExtra("ThumbnailPaths",
									imagevideothumbnailpath);
							i.putExtra("setpagerposition", position);
							i.putExtra("foldername", ISCIName);
							i.putExtra("pathString", "");
							i.putExtra("clipbin", 2);
							startActivity(i);
						} else {
							AlertDialog.Builder builder1 = new AlertDialog.Builder(
									ISCIBinActivity.this);
							builder1.setMessage("View or Open Document?");
							builder1.setCancelable(true);
							builder1.setPositiveButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
							builder1.setNegativeButton("View",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent i = new Intent(
													ISCIBinActivity.this,
													GridViewDocument.class);
											i.putExtra(
													"DRateFormatIDKey",
													arlstISCIBinModel
															.get(position)
															.getDRateFormatIDKey());
											i.putExtra(
													"DocumentName",
													arlstISCIBinModel.get(
															position)
															.getFileName());
											startActivity(i);
											dialog.cancel();
										}
									});

							builder1.setNeutralButton("Open",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											String fileSelected = arlstISCIBinModel
													.get(position)
													.getFileWebPAth();
											Log.e("fileSelected", fileSelected);
											String downloaddocname = arlstISCIBinModel
													.get(position)
													.getDisplayFileName();

											String extStorageDirectory = Environment
													.getExternalStorageDirectory()
													.toString();
											File folder = new File(
													extStorageDirectory,
													"VideoBank VMR");
											folder.mkdir();
											File file = new File(folder,
													downloaddocname);
											if (file.exists() == true) {

												String fileSelected1 = "file://"
														+ extStorageDirectory
														+ "/VideoBank VMR/"
														+ downloaddocname;

												if (determineExtension(
														fileSelected1)
														.equalsIgnoreCase("pdf")) {
													if (canDisplayPdf(
															ISCIBinActivity.this,
															"application/pdf")) {
														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setDataAndType(
																Uri.parse(fileSelected1),
																"application/pdf");
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														startActivity(intent);


													} else {
														Toast.makeText(
																ISCIBinActivity.this,
																"No Application found to open pdf file",
																Toast.LENGTH_LONG)
																.show();
													}
												} else if (determineExtension(
														fileSelected1)
														.equalsIgnoreCase("doc")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"docx")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"ppt")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"pptx")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"xls")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"xlsx")) {
													if (canDisplayPdf(
															ISCIBinActivity.this,
															"application/msword")) {

														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setDataAndType(
																Uri.parse(fileSelected1),
																"application/msword");
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														startActivity(intent);

													} else {
														Toast.makeText(
																ISCIBinActivity.this,
																"No Application found to open this file",
																Toast.LENGTH_LONG)
																.show();
													}
												} else if (determineExtension(
														fileSelected1)
														.equalsIgnoreCase("txt")) {
													if (canDisplayPdf(
															ISCIBinActivity.this,
															"text/*")) {
														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setDataAndType(
																Uri.parse(fileSelected1),
																"text/*");
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														startActivity(intent);
													} else {
														Toast.makeText(
																ISCIBinActivity.this,
																"No Application found to open this file",
																Toast.LENGTH_LONG)
																.show();
													}
												} else {
													Toast.makeText(
															ISCIBinActivity.this,
															"No Application to open the file",
															Toast.LENGTH_LONG)
															.show();
												}
											} else {
												try {
													file.createNewFile();
												} catch (IOException e1) {
													e1.printStackTrace();
												}

												new downloadFile(fileSelected,
														file,
														extStorageDirectory,
														downloaddocname)
														.execute(null, null,
																null);
											}
											dialog.cancel();
										}

									});

							AlertDialog alert11 = builder1.create();
							alert11.show();

						}
					}
				}
			});

			return vi;
		}

		@Override
		public long getHeaderId(int position) {
			// TODO Auto-generated method stub
			return arlstISCIBinModel.get(position).getDisplayFileName()
					.substring(0, 1).charAt(0);
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			HeaderViewHolder holder;
			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = inflater.inflate(R.layout.header, parent, false);
				holder.textheader = (TextView) convertView
						.findViewById(R.id.textheader);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			String headerText = ""
					+ arlstISCIBinModel.get(position).getDisplayFileName()
							.subSequence(0, 1).charAt(0);

			if (headerText.equals("I")) {
				holder.textheader.setText("Images");
			} else if (headerText.equals("V")) {
				holder.textheader.setText("Videos");
			} else {
				holder.textheader.setText("Documents");
			}
			return convertView;
		}

		class HeaderViewHolder {
			TextView textheader;
		}

		class ViewHolder {
			TextView text;
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			Filter filter = new Filter() {

				ArrayList<ISCIBinModel> OriginalList = arlstISCIBinModel;
				private String[] documentpathfilter = new String[documentpath.length];
				private String[] imagevideopathfilter = new String[imagevideopath.length];
				private String[] imagevideothumbnailpathfilter = new String[imagevideothumbnailpath.length];

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					ArrayList<ISCIBinModel> temporarylist = (ArrayList<ISCIBinModel>) results.values;
					Intent i = new Intent(ISCIBinActivity.this,
							PowerSearchFilter3TierActivity.class);
					Bundle bundleObject = new Bundle();
					bundleObject.putSerializable("filterdata", temporarylist);

					// Put Bundle in to Intent and call start Activity
					i.putExtras(bundleObject);
					i.putExtra("ISCIName", ISCIName);

					i.putExtra("imagevideopath", imagevideopathfilter);
					i.putExtra("documentpath", documentpathfilter);
					i.putExtra("imagevideothumbnailpath",
							imagevideothumbnailpathfilter);

					startActivity(i);
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<ISCIBinModel> FilteredList = new ArrayList<ISCIBinModel>();

					if (constraint == null || constraint.length() == 0) {
						// No filter implemented we return all the list
						results.values = OriginalList;

						results.count = OriginalList.size();
					} else {

						OriginalList = arlstISCIBinModel;
						Log.d("isze", OriginalList.size() + "");
						int j = 0;
						for (int i = 0; i < OriginalList.size(); i++) {
							String data = OriginalList.get(i)
									.getDisplayFileName();
							Log.d("search", data + i + "-" + constraint);
							if (data.toLowerCase().contains(
									constraint.toString())) {
								Log.d("match", data + i + "-" + constraint);
								if (OriginalList.get(i).getFileType()
										.equalsIgnoreCase("jpg")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("png")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("jpeg")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("gif")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("bmp")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("tif")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("gift")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("m4v")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("3gp")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("mp4")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("mov")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("wmv")) {
									imagevideopathfilter[j] = imagevideopath[i];
									imagevideothumbnailpathfilter[j] = imagevideothumbnailpath[i];
								} else {
									documentpathfilter[j] = documentpath[i];
								}

								FilteredList.add(OriginalList.get(i));
								j++;

							}
						}
						results.values = FilteredList;
						results.count = FilteredList.size();
					}
					return results;
				}
			};
			return filter;
		}
	}

	public class StickyISCIBinGalleryListAdapter extends BaseAdapter implements
			StickyListHeadersAdapter, Filterable {

		private LayoutInflater inflater = null;
		private ImageLoader imageLoader;
		private DisplayImageOptions options;
		private ArrayList<ISCIBinModel> arlstISCIBinModel;
		private String[] documentpath;
		private String[] imagevideopath;
		private String[] imagevideothumbnailpath;
		private boolean setSelection;

		public StickyISCIBinGalleryListAdapter(Activity a,
				ArrayList<ISCIBinModel> arlstISCIBinModel,
				String[] imagevideopath, String[] documentpath,
				String[] imagevideothumbnailpath, boolean setSelection) {
			inflater = LayoutInflater.from(a);
			this.arlstISCIBinModel = arlstISCIBinModel;
			this.imagevideopath = new String[imagevideopath.length];
			this.imagevideopath = imagevideopath;
			this.documentpath = new String[documentpath.length];
			this.documentpath = documentpath;
			this.imagevideothumbnailpath = new String[imagevideothumbnailpath.length];
			this.imagevideothumbnailpath = imagevideothumbnailpath;
			this.setSelection = setSelection;

			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.no_media)
					.showImageForEmptyUri(R.drawable.no_media)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.showImageOnFail(R.drawable.ic_launcher).cacheInMemory()
					.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					a.getApplicationContext())
					.defaultDisplayImageOptions(options)
					.discCacheExtraOptions(400, 400, CompressFormat.PNG, 0,
							null).build();

			imageLoader = ImageLoader.getInstance();
			imageLoader.init(config);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arlstISCIBinModel.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arlstISCIBinModel.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			View vi = convertView;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vi = inflater.inflate(
						R.layout.activity_iscibin_gallery_list_item, parent,
						false);
			} else {
				vi = (View) convertView;
			}

			LinearLayout linear_list_iscibinfolder = (LinearLayout) vi
					.findViewById(R.id.linear_list_iscibinfolder);

			ImageView image = (ImageView) vi
					.findViewById(R.id.iv_list_iscibinfolder_galleryImage);

			ImageView iv_localclipbinfolder_videoicon = (ImageView) vi
					.findViewById(R.id.iv_list_iscibinfolder_videoicon);

			final ImageView iv_select_image = (ImageView) vi
					.findViewById(R.id.iv_list_isci_select_image);

			final ProgressBar progress_iscibin_image = (ProgressBar) vi
					.findViewById(R.id.list_progress_iscibin_image);

			TextView tv_iscibin_filename = (TextView) vi
					.findViewById(R.id.tv_list_iscibin_filename);

			tv_iscibin_filename.setText(arlstISCIBinModel.get(position)
					.getFileName());

			if (arlstISCIBinModel.get(position).getDisplayFileName()
					.subSequence(0, 1).equals("V")) {
				iv_localclipbinfolder_videoicon.setVisibility(View.VISIBLE);

			} else if (arlstISCIBinModel.get(position).getDisplayFileName()
					.subSequence(0, 1).equals("I")) {
				iv_localclipbinfolder_videoicon.setVisibility(View.GONE);

			} else {

			}
			imageLoader.displayImage(arlstISCIBinModel.get(position)
					.getImagePath(), image, options,
					new ImageLoadingListener() {

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							// TODO Auto-generated method stub
							progress_iscibin_image.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason failReason) {
							// TODO Auto-generated method stub
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(ISCIBinActivity.this, message,
									Toast.LENGTH_SHORT).show();

							progress_iscibin_image.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							// TODO Auto-generated method stub
							progress_iscibin_image.setVisibility(View.VISIBLE);
						}

					});

			String filename = arlstISCIBinModel
					.get(position)
					.getFileWebPAth()
					.substring(
							arlstISCIBinModel.get(position).getFileWebPAth()
									.lastIndexOf("/") + 1);
			boolean isExist = dbh.CheckIsDataAlreadyInDBorNot(filename, 13);

			if (setSelection == true) {
				iv_select_image.setVisibility(View.VISIBLE);
				for (int i = 0; i < arlstISCIBinModel.size(); i++) {

					if (imgflag0[position] == true) {
						iv_select_image
								.setImageResource(R.drawable.gallery_image_selected);
					} else {
						iv_select_image
								.setImageResource(R.drawable.gallery_image_toselect);
					}
				}
			} else {
				iv_select_image.setVisibility(View.GONE);
			}

			linear_list_iscibinfolder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (setSelection == true) {
						Log.e("finalpos", position + "");
						if (crntSelectedPaths.containsKey(position)) {
							crntSelectedPaths.remove(position);
						} else {
							Log.e("sectionPathStrings.get(finalpos)",
									arlstISCIBinModel.get(position)
											.getFileWebPAth() + "");
							crntSelectedPaths.put(position, arlstISCIBinModel
									.get(position).getFileWebPAth());
						}

						if (crntSelectedFormatIDKeys.containsKey(position)) {
							crntSelectedFormatIDKeys.remove(position);
							crntSelectedFileNames.remove(position);
						} else {
							Log.e("crntSelectedFormatIDKeys", arlstISCIBinModel
									.get(position).getDRateFormatIDKey() + "");
							crntSelectedFormatIDKeys.put(position,
									arlstISCIBinModel.get(position)
											.getDRateFormatIDKey());
							crntSelectedFileNames.put(position,
									arlstISCIBinModel.get(position)
											.getFileName());
						}

						if (imgflag0[position] == false) {
							imgflag0[position] = true;
							iv_select_image
									.setImageResource(R.drawable.gallery_image_selected);
						} else {
							imgflag0[position] = false;
							iv_select_image
									.setImageResource(R.drawable.gallery_image_toselect);
						}
					} else {
						if (arlstISCIBinModel.get(position)
								.getDisplayFileName().subSequence(0, 1)
								.equals("V")
								|| arlstISCIBinModel.get(position)
										.getDisplayFileName().subSequence(0, 1)
										.equals("I")) {
							Intent i = new Intent(ISCIBinActivity.this,
									ImageVideoViewerActivity.class);
							i.putExtra("FilePathStrings", imagevideopath);
							i.putExtra("ThumbnailPaths",
									imagevideothumbnailpath);
							i.putExtra("setpagerposition", position);
							i.putExtra("foldername", ISCIName);
							i.putExtra("pathString", "");
							i.putExtra("clipbin", 2);
							startActivity(i);
						} else {
							AlertDialog.Builder builder1 = new AlertDialog.Builder(
									ISCIBinActivity.this);
							builder1.setMessage("View or Open Document?");
							builder1.setCancelable(true);
							builder1.setPositiveButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
							builder1.setNegativeButton("View",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Intent i = new Intent(
													ISCIBinActivity.this,
													GridViewDocument.class);
											i.putExtra(
													"DRateFormatIDKey",
													arlstISCIBinModel
															.get(position)
															.getDRateFormatIDKey());
											i.putExtra(
													"DocumentName",
													arlstISCIBinModel.get(
															position)
															.getFileName());
											startActivity(i);
											dialog.cancel();
										}
									});

							builder1.setNeutralButton("Open",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											String fileSelected = arlstISCIBinModel
													.get(position)
													.getFileWebPAth();
											Log.e("fileSelected", fileSelected);
											String downloaddocname = arlstISCIBinModel
													.get(position)
													.getDisplayFileName();

											String extStorageDirectory = Environment
													.getExternalStorageDirectory()
													.toString();
											File folder = new File(
													extStorageDirectory,
													"VideoBank VMR");
											folder.mkdir();
											File file = new File(folder,
													downloaddocname);
											if (file.exists() == true) {

												String fileSelected1 = "file://"
														+ extStorageDirectory
														+ "/VideoBank VMR/"
														+ downloaddocname;

												if (determineExtension(
														fileSelected1)
														.equalsIgnoreCase("pdf")) {
													if (canDisplayPdf(
															ISCIBinActivity.this,
															"application/pdf")) {
														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setDataAndType(
																Uri.parse(fileSelected1),
																"application/pdf");
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														startActivity(intent);


													} else {
														Toast.makeText(
																ISCIBinActivity.this,
																"No Application found to open pdf file",
																Toast.LENGTH_LONG)
																.show();
													}
												} else if (determineExtension(
														fileSelected1)
														.equalsIgnoreCase("doc")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"docx")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"ppt")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"pptx")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"xls")
														|| determineExtension(
																fileSelected1)
																.equalsIgnoreCase(
																		"xlsx")) {
													if (canDisplayPdf(
															ISCIBinActivity.this,
															"application/msword")) {

														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setDataAndType(
																Uri.parse(fileSelected1),
																"application/msword");
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														startActivity(intent);

													} else {
														Toast.makeText(
																ISCIBinActivity.this,
																"No Application found to open this file",
																Toast.LENGTH_LONG)
																.show();
													}
												} else if (determineExtension(
														fileSelected1)
														.equalsIgnoreCase("txt")) {
													if (canDisplayPdf(
															ISCIBinActivity.this,
															"text/*")) {
														Intent intent = new Intent(
																Intent.ACTION_VIEW);
														intent.setDataAndType(
																Uri.parse(fileSelected1),
																"text/*");
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														startActivity(intent);
													} else {
														Toast.makeText(
																ISCIBinActivity.this,
																"No Application found to open this file",
																Toast.LENGTH_LONG)
																.show();
													}
												} else {
													Toast.makeText(
															ISCIBinActivity.this,
															"No Application to open the file",
															Toast.LENGTH_LONG)
															.show();
												}
											} else {
												try {
													file.createNewFile();
												} catch (IOException e1) {
													e1.printStackTrace();
												}

												new downloadFile(fileSelected,
														file,
														extStorageDirectory,
														downloaddocname)
														.execute(null, null,
																null);
											}
											dialog.cancel();
										}

									});

							AlertDialog alert11 = builder1.create();
							alert11.show();

						}
					}
				}
			});

			return vi;
		}

		@Override
		public long getHeaderId(int position) {
			// TODO Auto-generated method stub
			return arlstISCIBinModel.get(position).getDisplayFileName()
					.substring(0, 1).charAt(0);
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			HeaderViewHolder holder;
			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = inflater.inflate(R.layout.header, parent, false);
				holder.textheader = (TextView) convertView
						.findViewById(R.id.textheader);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}
			String headerText = ""
					+ arlstISCIBinModel.get(position).getDisplayFileName()
							.subSequence(0, 1).charAt(0);

			if (headerText.equals("I")) {
				holder.textheader.setText("Images");
			} else if (headerText.equals("V")) {
				holder.textheader.setText("Videos");
			} else {
				holder.textheader.setText("Documents");
			}
			return convertView;
		}

		class HeaderViewHolder {
			TextView textheader;
		}

		class ViewHolder {
			TextView text;
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			Filter filter = new Filter() {

				ArrayList<ISCIBinModel> OriginalList = arlstISCIBinModel;
				private String[] documentpathfilter = new String[documentpath.length];
				private String[] imagevideopathfilter = new String[imagevideopath.length];
				private String[] imagevideothumbnailpathfilter = new String[imagevideothumbnailpath.length];

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					ArrayList<ISCIBinModel> temporarylist = (ArrayList<ISCIBinModel>) results.values;
					Intent i = new Intent(ISCIBinActivity.this,
							PowerSearchFilter3TierActivity.class);
					Bundle bundleObject = new Bundle();
					bundleObject.putSerializable("filterdata", temporarylist);

					// Put Bundle in to Intent and call start Activity
					i.putExtras(bundleObject);
					i.putExtra("ISCIName", ISCIName);

					i.putExtra("imagevideopath", imagevideopathfilter);
					i.putExtra("documentpath", documentpathfilter);
					i.putExtra("imagevideothumbnailpath",
							imagevideothumbnailpathfilter);

					startActivity(i);
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults();
					ArrayList<ISCIBinModel> FilteredList = new ArrayList<ISCIBinModel>();

					if (constraint == null || constraint.length() == 0) {
						// No filter implemented we return all the list
						results.values = OriginalList;

						results.count = OriginalList.size();
					} else {

						OriginalList = arlstISCIBinModel;
						Log.d("isze", OriginalList.size() + "");
						int j = 0;
						for (int i = 0; i < OriginalList.size(); i++) {
							String data = OriginalList.get(i)
									.getDisplayFileName();
							Log.d("search", data + i + "-" + constraint);
							if (data.toLowerCase().contains(
									constraint.toString())) {
								Log.d("match", data + i + "-" + constraint);
								if (OriginalList.get(i).getFileType()
										.equalsIgnoreCase("jpg")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("png")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("jpeg")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("gif")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("bmp")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("tif")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("gift")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("m4v")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("3gp")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("mp4")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("mov")
										|| OriginalList.get(i).getFileType()
												.equalsIgnoreCase("wmv")) {
									imagevideopathfilter[j] = imagevideopath[i];
									imagevideothumbnailpathfilter[j] = imagevideothumbnailpath[i];
								} else {
									documentpathfilter[j] = documentpath[i];
								}

								FilteredList.add(OriginalList.get(i));
								j++;

							}
						}
						results.values = FilteredList;
						results.count = FilteredList.size();
					}
					return results;
				}
			};
			return filter;
		}
	}

	public static boolean canDisplayPdf(Context context, String MIME_TYPE) {
		PackageManager packageManager = context.getPackageManager();
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		testIntent.setType(MIME_TYPE);
		if (packageManager.queryIntentActivities(testIntent,
				PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static String determineExtension(String filename) {
		String filenameArray[] = filename.split("\\.");
		String extension = filenameArray[filenameArray.length - 1];
		return extension;
	}

	class downloadFile extends AsyncTask<Void, Void, Void> {

		private String fileURL;
		private File directory;
		private String extStorageDirectory;
		private String downloaddocname;
		private static final int MEGABYTE = 1024 * 1024;

		public downloadFile(String fileURL, File directory,
				String extStorageDirectory, String downloaddocname) {
			// TODO Auto-generated constructor stub
			this.fileURL = fileURL;
			this.directory = directory;
			this.extStorageDirectory = extStorageDirectory;
			this.downloaddocname = downloaddocname;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			SVProgressHUD.showInView(ISCIBinActivity.this,
					"Opening and Caching..", true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {

				URL url = new URL(fileURL);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				// urlConnection.setRequestMethod("GET");
				// urlConnection.setDoOutput(true);
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				FileOutputStream fileOutputStream = new FileOutputStream(
						directory);
				int totalSize = urlConnection.getContentLength();

				byte[] buffer = new byte[MEGABYTE];
				int bufferLength = 0;
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, bufferLength);
				}
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			super.onPostExecute(result);
			SVProgressHUD.dismiss(ISCIBinActivity.this);
			Log.e("PAth", extStorageDirectory + "/VideoBank VMR/"
					+ downloaddocname);
			String fileSelected = "file://" + extStorageDirectory
					+ "/VideoBank VMR/" + downloaddocname;

			if (determineExtension(fileSelected).equalsIgnoreCase("pdf")) {
				if (canDisplayPdf(ISCIBinActivity.this, "application/pdf")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(fileSelected),
							"application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);

				} else {
					Toast.makeText(ISCIBinActivity.this,
							"No Application found to open pdf file",
							Toast.LENGTH_LONG).show();
				}
			} else if (determineExtension(fileSelected).equalsIgnoreCase("doc")
					|| determineExtension(fileSelected)
							.equalsIgnoreCase("docx")
					|| determineExtension(fileSelected).equalsIgnoreCase("ppt")
					|| determineExtension(fileSelected)
							.equalsIgnoreCase("pptx")
					|| determineExtension(fileSelected).equalsIgnoreCase("xls")
					|| determineExtension(fileSelected)
							.equalsIgnoreCase("xlsx")) {
				if (canDisplayPdf(ISCIBinActivity.this, "application/msword")) {

					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(fileSelected),
							"application/msword");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);

				} else {
					Toast.makeText(ISCIBinActivity.this,
							"No Application found to open this file",
							Toast.LENGTH_LONG).show();
				}
			} else if (determineExtension(fileSelected).equalsIgnoreCase("txt")) {
				if (canDisplayPdf(ISCIBinActivity.this, "text/*")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(fileSelected), "text/*");
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(intent);
				} else {
					Toast.makeText(ISCIBinActivity.this,
							"No Application found to open this file",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(ISCIBinActivity.this,
						"No Application to open the file", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case DownloadService.STATUS_RUNNING:

			Toast.makeText(this, "Downloading Started", Toast.LENGTH_LONG)
					.show();

			ll_iscibin_editoptions.setVisibility(View.GONE);
			StickyListgalleryAdapter = new StickyISCIBinGalleryListAdapter(
					ISCIBinActivity.this, ISCIBinModelList, imagevideopath,
					documentpath, imagevideothumbnailpath, false);
			iscibinfolder_galley_listview.setAdapter(StickyListgalleryAdapter);
			iscibinfolder_galley_listview
					.setEmptyView(iscibingallery_empty_emoji);
			iscibin_select_action_gallery.setText("Select");
			selectOptionorCancel = true;
			break;
		case UploadService.STATUS_FINISHED:
			Toast.makeText(this, "Downloading Completed", Toast.LENGTH_LONG)
					.show();
			break;
		case UploadService.STATUS_ERROR:
			Toast.makeText(this, "Downloading Error!!", Toast.LENGTH_LONG)
					.show();
			break;
		case UploadService.STATUS_SOMEFILES:
			Toast.makeText(this, "Error! Some Files not Downloaded.",
					Toast.LENGTH_LONG).show();
			break;
		}
	}
}
