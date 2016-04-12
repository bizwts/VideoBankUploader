package com.wings.videobankuploader.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.ksoap2.serialization.SoapObject;
import org.lcsky.SVProgressHUD;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wings.videobankuploader.MainActivity;
import com.wings.videobankuploader.R;
import com.wings.videobankuploader.database_helper.DataBase_Helper;
import com.wings.videobankuploader.globals.Constant;
import com.wings.videobankuploader.globals.GlobalMethods;
import com.wings.videobankuploader.services.MyService;

public class Fragment_Login extends Fragment {

	private EditText et_login_username;
	private EditText et_login_password;
	private Button buttonlogin;
	private SharedPreferences sharedpreferences;
	ArrayList<String> arlstPermissions = new ArrayList<String>();
	private SharedPreferences sharedpreferencesPrefPortConnect;
	private TextView textViewdate;
	private String version;
	private TextView textViewversion;
	DataBase_Helper dbh;

	public Fragment_Login() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_login, container,
				false);
		dbh = new DataBase_Helper(getActivity().getApplicationContext());

		sharedpreferences = getActivity().getSharedPreferences(
				Constant.PrefName, 0);
		sharedpreferencesPrefPortConnect = getActivity().getSharedPreferences(
				Constant.PrefPortConnect, 0);

		et_login_username = (EditText) rootView
				.findViewById(R.id.et_login_username);
		et_login_password = (EditText) rootView
				.findViewById(R.id.et_login_password);
		buttonlogin = (Button) rootView.findViewById(R.id.buttonlogin);
		et_login_username.setCompoundDrawables(GlobalMethods
				.setCompoundDrawable(getActivity(), R.drawable.login_username),
				null, null, null);
		et_login_password.setCompoundDrawables(GlobalMethods
				.setCompoundDrawable(getActivity(), R.drawable.login_password),
				null, null, null);

		textViewversion = (TextView) rootView
				.findViewById(R.id.textViewversion);
		textViewdate = (TextView) rootView.findViewById(R.id.textViewdate);
		Calendar c = Calendar.getInstance();

		buttonlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new LoginUser(et_login_username.getText().toString(),
						et_login_password.getText().toString()).execute(null,
						null, null);
			}
		});

		return rootView;
	}

	class LoginUser extends AsyncTask<String, String, String> {

		private String username;
		private String user_password;
		SoapObject resultSoap;
		private String base64bytearray;
		private InputStream is;
		private String ConnectHostname;

		public LoginUser(String username, String user_password) {
			// TODO Auto-generated constructor stub
			this.username = username;
			this.user_password = user_password;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			SVProgressHUD.showInView(getActivity(),
					"Logging in.. Please wait..", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String resp = "";
			// TODO Auto-generated method stub

			if (!sharedpreferencesPrefPortConnect.getString(
					"VMRConnectHostname", "").equals("")) {
				ConnectHostname = sharedpreferencesPrefPortConnect.getString(
						"VMRConnectHostname", "");
			}

			String[] attributeslist1 = { "username", "password" };
			String[] attributeslist_value1 = { username, user_password };

			SoapObject resultLoginMobile = GlobalMethods.SOAPMethod(
					"LoginMobile", attributeslist1, attributeslist_value1,
					ConnectHostname);
			if (resultLoginMobile != null) {
				Log.e("result from LoginMobile", resultLoginMobile.toString());
				resultSoap = resultLoginMobile;
				resp = resultLoginMobile.toString();
				if (resultSoap.getProperty("Photo") != null) {
					byte[] bytearray = null;
					try {
						URL url = new URL(ConnectHostname + "/ServiceFactory/"
								+ resultSoap.getProperty("Photo").toString());
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();
						connection.setDoInput(true);
						connection.connect();
						is = connection.getInputStream();
						if (ConnectHostname + "/ServiceFactory/"
								+ resultSoap.getProperty("Photo").toString() != null)
							try {
								bytearray = streamToBytes(is);
							} finally {
								is.close();
							}
					} catch (Exception e) {
					}

					if (bytearray != null) {
						base64bytearray = Base64.encodeToString(bytearray,
								Base64.DEFAULT);
					} else {
						base64bytearray = GlobalMethods
								.NoMediaByte64(getActivity());
					}
				} else {
					base64bytearray = GlobalMethods
							.NoMediaByte64(getActivity());
				}
			} else {
				resp = "101";
			}

			return resp;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			SVProgressHUD.dismiss(getActivity());
			if (result != null && !result.equals("101")
					&& !result.equals("104") && !result.equals("105")
					&& !result.equals("")) {

				getActivity().startService(
						new Intent(getActivity(), MyService.class));
				SharedPreferences.Editor editor = sharedpreferences.edit();
				Log.e("resultSoap", resultSoap.toString());

				if (resultSoap.getProperty("UsernameID") != null) {
					editor.putString("UsernamesID",
							resultSoap.getProperty("UsernameID").toString());
				} else {
					editor.putString("UsernamesID", "null");
				}

				if (resultSoap.getProperty("UserID") != null) {
					editor.putString("UserID", resultSoap.getProperty("UserID")
							.toString());
				} else {
					editor.putString("UserID", "null");
				}

				if (base64bytearray != null && !base64bytearray.equals("")) {
					editor.putString("UserPhoto", base64bytearray);
				} else {
					editor.putString("UserPhoto", "null");
				}

				if (resultSoap.getProperty("MobileOnline") != null) {
					editor.putString("MobileOnline",
							resultSoap.getProperty("MobileOnline").toString());
				} else {
					editor.putString("MobileOnline", "null");
				}

				if (resultSoap.getProperty("LastName") != null) {
					editor.putString("UserLastName",
							resultSoap.getProperty("LastName").toString());
				} else {
					editor.putString("UserLastName", "null");
				}

				if (resultSoap.getProperty("FirstName") != null) {
					editor.putString("UserFirstName",
							resultSoap.getProperty("FirstName").toString());
				} else {
					editor.putString("UserFirstName", "null");
				}
				if (resultSoap.getProperty("EmailAddress") != null) {
					editor.putString("UserEmailAddress", resultSoap
							.getProperty("EmailAddress").toString());
				} else {
					editor.putString("UserEmailAddress", "null");
				}

				if (resultSoap.getProperty("ContactNumber") != null) {
					editor.putString("UserContactNumber", resultSoap
							.getProperty("ContactNumber").toString());
				} else {
					editor.putString("UserContactNumber", "null");
				}

				Object propertyAccessPermission = resultSoap
						.getProperty("AccessPermission");

				if (((SoapObject) propertyAccessPermission).getPropertyCount() > 0) {
					editor.putInt("UserAccessPermissionCount",
							((SoapObject) propertyAccessPermission)
									.getPropertyCount());
					for (int i = 0; i < ((SoapObject) propertyAccessPermission)
							.getPropertyCount(); i++) {
						Object property = ((SoapObject) propertyAccessPermission)
								.getProperty(i);
						arlstPermissions.add(property.toString());
						editor.putString("UserAccessPermission" + i,
								property.toString());

						Log.e("UserAccessPermission" + i, property.toString());
					}
				} else {
					editor.putInt("UserAccessPermissionCount", 0);
				}

				editor.putString("UserAccessDescription", resultSoap
						.getProperty("AccessDescription").toString());
				editor.putString("UserAccessLevel",
						resultSoap.getProperty("AccessLevel").toString());
				editor.putString("UserAccessLevelID",
						resultSoap.getProperty("AccessLevelID").toString());
				editor.commit();

				Intent i = new Intent(getActivity(), MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				getActivity().finish();

			} else if (result.equals("101")) {
				Toast.makeText(getActivity(),
						"Login Error.. Invalid Username or Password!",
						Toast.LENGTH_LONG).show();
			} else if (result.equals("104")) {
				Toast.makeText(getActivity(),
						"Authentication Error.. CSA False!!", Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(getActivity(), "Failed to Login User!!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
		}
		return os.toByteArray();
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
}
