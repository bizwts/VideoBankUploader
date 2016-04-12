package com.wings.videobankuploader.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wings.videobankuploader.R;
import com.wings.videobankuploader.models.ActivitiesModel;

public class ActivitiesListAdapter extends BaseAdapter {

	private Context context;
	private List<ActivitiesModel> arlstActivitiesModel;
	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

	public ActivitiesListAdapter(Context context,
			List<ActivitiesModel> arlstActivities) {
		this.context = context;
		this.arlstActivitiesModel = arlstActivities;
	}

	@Override
	public int getCount() {
		return arlstActivitiesModel.size();
	}

	@Override
	public Object getItem(int position) {
		return arlstActivitiesModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.activity_list_item, null);
		}
		ImageView activity_listimg = (ImageView) convertView
				.findViewById(R.id.activity_listimg);
		TextView activity_name = (TextView) convertView
				.findViewById(R.id.activity_name);
		TextView activity_message = (TextView) convertView
				.findViewById(R.id.activity_message);
		TextView activity_filename = (TextView) convertView
				.findViewById(R.id.activity_filename);
		TextView activity_transaction = (TextView) convertView
				.findViewById(R.id.activity_transaction);
		TextView activity_date = (TextView) convertView
				.findViewById(R.id.activity_date);

		activity_name.setText(arlstActivitiesModel.get(position).getUsername());

		if (arlstActivitiesModel.get(position).getIsdirectory() == 1) {
			activity_message.setText(arlstActivitiesModel.get(position)
					.getMessage() + " - Local Bin");
		} else {
			activity_message.setText(arlstActivitiesModel.get(position)
					.getMessage() + " - VMR");
		}
		activity_filename.setText(arlstActivitiesModel.get(position)
				.getFilename());

		if (arlstActivitiesModel.get(position).getActivityType() == 6
				|| arlstActivitiesModel.get(position).getActivityType() == 7
				|| arlstActivitiesModel.get(position).getActivityType() == 8
				|| arlstActivitiesModel.get(position).getActivityType() == 9
				|| arlstActivitiesModel.get(position).getActivityType() == 12
				|| arlstActivitiesModel.get(position).getActivityType() == 13) {
			activity_transaction.setVisibility(View.VISIBLE);

			if (arlstActivitiesModel.get(position).getFilePath().contains("/")
					&& arlstActivitiesModel.get(position).getDestPath()
							.contains("/")) {
				String sourcefilename = arlstActivitiesModel
						.get(position)
						.getFilePath()
						.substring(
								arlstActivitiesModel.get(position)
										.getFilePath().lastIndexOf("/"));
				String sourcedisplay = arlstActivitiesModel.get(position)
						.getFilePath().replace(sourcefilename, "");

				String destfilename = arlstActivitiesModel
						.get(position)
						.getDestPath()
						.substring(
								arlstActivitiesModel.get(position)
										.getDestPath().lastIndexOf("/"));
				String destdisplay = arlstActivitiesModel.get(position)
						.getDestPath().replace(destfilename, "");
				activity_transaction.setText(sourcedisplay + " -> "
						+ destdisplay);
			} else if (arlstActivitiesModel.get(position).getFilePath()
					.contains("/")
					&& !arlstActivitiesModel.get(position).getDestPath()
							.contains("/")) {
				String sourcefilename = arlstActivitiesModel
						.get(position)
						.getFilePath()
						.substring(
								arlstActivitiesModel.get(position)
										.getFilePath().lastIndexOf("/"));
				String sourcedisplay = arlstActivitiesModel.get(position)
						.getFilePath().replace(sourcefilename, "");

				activity_transaction.setText(sourcedisplay + " -> "
						+ arlstActivitiesModel.get(position).getDestPath());
			} else if (!arlstActivitiesModel.get(position).getFilePath()
					.contains("/")
					&& arlstActivitiesModel.get(position).getDestPath()
							.contains("/")) {
				String destfilename = arlstActivitiesModel
						.get(position)
						.getDestPath()
						.substring(
								arlstActivitiesModel.get(position)
										.getDestPath().lastIndexOf("/"));
				String destdisplay = arlstActivitiesModel.get(position)
						.getDestPath().replace(destfilename, "");

				activity_transaction.setText(arlstActivitiesModel.get(position)
						.getFilePath() + " -> " + destdisplay);
			} else {
				activity_transaction.setText(arlstActivitiesModel.get(position)
						.getFilePath()
						+ " -> "
						+ arlstActivitiesModel.get(position).getDestPath());
			}

		} else {
			activity_transaction.setVisibility(View.GONE);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		formatter.setLenient(false);

		Date oldDate;
		try {
			oldDate = formatter.parse(arlstActivitiesModel.get(position)
					.getCreateddate());
			long oldMillis = oldDate.getTime();

			activity_date.setText(getTimeAgo(oldMillis, context));

			// Log.e("Time Ago", getTimeAgo(oldMillis, context));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return convertView;
	}

	public static String getTimeAgo(long time, Context ctx) {
		if (time < 1000000000000L) {
			// if timestamp given in seconds, convert to millis
			time *= 1000;
		}

		Date curDate = new Date();
		long curMillis = curDate.getTime();

		long now = curMillis;
		if (time > now || time <= 0) {
			return null;
		}

		// TODO: localize
		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return "just now";
		} else if (diff < 2 * MINUTE_MILLIS) {
			return "a minute ago";
		} else if (diff < 50 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + " minutes ago";
		} else if (diff < 90 * MINUTE_MILLIS) {
			return "an hour ago";
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + " hours ago";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "yesterday";
		} else {
			return diff / DAY_MILLIS + " days ago";
		}
	}
}
