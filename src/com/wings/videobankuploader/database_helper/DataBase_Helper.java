package com.wings.videobankuploader.database_helper;

import java.util.ArrayList;
import java.util.List;

import com.wings.videobankuploader.models.ActivitiesModel;
import com.wings.videobankuploader.models.HostHistoryModel;
import com.wings.videobankuploader.models.LocalBinModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase_Helper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	public static final String DATABASE_NAME = "DB_VBUFolder";
	// Table Names
	private static final String TABLE_VBUFolder = "VBU_Folder";
	private static final String TABLE_VBUFiles = "VBU_Files";
	private static final String TABLE_VBUHostHistory = "VBU_Host_History";
	private static final String TABLE_Activities = "VBU_Activities";
	private static final String TABLE_ActivityDetails = "VBU_ActivityDetails";
	private static final String TABLE_LocalVideoThumbs = "VBU_videothumbs";

	public DataBase_Helper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// TODO Auto-generated method stub
		String CREATE_TABLE_VBUFolder = "CREATE TABLE "
				+ TABLE_VBUFolder
				+ "(VF_Id INTEGER PRIMARY KEY AUTOINCREMENT, VFName TEXT, VFImageCount INTEGER,VFVideoCount INTEGER,VFDocumentCount INTEGER,VFStatus INTEGER, VFDate TEXT)";
		db.execSQL(CREATE_TABLE_VBUFolder);

		String CREATE_TABLE_VBUFiles = "CREATE TABLE "
				+ TABLE_VBUFiles
				+ "(VFillId INTEGER PRIMARY KEY AUTOINCREMENT,VFID INTEGER, VFillName TEXT, VFileDwnlStatus INTEGER,VFillType INTEGER)";
		db.execSQL(CREATE_TABLE_VBUFiles);

		String CREATE_TABLE_VBUHostHistory = "CREATE TABLE "
				+ TABLE_VBUHostHistory
				+ "(VHostID INTEGER PRIMARY KEY AUTOINCREMENT,VProtocol TEXT,VHostName TEXT, VPortNumber TEXT)";
		db.execSQL(CREATE_TABLE_VBUHostHistory);

		String CREATE_TABLE_Activities = "CREATE TABLE "
				+ TABLE_Activities
				+ "(VActivityID INTEGER PRIMARY KEY AUTOINCREMENT,VActUsername TEXT,VActMessage TEXT, VActivityType Integer,VActStatus Integer,VActCreatedDate TEXT)";
		db.execSQL(CREATE_TABLE_Activities);

		String CREATE_TABLE_ActivityDetails = "CREATE TABLE "
				+ TABLE_ActivityDetails
				+ "(VActivityDetailID INTEGER PRIMARY KEY AUTOINCREMENT,VActivityID INTEGER,VActdetFilename TEXT,VActdetFilepath TEXT, VActdetDestpath TEXT,VActisDirectory Integer,VActisVMR Integer)";
		db.execSQL(CREATE_TABLE_ActivityDetails);

		String CREATE_TABLE_LocalVideoThumbs = "CREATE TABLE "
				+ TABLE_LocalVideoThumbs
				+ "(VvideothumbID INTEGER PRIMARY KEY AUTOINCREMENT,Vvideothumbname TEXT,Vvideothumbimage BLOB)";
		db.execSQL(CREATE_TABLE_LocalVideoThumbs);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String VbuFolder = "DROP TABLE IF EXISTS " + TABLE_VBUFolder;
		db.execSQL(VbuFolder);

		String VbuFiles = "DROP TABLE IF EXISTS " + TABLE_VBUFiles;
		db.execSQL(VbuFiles);

		String VbuHostHistory = "DROP TABLE IF EXISTS " + TABLE_VBUHostHistory;
		db.execSQL(VbuHostHistory);

		String VbuActivities = "DROP TABLE IF EXISTS " + TABLE_Activities;
		db.execSQL(VbuActivities);

		String VbuActivityDetails = "DROP TABLE IF EXISTS "
				+ TABLE_ActivityDetails;
		db.execSQL(VbuActivityDetails);

		String VbuVideoThumbs = "DROP TABLE IF EXISTS "
				+ TABLE_LocalVideoThumbs;
		db.execSQL(VbuVideoThumbs);

		onCreate(db);
	}

	// -----------------VBU_videothumbs Table----------------------
	public void addVideoThumb(String name, byte[] image) throws SQLiteException {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		Log.e("addThumb", name);
		cv.put("Vvideothumbname", name);
		cv.put("Vvideothumbimage", image);
		db.insert(TABLE_LocalVideoThumbs, null, cv);
	}

	// -----------------VBUFOLDER Table----------------------
	public void VBU_Folder_Insert(String name, int imgcount, int videocount,
			int doccount, int status, String date) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VFName", name);
		values.put("VFImageCount", imgcount);
		values.put("VFVideoCount", videocount);
		values.put("VFDocumentCount", doccount);
		values.put("VFStatus", status);
		values.put("VFDate", date);

		Log.d("Inserted", "Inserted folder");
		// insert row
		db.insert(TABLE_VBUFolder, null, values);
		db.close();
	}

	// -----------------VBUFILLS Table----------------------
	public void VBU_Files_Insert(int vfid, String name, int donloadstatus,
			int filetype) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VFID", vfid);
		values.put("VFillName", name);
		values.put("VFileDwnlStatus", donloadstatus);
		values.put("VFillType", filetype);

		// insert row
		db.insert(TABLE_VBUFiles, null, values);

		db.close();
	}

	// -----------------VBUHostHistory Table----------------------
	public void VBU_HostHistory_Insert(String protocol, String hostname,
			String portnumber) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VProtocol", protocol);
		values.put("VHostName", hostname);
		values.put("VPortNumber", portnumber);

		// insert row
		db.insert(TABLE_VBUHostHistory, null, values);
		db.close();
	}

	// -----------------VBU_Activities Table----------------------
	public long VBU_Activities_Insert(String username, String message,
			Integer activitytype, Integer status, String createdDate) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VActUsername", username);
		values.put("VActMessage", message);
		values.put("VActivityType", activitytype);
		values.put("VActStatus", status);
		values.put("VActCreatedDate", createdDate);

		// db.insert(TABLE_Activities, null, values);

		return db.insert(TABLE_Activities, null, values);
	}

	// -----------------VBU_ActivityDetails Table----------------------
	public void VBU_ActivityDetails_Insert(Integer activityId, String filename,
			String filepath, String destpath, Integer isdirectory, Integer isvmr) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VActivityID", activityId);
		values.put("VActdetFilename", filename);
		values.put("VActdetFilepath", filepath);
		values.put("VActdetDestpath", destpath);
		values.put("VActisDirectory", isdirectory);
		values.put("VActisVMR", isvmr);

		db.insert(TABLE_ActivityDetails, null, values);
		db.close();
	}

	// Getting All PAst Host History
	public List<HostHistoryModel> getAllHostHistory() {
		List<HostHistoryModel> hostList = new ArrayList<HostHistoryModel>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_VBUHostHistory;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				HostHistoryModel host = new HostHistoryModel();
				host.setHostID(Integer.parseInt(cursor.getString(0)));
				host.setProtocol(cursor.getString(1));
				host.setHostName(cursor.getString(2));
				host.setPortNumber(cursor.getString(3));
				// Adding contact to list
				hostList.add(host);
			} while (cursor.moveToNext());
		}
		// return contact list
		return hostList;
	}

	// Getting All PAst Host History
	public List<ActivitiesModel> getAllActivities() {
		List<ActivitiesModel> ActivitiesList = new ArrayList<ActivitiesModel>();
		// Select All Query
		String selectQuery = "SELECT  v.*,vd.* FROM VBU_Activities AS v,VBU_ActivityDetails AS vd WHERE v.VActivityID = vd.VActivityID ORDER BY vd.VActivityDetailID DESC";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		cursor.moveToFirst();
		Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));

		if (cursor.moveToFirst()) {
			do {
				ActivitiesModel Activity = new ActivitiesModel();
				Activity.setActDetailID(cursor.getInt(cursor
						.getColumnIndex("VActivityDetailID")));
				Activity.setActivityID(cursor.getInt(cursor
						.getColumnIndex("VActivityID")));
				Activity.setUsername(cursor.getString(cursor
						.getColumnIndex("VActUsername")));
				Activity.setMessage(cursor.getString(cursor
						.getColumnIndex("VActMessage")));
				Activity.setActivityType(cursor.getInt(cursor
						.getColumnIndex("VActivityType")));
				Activity.setStatus(cursor.getInt(cursor
						.getColumnIndex("VActStatus")));
				Activity.setCreateddate(cursor.getString(cursor
						.getColumnIndex("VActCreatedDate")));
				Activity.setFilename(cursor.getString(cursor
						.getColumnIndex("VActdetFilename")));
				Activity.setFilePath(cursor.getString(cursor
						.getColumnIndex("VActdetFilepath")));
				Activity.setDestPath(cursor.getString(cursor
						.getColumnIndex("VActdetDestpath")));
				Activity.setIsdirectory(cursor.getInt(cursor
						.getColumnIndex("VActisDirectory")));
				Activity.setIsVMR(cursor.getInt(cursor
						.getColumnIndex("VActisVMR")));
				ActivitiesList.add(Activity);
			} while (cursor.moveToNext());
		}
		return ActivitiesList;
	}

	public boolean CheckIsDataAlreadyInDBorNot(String filename, int activitytype) {
		SQLiteDatabase sqldb = this.getWritableDatabase();
		String Query = "SELECT  v.*,vd.* FROM VBU_Activities AS v,VBU_ActivityDetails AS vd WHERE v.VActivityID = vd.VActivityID AND vd.VActdetFilename ='"
				+ filename + "' AND v.VActivityType=" + activitytype + "";
		Cursor cursor = sqldb.rawQuery(Query, null);
		if (cursor.getCount() <= 0) {
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	public HostHistoryModel getLastInsertedHistory() {
		// Select All Query
		String selectQuery = "SELECT * FROM VBU_Host_History ORDER BY VHostID DESC LIMIT 1";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		HostHistoryModel hostListModel;
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			hostListModel = new HostHistoryModel(cursor.getString(1),
					cursor.getString(2), cursor.getString(3));
		} else {
			hostListModel = new HostHistoryModel("", "", "");
		}

		// return contact list
		return hostListModel;
	}

	public int SelectByHostHistory(String protocol, String hostname,
			String portnumber) {
		int host_id;
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT VHostID FROM VBU_Host_History WHERE VProtocol ='"
				+ protocol
				+ "' AND VHostName='"
				+ hostname
				+ "' AND VPortNumber='" + portnumber + "'";

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			host_id = c.getInt(c.getColumnIndex("VHostID"));
			c.close();
		} else {
			host_id = 0;
		}
		return host_id;
	}

	public byte[] Select_ThumbImage(String videoName) {
		SQLiteDatabase db = this.getReadableDatabase();

		Log.e("SelectThumb", videoName);

		String selectQuery = "SELECT * FROM VBU_videothumbs WHERE Vvideothumbname ='"
				+ videoName + "'";

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		byte[] image = c.getBlob(2);

		return image;
	}

	public LocalBinModel Folder_SelectByFolderName(String folder_name) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM VBU_Folder WHERE VFName ='"
				+ folder_name + "'";

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		LocalBinModel td = new LocalBinModel();

		td.setItem_id(c.getInt(c.getColumnIndex("VF_Id")));
		td.setItem_name(c.getString(c.getColumnIndex("VFName")));
		td.setImageCount(c.getInt(c.getColumnIndex("VFImageCount")));
		td.setVideoCount(c.getInt(c.getColumnIndex("VFVideoCount")));
		td.setDocumentCount(c.getInt(c.getColumnIndex("VFDocumentCount")));
		td.setStatus(c.getInt(c.getColumnIndex("VFStatus")));
		td.setDate(c.getString(c.getColumnIndex("VFDate")));

		c.close();
		return td;

	}

	public int File_SelectByFileName(String file_name) {
		int file_id;
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT VFillId FROM VBU_Files WHERE VFillName ='"
				+ file_name + "'";

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			file_id = c.getInt(c.getColumnIndex("VFillId"));
			c.close();
		} else {
			file_id = 0;
		}
		return file_id;
	}

	public int CheckFolderExist(String folder_name) {
		int folder_id;
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT VF_Id FROM VBU_Folder WHERE VFName ='"
				+ folder_name + "'";

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			folder_id = c.getInt(c.getColumnIndex("VF_Id"));
			c.close();
		} else {
			folder_id = 0;
		}
		return folder_id;
	}

	public void VBU_Delete_File(String vfid) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_VBUFiles, "VFillId" + " = ?", new String[] { vfid });
		Log.e("deleting file id", vfid);

		db.close();
	}

	public void VBU_Delete_HostHistory(String protocol, String hostname,
			String portnumber) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_VBUHostHistory, "VProtocol = ? AND "
				+ "VHostName =? AND " + "VPortNumber =?", new String[] {
				protocol, hostname, portnumber });

		db.close();
	}

	public int Update_Imagecount(LocalBinModel gsc) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VFImageCount", gsc.getImageCount());

		// updating row
		return db.update(TABLE_VBUFolder, values, "VF_Id" + " = ?",
				new String[] { String.valueOf(gsc.getItem_id()) });
	}

	public int Update_Videocount(LocalBinModel gsc) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VFVideoCount", gsc.getVideoCount());

		// updating row
		return db.update(TABLE_VBUFolder, values, "VF_Id" + " = ?",
				new String[] { String.valueOf(gsc.getItem_id()) });
	}

	public int Update_Doccount(LocalBinModel gsc) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VFDocumentCount", gsc.getDocumentCount());

		// updating row
		return db.update(TABLE_VBUFolder, values, "VF_Id" + " = ?",
				new String[] { String.valueOf(gsc.getItem_id()) });
	}

	public int UpdateLocalFolderName(LocalBinModel gsc) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("VFName", gsc.getItem_name());

		// updating row
		return db.update(TABLE_VBUFolder, values, "VF_Id" + " = ?",
				new String[] { String.valueOf(gsc.getItem_id()) });
	}

	public void deleteLocalFolderwithFiles(LocalBinModel localmodel) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VBUFolder, "VF_Id" + " = ?",
				new String[] { String.valueOf(localmodel.getItem_id()) });
		db.delete(TABLE_VBUFiles, "VFID" + " = ?",
				new String[] { String.valueOf(localmodel.getItem_id()) });
		db.close();
	}

	public ArrayList<LocalBinModel> Client_Select() {
		ArrayList<LocalBinModel> arraycd = new ArrayList<LocalBinModel>();
		String selectQuery = "SELECT  * FROM " + TABLE_VBUFolder;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				LocalBinModel td = new LocalBinModel();

				td.setItem_id(c.getInt(c.getColumnIndex("VF_Id")));
				td.setItem_name(c.getString(c.getColumnIndex("VFName")));
				td.setImageCount(c.getInt(c.getColumnIndex("VFImageCount")));
				td.setVideoCount(c.getInt(c.getColumnIndex("VFVideoCount")));
				td.setDocumentCount(c.getInt(c
						.getColumnIndex("VFDocumentCount")));
				td.setStatus(c.getInt(c.getColumnIndex("VFStatus")));
				td.setDate(c.getString(c.getColumnIndex("VFDate")));

				// adding to todo list
				arraycd.add(td);
			} while (c.moveToNext());
		}
		c.close();
		return arraycd;
	}
}