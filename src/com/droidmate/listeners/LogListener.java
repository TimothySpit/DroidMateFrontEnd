package com.droidmate.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Application Lifecycle Listener implementation class LogListener
 *
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
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent)  { 
    	logger.info("Context destroyed: "+ servletContextEvent.getServletContext().toString());
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent)  { 
         logger = getLogger(this.getClass().getName());
         logger.info("Context initialized: "+ servletContextEvent.getServletContext().toString());
    }
	
}
