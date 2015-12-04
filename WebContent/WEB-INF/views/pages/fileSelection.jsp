<%@ page import="java.io.File,java.io.FilenameFilter,org.json.JSONArray"%>


<%
	File dir = new File(request.getParameter("dir"));
	if(!dir.exists()) {
		dir = new File(File.listRoots()[0], dir.getAbsolutePath());
	}
	String[] fileNames = dir.list(new FilenameFilter() {
	    public boolean accept(File dir, String name) {
			return name.endsWith(".apk");
	    }
	});
	JSONArray arrAll = new JSONArray();
	for (String name : fileNames) {
		File file = new File(name);
		JSONArray arr = new JSONArray();
		arr.put(file.getName());
		arr.put(file.length());
		arr.put(file.getAbsolutePath());
		arr.put("Version dummy");
		arr.put("<div class=\"ratio\"><label><input type=\"checkbox\" value=\"\"></label></div>");
		
		arrAll.put(arr);
	}
	out.print(arrAll);
%>