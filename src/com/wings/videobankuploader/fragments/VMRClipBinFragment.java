package com.wings.videobankuploader.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.ksoap2.serialization.SoapObject;
import org.lcsky.SVProgressHUD;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wings.videobankuploader.Activity_PortConnect;
import com.wings.videobankuploader.MainActivity;
import com.wings.videobankuploader.PowerSearchActivity;
import com.wings.videobankuploader.R;
import com.wings.videobankuploader.SigninActivity;
import com.wings.videobankuploader.VMRAllISCIActivity;
import com.wings.videobankuploader.adapter.VMRBinListAdapter;
import com.wings.videobankuploader.database_helper.DataBase_Helper;
import com.wings.videobankuploader.globals.Constant;
import com.wings.videobankuploader.globals.GlobalMethods;
import com.wings.videobankuploader.models.ReviewProductModel;

public class VMRClipBinFragment extends Fragment {

	private LinearLayout linearvmremptyview;
	private PullToRefreshListView vmrclipbinlist;
	private SharedPreferences sharedpreferences;
	private String UsernamesID;
	public ArrayList<ReviewProductModel> reviewProductModelList = new ArrayList<ReviewProductModel>();
	private VMRBinListAdapter adapter;
	private RelativeLayout relative_vmr_login;
	private Button vmr_buttonlogin;
	private FloatingActionMenu vmr_menu_fab;
	private boolean vmr_connect_flag = true;
	private TextView tv_vmrbin;
	private DataBase_Helper dbh;
	boolean isVMRLoaded = false;
	private FloatingActionButton vmr_add_directory;
	private PopupWindow pwindo;
	private ImageView iv_vmrproductpopup_close;
	private EditText et_vmrproduct;
	private Button btn_popup_create_vmrproduct;
	String[] VMRClientArray = { "REVIEW", "TARGET FOLDER", "ARCHIVE",
			"FIXEDCAMERA" };
	private Spinner spn_vmrclient;
	ArrayList<String> AccessPermissionArray = new ArrayList<String>();
	private FloatingActionButton vmr_power_search;
	private Button vmr_buttonconnect;
	private TextView vmr_connectioninfotext;
	private SharedPreferences sharedpreferencesPrefPortConnect;
	private TextView vmr_logintext;
	private ImageView vmr_disconnectbtn;
	private TextView vmr_connectioninfotext1;
	private View view;
	private String VMRHostforRefreshList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_vmrclipbin, container, false);

		linearvmremptyview = (LinearLayout) view
				.findViewById(R.id.linearvmremptyview);
		vmrclipbinlist = (PullToRefreshListView) view
				.findViewById(R.id.vmrclipbinlist);
		relative_vmr_login = (RelativeLayout) view
				.findViewById(R.id.relative_vmr_login);
		vmr_menu_fab = (FloatingActionMenu) view
				.findViewById(R.id.vmr_menu_fab);
		vmr_power_search = (FloatingActionButton) view
				.findViewById(R.id.vmr_power_search);
		tv_vmrbin = (TextView) view.findViewById(R.id.tv_vmrbin);
		vmr_buttonlogin = (Button) view.findViewById(R.id.vmr_buttonlogin);
		vmr_buttonconnect = (Button) view.findViewById(R.id.vmr_buttonconnect);
		vmr_connectioninfotext = (TextView) view
				.findViewById(R.id.vmr_connectioninfotext);
		vmr_connectioninfotext1 = (TextView) view
				.findViewById(R.id.vmr_connectioninfotext1);
		vmr_logintext = (TextView) view.findViewById(R.id.vmr_logintext);
		vmr_add_directory = (FloatingActionButton) view
				.findViewById(R.id.vmr_add_directory);
		vmr_disconnectbtn = (ImageView) view
				.findViewById(R.id.vmr_disconnectbtn);

		dbh = new DataBase_Helper(getActivity().getApplicationContext());

		sharedpreferences = getActivity().getSharedPreferences(
				Constant.PrefName, 0);
		sharedpreferencesPrefPortConnect = getActivity().getSharedPreferences(
				Constant.PrefPortConnect, 0);
		UsernamesID = sharedpreferences.getString("UsernamesID", "");
		int UserAccessPermissionCount = sharedpreferences.getInt(
				"UserAccessPermissionCount", 0);

		if (UserAccessPermissionCount > 0) {
			for (int i = 0; i < UserAccessPermissionCount; i++) {
				AccessPermissionArray.add(sharedpreferences.getString(
						"UserAccessPermission" + i, ""));
			}
		}

		if (UsernamesID != null && !UsernamesID.equals("")) {
			if (sharedpreferencesPrefPortConnect.contains("VMRConnectHostname")) {
				final String VMRConnectHostname = sharedpreferencesPrefPortConnect
						.getString("VMRConnectHostname", "");
				VMRHostforRefreshList = VMRConnectHostname;
				Log.e("VMRConnectHostname", VMRConnectHostname);
				if (VMRConnectHostname != null
						&& !VMRConnectHostname.equals("")) {
					new GetAllReviewProducts(VMRConnectHostname).execute(null,
							null, null);

				} else {
					// ConnectPort(false);
				}
			} else {
				// ConnectPort(false);
			}
		} else {
			vmrclipbinlist.setVisibility(View.GONE);
			relative_vmr_login.setVisibility(View.VISIBLE);
			vmr_menu_fab.setVisibility(View.GONE);

			if (sharedpreferencesPrefPortConnect.contains("VMRConnectHostname")) {
				final String VMRConnectHostname = sharedpreferencesPrefPortConnect
						.getString("VMRConnectHostname", "");
				if (VMRConnectHostname != null
						&& !VMRConnectHostname.equals("")) {
					vmr_buttonlogin.setText("LOGIN");
					vmr_buttonconnect.setVisibility(View.VISIBLE);
					vmr_connectioninfotext.setVisibility(View.VISIBLE);
					vmr_connectioninfotext1.setVisibility(View.VISIBLE);
					vmr_disconnectbtn.setVisibility(View.VISIBLE);
					vmr_connectioninfotext.setText(""
							+ sharedpreferencesPrefPortConnect.getString(
									"VMRConnectHostname", ""));
					vmr_logintext
							.setText("You are not Logged in. \n Please Log In to access VMR");
					vmr_disconnectbtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							new AlertDialog.Builder(getActivity())
									.setTitle("Disconnect Server")
									.setMessage(
											"Are you sure you want to disconnect Server "
													+ VMRConnectHostname + "?")
									.setPositiveButton(
											android.R.string.yes,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													// continue with
													// delete
													Editor editorportpref = sharedpreferencesPrefPortConnect
															.edit();
													editorportpref.clear();
													editorportpref.commit();
													Editor editpref = sharedpreferences
															.edit();
													editpref.putString(
															"transition", "vmr");
													editpref.commit();
													Intent i = new Intent(
															getActivity(),
															MainActivity.class);
													i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													startActivity(i);
													getActivity().finish();
													getActivity()
															.overridePendingTransition(
																	R.anim.push_up_in,
																	R.anim.push_up_out);
												}
											})
									.setNegativeButton(
											android.R.string.no,
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int which) {
													// do nothing
												}
											})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						}
					});
				} else {
					vmr_buttonlogin.setText("CONNECT");
					vmr_buttonconnect.setVisibility(View.GONE);
					vmr_connectioninfotext.setVisibility(View.GONE);
					vmr_connectioninfotext1.setVisibility(View.GONE);
					vmr_disconnectbtn.setVisibility(View.GONE);
					vmr_logintext
							.setText("You are not Connected to any server. \n Please connect to access VMR");
				}
			} else {
				vmr_buttonlogin.setText("CONNECT");
				vmr_buttonconnect.setVisibility(View.GONE);
				vmr_connectioninfotext.setVisibility(View.GONE);
				vmr_connectioninfotext1.setVisibility(View.GONE);
				vmr_disconnectbtn.setVisibility(View.GONE);
				vmr_logintext
						.setText("You are not Connected to any server. \n Please connect to access VMR");
			}

			vmr_buttonlogin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (sharedpreferencesPrefPortConnect
							.contains("VMRConnectHostname")) {
						String VMRConnectHostname = sharedpreferencesPrefPortConnect
								.getString("VMRConnectHostname", "");
						if (VMRConnectHostname != null
								&& !VMRConnectHostname.equals("")) {
							Editor editpref = sharedpreferences.edit();
							editpref.putString("transition", "vmr");
							editpref.commit();
							Intent i = new Intent(getActivity(),
									SigninActivity.class);
							startActivity(i);
							getActivity().overridePendingTransition(
									R.anim.push_up_in, R.anim.push_up_out);
						} else {
							Log.e("vmr", "transition1");
							Editor editpref = sharedpreferences.edit();
							editpref.putString("transition", "vmr");
							editpref.commit();
							Intent i = new Intent(getActivity(),
									Activity_PortConnect.class);
							startActivity(i);
							getActivity().overridePendingTransition(
									R.anim.push_up_in, R.anim.push_up_out);
						}
					} else {
						Log.e("vmr", "transition2");
						Editor editpref = sharedpreferences.edit();
						editpref.putString("transition", "vmr");
						editpref.commit();
						Intent i = new Intent(getActivity(),
								Activity_PortConnect.class);
						startActivity(i);
						getActivity().overridePendingTransition(
								R.anim.push_up_in, R.anim.push_up_out);
					}
				}
			});

			vmr_buttonconnect.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Editor editpref = sharedpreferences.edit();
					editpref.putString("transition", "vmr");
					editpref.commit();
					Intent i = new Intent(getActivity(),
							Activity_PortConnect.class);
					startActivity(i);
					getActivity().overridePendingTransition(R.anim.push_up_in,
							R.anim.push_up_out);
				}
			});
		}

		vmrclipbinlist.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// Do work to refresh the list here.
				new GetAllReviewProducts(VMRHostforRefreshList).execute(null,
						null, null);
			}
		});

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (vmr_menu_fab.isOpened()) {
					vmr_menu_fab.close(true);
				}
			}
		});

		vmr_add_directory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (AccessPermissionArray.contains("AddProduct")) {
					initiatePopupWindow();
				} else {
					Toast.makeText(getActivity(),
							"You don't have 'Add Product' Permission",
							Toast.LENGTH_LONG).show();
				}

				if (vmr_menu_fab.isOpened()) {
					vmr_menu_fab.close(true);
				}
			}
		});

		vmr_power_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InitiatePowerSearchPopUp();
				if (vmr_menu_fab.isOpened()) {
					vmr_menu_fab.close(true);
				}
			}
		});

		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SVProgressHUD.dismiss(getActivity());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		SVProgressHUD.dismiss(getActivity());
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser && !isVMRLoaded) {

			if (UsernamesID != null && !UsernamesID.equals("")) {
				if (sharedpreferencesPrefPortConnect
						.contains("VMRConnectHostname")) {
					String VMRConnectHostname = sharedpreferencesPrefPortConnect
							.getString("VMRConnectHostname", "");
					Log.e("VMRConnectHostname", VMRConnectHostname);
					Log.e("products1", "I will review products");
					if (VMRConnectHostname != null
							&& !VMRConnectHostname.equals("")) {
						Log.e("products", "I will review products");
						new GetAllReviewProducts(VMRConnectHostname).execute(
								null, null, null);
					} else {
						// ConnectPort(false);
						Log.e("products", "I will not review products");
					}
				} else {
					// ConnectPort(false);
				}
			}
			isVMRLoaded = true;
		}
	}

	class GetAllReviewProducts extends AsyncTask<String, String, String> {

		SoapObject resultSoap;
		private String sOAPHost;
		String resultSoapString = "";

		public GetAllReviewProducts(String sOAPHost) {
			// TODO Auto-generated constructor stub
			this.sOAPHost = sOAPHost;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD
					.showInView(getActivity(), "Fetching Clip Bin..", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String[] attributeslist2 = { "userId" };
			String[] attributeslist_value = { UsernamesID };

			SoapObject resultGetAllReviewProducts = GlobalMethods.SOAPMethod(
					"GetAllReviewProducts", attributeslist2,
					attributeslist_value, sOAPHost);
			if (resultGetAllReviewProducts != null) {
				resultSoap = resultGetAllReviewProducts;
				resultSoapString = resultGetAllReviewProducts.toString();
				Log.e("resultSoapString", resultSoapString);
			} else {
				Log.e("resultSoapString", "Error! Not found!");
				resultSoapString = "Error! Not found!";
			}

			return resultSoapString;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(getActivity());

			if (getActivity() != null) {
				HideKeyboard();
			}

			if (!result.equals("Error! Not found!")) {

				if (getUserVisibleHint()) {

					Log.e("result", result);
					// scroll_port_connect.setVisibility(View.GONE);
					vmrclipbinlist.setVisibility(View.VISIBLE);
					vmr_menu_fab.setVisibility(View.VISIBLE);
					// vmr_port_connect_fab.setVisibility(View.VISIBLE);
					reviewProductModelList.clear();
					for (int i = 0; i < resultSoap.getPropertyCount(); i++) {
						ReviewProductModel rProModel = new ReviewProductModel();
						Object property = resultSoap.getProperty(i);
						if (property instanceof SoapObject) {
							SoapObject product_list = (SoapObject) property;

							rProModel.setClientName(product_list.getProperty(
									"CLIENT").toString());
							rProModel.setCreatedDate(product_list.getProperty(
									"CreatedDate").toString());
							rProModel.setProductName(product_list.getProperty(
									"PRODUCT1").toString());
							rProModel.setThumbnail(product_list.getProperty(
									"Thumbnail").toString());
						}
						reviewProductModelList.add(rProModel);
					}

					tv_vmrbin.setVisibility(View.GONE);

					adapter = new VMRBinListAdapter(getActivity(),
							reviewProductModelList);
					vmrclipbinlist.setAdapter(adapter);
					vmrclipbinlist.onRefreshComplete();

					registerForContextMenu(vmrclipbinlist);

					vmrclipbinlist
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									// TODO Auto-generated method stub
									if (vmr_menu_fab.isOpened()) {
										vmr_menu_fab.close(true);
									} else {
										Intent i = new Intent(getActivity(),
												VMRAllISCIActivity.class);
										i.putExtra(
												"ProductName",
												reviewProductModelList.get(
														position - 1)
														.getProductName());
										i.putExtra(
												"ClientName",
												reviewProductModelList.get(
														position - 1)
														.getClientName());
										startActivity(i);
									}
								}
							});
				} else {
					vmrclipbinlist.setEmptyView(linearvmremptyview);
				}
			} else {
				Toast.makeText(getActivity(),
						"Failed to connect with generated URL : " + sOAPHost,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Select Action");
		menu.add(0, v.getId(), 0, "Delete");
		menu.add(0, v.getId(), 0, "Rename");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getTitle() == "Delete") {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			new DeleteProductBin(reviewProductModelList.get(info.position)
					.getProductName()).execute(null, null, null);
			Log.e("reviewProductModelList Delete",
					reviewProductModelList.get(info.position).getProductName());
		} else if (item.getTitle() == "Rename") {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Log.e("reviewProductModelList Rename",
					reviewProductModelList.get(info.position).getProductName());
			InitiateRenamePopUp(reviewProductModelList.get(info.position)
					.getProductName());
		} else {
			return false;
		}
		return super.onContextItemSelected(item);
	}

	private void InitiateRenamePopUp(final String productname) {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(
					R.layout.popup_rename,
					(ViewGroup) getActivity().findViewById(
							R.id.rename_popup_element));
			final PopupWindow pwindops = new PopupWindow(layout,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			pwindops.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pwindops.setFocusable(true);
			pwindops.update();

			ImageView iv_rename_popup_close = (ImageView) layout
					.findViewById(R.id.iv_rename_popup_close);
			final EditText et_rename_filename = (EditText) layout
					.findViewById(R.id.et_rename_filename);
			et_rename_filename.setText(productname);
			et_rename_filename.setSelectAllOnFocus(true);
			Button btn_popup_rename = (Button) layout
					.findViewById(R.id.btn_popup_rename);
			iv_rename_popup_close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pwindops.dismiss();
				}
			});

			btn_popup_rename.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (GlobalMethods.isEmpty(et_rename_filename)) {
						Toast.makeText(getActivity(), "Enter required field",
								Toast.LENGTH_LONG).show();
						pwindops.dismiss();
					} else if (et_rename_filename.getText().toString()
							.equals(productname)) {
						Toast.makeText(getActivity(), "No Change",
								Toast.LENGTH_LONG).show();
						pwindops.dismiss();
					} else {
						pwindops.dismiss();
						new RenameProductBin(productname, et_rename_filename
								.getText().toString())
								.execute(null, null, null);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void InitiatePowerSearchPopUp() {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(
					R.layout.power_search_popup,
					(ViewGroup) getActivity().findViewById(
							R.id.ps_popup_element));
			final PopupWindow pwindops = new PopupWindow(layout,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			pwindops.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pwindops.setFocusable(true);
			pwindops.update();

			TextView iv_ps_popup_text1 = (TextView) layout
					.findViewById(R.id.iv_ps_popup_text1);
			iv_ps_popup_text1.setText("Tier 1 Search");
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
						Toast.makeText(getActivity(),
								"Please enter keyword to search..",
								Toast.LENGTH_LONG).show();
					} else {
						Intent i = new Intent(getActivity(),
								PowerSearchActivity.class);
						i.putExtra("searchTier", 1);
						i.putExtra("searchKeyword", et_ps_keyword.getText()
								.toString());
						i.putExtra("searchClipbinname", "");
						startActivity(i);
						pwindops.dismiss();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initiatePopupWindow() {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(
					R.layout.vmr_screen_popup,
					(ViewGroup) getActivity().findViewById(
							R.id.vmrproduct_popup_element));
			pwindo = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
			pwindo.setFocusable(true);
			pwindo.update();

			iv_vmrproductpopup_close = (ImageView) layout
					.findViewById(R.id.iv_vmrproductpopup_close);

			et_vmrproduct = (EditText) layout.findViewById(R.id.et_vmrproduct);
			spn_vmrclient = (Spinner) layout.findViewById(R.id.spn_vmrproduct);
			et_vmrproduct.requestFocus();
			et_vmrproduct.setFocusable(true);

			ArrayAdapter<String> adpt = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, VMRClientArray);
			spn_vmrclient.setAdapter(adpt);

			btn_popup_create_vmrproduct = (Button) layout
					.findViewById(R.id.btn_popup_create_vmrproduct);
			iv_vmrproductpopup_close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					pwindo.dismiss();
				}
			});

			btn_popup_create_vmrproduct
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (!GlobalMethods.isEmpty(et_vmrproduct)) {
								pwindo.dismiss();
								new CreateProductBin(et_vmrproduct.getText()
										.toString(), spn_vmrclient
										.getSelectedItem().toString()).execute(
										null, null, null);
							} else {
								Toast.makeText(getActivity(),
										"Please Enter Bin Name",
										Toast.LENGTH_LONG).show();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class RenameProductBin extends AsyncTask<String, String, String> {

		private String oldfilename;
		Object resultSoap;
		String resultSoapString = "";
		String sOAPHost = "";
		private String newfilename;

		public RenameProductBin(String oldfilename, String newfilename) {
			// TODO Auto-generated constructor stub
			this.oldfilename = oldfilename;
			this.newfilename = newfilename;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(getActivity(), "Renaming Product Bin..",
					true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String[] attributeslist2 = { "oldValue", "newValue" };
			String[] attributeslist_value = { oldfilename, newfilename };
			sOAPHost = sharedpreferencesPrefPortConnect.getString(
					"VMRConnectHostname", "");

			Object resultDeleteProduct = GlobalMethods
					.SOAPMethodPrimitivewithVariables("RenameProducts",
							attributeslist2, attributeslist_value, sOAPHost);
			if (resultDeleteProduct != null) {
				resultSoap = resultDeleteProduct;
				resultSoapString = resultDeleteProduct.toString();
				Log.e("resultSoapString", resultSoapString);
			} else {
				Log.e("resultSoapString", "Error! Not found!");
				resultSoapString = "Error! Not found!";
			}
			return resultSoapString;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(getActivity());
			new GetAllReviewProducts(sOAPHost).execute(null, null, null);
		}
	}

	class DeleteProductBin extends AsyncTask<String, String, String> {

		private String product_name;
		Object resultSoap;
		String resultSoapString = "";
		String sOAPHost = "";

		public DeleteProductBin(String product_name) {
			// TODO Auto-generated constructor stub
			this.product_name = product_name;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(getActivity(), "Deleting Product Bin..",
					true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String[] attributeslist2 = { "product" };
			String[] attributeslist_value = { product_name };
			sOAPHost = sharedpreferencesPrefPortConnect.getString(
					"VMRConnectHostname", "");

			Object resultDeleteProduct = GlobalMethods
					.SOAPMethodPrimitivewithVariables("DeleteProduct",
							attributeslist2, attributeslist_value, sOAPHost);
			if (resultDeleteProduct != null) {
				resultSoap = resultDeleteProduct;
				resultSoapString = resultDeleteProduct.toString();
				Log.e("resultSoapString", resultSoapString);
			} else {
				Log.e("resultSoapString", "Error! Not found!");
				resultSoapString = "Error! Not found!";
			}
			return resultSoapString;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(getActivity());
			Log.e("result", result);
			new GetAllReviewProducts(sOAPHost).execute(null, null, null);
		}
	}

	class CreateProductBin extends AsyncTask<String, String, String> {

		private String product_name;
		private String client_name;
		Object resultSoap;
		String resultSoapString = "";
		String sOAPHost = "";

		public CreateProductBin(String product_name, String client_name) {
			// TODO Auto-generated constructor stub
			this.product_name = product_name;
			this.client_name = client_name;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(getActivity(), "Creating Product Bin..",
					true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String[] attributeslist2 = { "client", "product" };
			String[] attributeslist_value = { client_name, product_name };
			sOAPHost = sharedpreferencesPrefPortConnect.getString(
					"VMRConnectHostname", "");
			String UserName = sharedpreferences.getString("UserID", "");

			Object resultGetAllReviewProducts = GlobalMethods
					.SOAPMethodPrimitivewithVariables("CreateProductBin",
							attributeslist2, attributeslist_value, sOAPHost);
			if (resultGetAllReviewProducts != null) {

				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();

				long activityinsertid = dbh.VBU_Activities_Insert(UserName,
						"New Directory Created", 1, 1,
						dateFormat.format(cal.getTime()));

				dbh.VBU_ActivityDetails_Insert(
						Integer.parseInt(String.valueOf(activityinsertid)),
						product_name, "", "", 0, 1);

				resultSoap = resultGetAllReviewProducts;
				resultSoapString = resultGetAllReviewProducts.toString();
				Log.e("resultSoapString", resultSoapString);
			} else {
				Log.e("resultSoapString", "Error! Not found!");
				resultSoapString = "Error! Not found!";
			}
			return resultSoapString;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(getActivity());
			Log.e("result", result);
			new GetAllReviewProducts(sOAPHost).execute(null, null, null);
		}
	}

	public void HideKeyboard() {
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
