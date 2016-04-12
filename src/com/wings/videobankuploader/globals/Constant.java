package com.wings.videobankuploader.globals;

public class Constant {

	public static final String PrefName = "videobankpref";
	public static final String PrefPortConnect = "videobankportconnectpref";

	public static final String DefaultSOAP_Host = "Your Server URL";

	public static final String NAMESPACE = "";
	public static final String SOAP_ACTION_URL = "urn:IServiceFactorySilverlight/";
	public static final String SOAP_URL = "/ServiceFactory?wsdl=wsdl0";

	public static final String MEDIA_NAMESPACE = "http://tempuri.org/";
	public static final String MEDIA_SOAP_ACTION_URL = "http://tempuri.org/IPlayerService/";
	public static final String MEDIA_SOAP_URL = "/MediaPlayerService/PlayerService.svc";

	public static final String[] Activity_Types = {
	/* 0 */"New Local Directory Created", /* 1 */"New VMR Directory Created",
	/* 2 */"Local Directory Edited", /* 3 */"VMR Directory Edited",
	/* 4 */"Local Directory Removed", /* 5 */"VMR Directory Removed",
	/* 6 */"Local Files Copied",/* 7 */"VMR Files Copied",
	/* 8 */"Local Files Moved",/* 9 */"VMR Files Moved",
	/* 10 */"Local Files Removed",/* 11 */"VMR Files Removed",
	/* 12 */"Files Uploaded",/* 13 */"Files Downloaded",
	/* 14 */"Profile Updated" };
}
