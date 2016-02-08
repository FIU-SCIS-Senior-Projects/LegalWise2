package com.ibm.devworks.examples.java.lll;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DispatchServlet
 */
@WebServlet({  "/home", "/antarctica", "/alaska", "/arctic", "/australia"})
public class DispatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final WebsiteTitle myapp = new WebsiteTitle();

    /**
     * Default constructor. 
     */
    public DispatchServlet() {
        // TODO Auto-generated constructor stub
    	
   
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getRequestURI().substring(request.getContextPath().length());
		
		System.out.println(path);
		request.setAttribute("myapp", myapp);
		// TODO Auto-generated method stub
		RequestDispatcher view = request.getRequestDispatcher(path + ".jsp");
        view.forward(request, response); 		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
