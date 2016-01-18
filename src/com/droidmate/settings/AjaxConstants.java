package com.droidmate.settings;

public class AjaxConstants {

	// get requests
	public final static String APKPathHandeler_GET_INFORMATION = "info[]";
	public final static String APKPathHandeler_GET_INFORMATION_SELECTEDAPKS = "selApks";
	public final static String APKPathHandeler_GET_INFORMATION_APKROOT = "apkRoot";
	public final static String APKPathHandeler_GET_INFORMATION_APKS = "apks";

	// save requests
	public final static String APKPathHandler_SAVE_APKROOT = "apkRoot";
	public static final String APKPathHandler_SAVE_SELECTED_APKS = "selApks[]";

	// -----------------------------------------------------------------------------

	// get requests
	public final static String APKInlineHandler_GET_INLINE_STATUS = "status";

	// save requests
	public final static String APKInlineHandler_SAVE_INLINE = "inline";

	// -----------------------------------------------------------------------------

	// get requests
	public final static String FileSystem_GET_FILETYPE = "type";
	public final static String FileSystem_GET_PATH = "path";
	
	// -----------------------------------------------------------------------------
	
	// explore requests
	public final static String EXPLORE_START = "explore_start";
	public final static String EXPLORE_STOP = "explore_stop";
	public final static String EXPLORE_RESTART = "explore_restart";
	public final static String EXPLORE_GET_INFO = "explore_get_info";
	public final static String EXPLORE_GET_INFO_APK_NAME = "explore_get_info_apkname";
	public final static String EXPLORE_GET_GLOBAL_ELEMENTS_SEEN = "explore_get_global_elements_seen";
	public static final String EXPLORE_GET_GLOBAL_ELEMENTS_SEEN_HISTORY = "explore_get_global_elements_seen_history";
	public final static String EXPLORE_GET_GLOBAL_SCREENS_SEEN = "explore_get_global_screens_seen";
	public static final String EXPLORE_GET_GLOBAL_SCREENS_SEEN_HISTORY = "explore_get_global_screens_seen_history";
	public static final String EXPLORE_OPEN_REPORT_FOLDER = "explore_open_report_folder";

	//------------------------------------------------------------------------------
	public final static String ReportProvider_GET_REPORT = "get_report";
	public static final String ReportProvider_SAVE_REPORT = "save_report";
}
