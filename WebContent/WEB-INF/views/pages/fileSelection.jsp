<%@ page import="java.io.File,java.io.FilenameFilter,org.json.JSONArray,org.json.JSONObject,java.util.Random,java.util.List,java.util.LinkedList"%>


<%
	File dir = new File(request.getParameter("dir"));
	if(!dir.exists()) {
		System.out.println(File.listRoots()[0]);
		dir = new File(File.listRoots()[0], dir.getAbsolutePath());
	}
	File[] files = dir.listFiles(new FilenameFilter() {
	    public boolean accept(File dir, String name) {
			return name.endsWith(".apk");
	    }
	});
	JSONArray arrAll = new JSONArray();
	List<JSONObject> jsonFiles = new LinkedList<>();
	for (File file : files) {
		JSONArray arr = new JSONArray();
		arr.put(file.getName());
		arr.put(file.length());
		arr.put(file.getAbsolutePath());
		arr.put("Version dummy");
		arr.put("<div class=\"ratio\"><label><input type=\"checkbox\" value=\"\"></label></div>");
				
		JSONObject jsonFile = new JSONObject();
		jsonFile.put("name", file.getName());
		jsonFile.put("size", Long.toString(file.length()));
		jsonFile.put("package", file.getAbsolutePath());
		jsonFile.put("version", Long.toString((new Random()).nextInt(10)));
		jsonFiles.add(jsonFile);	
		
		arrAll.put(arr);
	}
	session.setAttribute("files", jsonFiles);
	out.print(arrAll);
%>