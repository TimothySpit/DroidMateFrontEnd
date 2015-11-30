package webFrontEnd;

import java.util.Optional;
import org.apache.catalina.startup.Tomcat;

public class Main {

	public static final Optional<String> port = Optional.ofNullable(System.getenv("PORT"));

	// Start the Tomcat server
	public static void main(String[] args) throws Exception {
		String contextPath = "/";
		String appBase = ".";
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(Integer.valueOf(port.orElse("8080")));
		tomcat.getHost().setAppBase(appBase);
		tomcat.addWebapp(contextPath, appBase);
		tomcat.start();
		tomcat.getServer().await();
	}
}