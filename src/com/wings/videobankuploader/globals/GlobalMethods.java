package com.wings.videobankuploader.globals;

import java.io.ByteArrayOutputStream;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.wings.videobankuploader.R;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

public class GlobalMethods {
	static LocationListener locationListener;

	public static final Drawable setCompoundDrawable(Activity activity,
			int drawable) {
		Drawable img = activity.getResources().getDrawable(drawable);
		img.setBounds(0, 0, 60, 60);
		return img;
	}

	public static boolean isEmpty(EditText etText) {
		if (etText.getText().toString().trim().length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
			boolean filter) {
		float ratio = Math.min((float) maxImageSize / realImage.getWidth(),
				(float) maxImageSize / realImage.getHeight());
		int width = Math.round((float) ratio * realImage.getWidth());
		int height = Math.round((float) ratio * realImage.getHeight());

		Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height,
				filter);
		return newBitmap;
	}

	public static byte[] ConvertBitmaptoByte(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

	public static Bitmap ConvertBytetoBitmap(byte[] image) {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}

	public static String NoMediaByte64(Activity activity) {
		String base64bytearray = "";

		Resources resources = activity.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(resources,
				R.drawable.no_media);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] image = stream.toByteArray();
		base64bytearray = Base64.encodeToString(image, Base64.DEFAULT);

		return base64bytearray;
	}

	public static final Drawable setswipeytabsCompoundDrawable(
			Activity activity, int drawable) {
		Drawable img = activity.getResources().getDrawable(drawable);
		img.setBounds(0, 0, 50, 50);
		return img;
	}

	public static SoapObject MediaSOAPMethod(String method_Name,
			String[] listattribute, String[] listattribute_value,
			String SOAPHost) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject(Constant.MEDIA_NAMESPACE,
					method_Name);

			for (int i = 0; i < listattribute.length; i++) {
				request.addProperty(listattribute[i], listattribute_value[i]);
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			Log.e("MEDIA_SOAP_URL", SOAPHost + Constant.MEDIA_SOAP_URL);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAPHost
					+ Constant.MEDIA_SOAP_URL);

			androidHttpTransport.call(Constant.MEDIA_SOAP_ACTION_URL
					+ method_Name, envelope);
			result = (SoapObject) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static SoapObject RecordSOAPMethod(String method_Name,
			String[] listattribute, Integer[] listattribute_value,
			String SOAPHost) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject(Constant.RECORD_NAMESPACE,
					method_Name);

			for (int i = 0; i < listattribute.length; i++) {
				request.addProperty(listattribute[i], listattribute_value[i]);
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			Log.e("RECORD_SOAP_URL", SOAPHost + Constant.RECORD_SOAP_URL);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAPHost
					+ Constant.RECORD_SOAP_URL);

			androidHttpTransport.call(Constant.RECORD_SOAP_ACTION_URL
					+ method_Name, envelope);
			result = (SoapObject) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static SoapObject SOAPMethod(String method_Name,
			String[] listattribute, String[] listattribute_value,
			String SOAPHost) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject(Constant.NAMESPACE, method_Name);

			for (int i = 0; i < listattribute.length; i++) {
				request.addProperty(listattribute[i], listattribute_value[i]);
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			Log.e("SOAP URL", SOAPHost + Constant.SOAP_URL);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAPHost
					+ Constant.SOAP_URL);

			androidHttpTransport.call(Constant.SOAP_ACTION_URL + method_Name,
					envelope);
			result = (SoapObject) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Object SOAPMethodPrimitive(String method_Name, String SOAPHost) {
		Object result = null;
		try {
			SoapObject request = new SoapObject(Constant.NAMESPACE, method_Name);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAPHost
					+ Constant.SOAP_URL);

			androidHttpTransport.call(Constant.SOAP_ACTION_URL + method_Name,
					envelope);
			result = (Object) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Object SOAPMethodPrimitivewithVariables(String method_Name,
			String[] listattribute, String[] listattribute_value,
			String SOAPHost) {
		Object result = null;
		try {
			SoapObject request = new SoapObject(Constant.NAMESPACE, method_Name);

			for (int i = 0; i < listattribute.length; i++) {
				request.addProperty(listattribute[i], listattribute_value[i]);
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			Log.e("SOAP URL", SOAPHost + Constant.SOAP_URL);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAPHost
					+ Constant.SOAP_URL);

			androidHttpTransport.call(Constant.SOAP_ACTION_URL + method_Name,
					envelope);
			result = (Object) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
