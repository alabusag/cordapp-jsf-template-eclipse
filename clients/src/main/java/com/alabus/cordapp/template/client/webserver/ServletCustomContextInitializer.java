package com.alabus.cordapp.template.client.webserver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.web.servlet.ServletContextInitializer;

/**
 * This class implements the default initialization
 * 
 * @author zh10082
 */
public class ServletCustomContextInitializer implements ServletContextInitializer {

	@Override
	public void onStartup(ServletContext jsfServlet) throws ServletException {
		jsfServlet.setInitParameter("com.sun.faces.forceLoadConfiguration", Boolean.TRUE.toString());
		jsfServlet.setInitParameter("javax.faces.PARTIAL_STATE_SAVING_METHOD", "true");
		jsfServlet.setInitParameter("javax.faces.PROJECT_STAGE", "Development");
		jsfServlet.setInitParameter("facelets.DEVELOPMENT", "true");
		jsfServlet.setInitParameter("javax.faces.FACELETS_REFRESH_PERIOD", "1");
		jsfServlet.setInitParameter("primefaces.CLIENT_SIDE_VALIDATION", "true");
		jsfServlet.setInitParameter("javax.faces.DEFAULT_SUFFIX", ".xhtml");
		jsfServlet.setInitParameter("primefaces.THEME", "bootstrap");
	}
	
}
