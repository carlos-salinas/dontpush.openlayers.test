package com.thingtrack.dontpush.openlayers.test.servlet;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.vaadin.dontpush.server.DontPushOzoneWebApplicationContext;
import org.vaadin.dontpush.server.SocketCommunicationManager;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationOSGiServlet;
import com.vaadin.terminal.gwt.server.CommunicationManager;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CometApplicationOSGiServlet extends ApplicationOSGiServlet {

	private Class<SocketCommunicationManager> communicationManagerClass;
	private SocketCommunicationManager activeManager;

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		String communicationManagerClassName = servletConfig
				.getInitParameter("communicationManagerClass");
		if (communicationManagerClassName != null) {
			try {
				this.communicationManagerClass = (Class<SocketCommunicationManager>) Class
						.forName(communicationManagerClassName);
			} catch (ClassNotFoundException e) {
				this.communicationManagerClass = null;
			}
		}
	}

	@Override
	protected WebApplicationContext getApplicationContext(HttpSession session) {
		WebApplicationContext cx = (WebApplicationContext) session
				.getAttribute(WebApplicationContext.class.getName());
		if (cx == null) {
			cx = new DontPushOzoneWebApplicationContext(session,
					this.communicationManagerClass);
			session.setAttribute(WebApplicationContext.class.getName(), cx);
		}
		return cx;
	}

	@Override
	protected boolean handleURI(CommunicationManager applicationManager,
			Window window, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		activeManager = (SocketCommunicationManager) applicationManager;
		return super.handleURI(applicationManager, window, request, response);
	}

	@Override
	protected void writeAjaxPage(HttpServletRequest request,
			HttpServletResponse response, Window window, Application application)
			throws IOException, MalformedURLException, ServletException {
		response.addCookie(new Cookie("OZONE_CM_ID", activeManager.getId()));
		super.writeAjaxPage(request, response, window, application);
	}

}
