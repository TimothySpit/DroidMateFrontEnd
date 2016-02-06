package com.droidmate.listeners;

import static org.slf4j.LoggerFactory.getLogger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;

/**
 * Logger for Events.
 */
@WebListener
public class LogListener implements ServletContextListener {

	Logger logger = null;

	/**
	 * Default constructor.
	 */
	public LogListener() {
	}

	/**
	 * Logs a destroyed context.
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("Context destroyed: " + servletContextEvent.getServletContext().toString());
	}

	/**
	 * Logs an context initialization.
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger = getLogger(this.getClass().getName());
		logger.info("Context initialized: " + servletContextEvent.getServletContext().toString());
	}

}
