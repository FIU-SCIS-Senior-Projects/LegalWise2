package dispatch;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Serves request looking for files (.css, .js, .png, .jpg, etc)
 * @author Fernando
 *
 */
public class FileDispatcher implements Dispatchable {
	private String file;
	
	/**
	 * 
	 */
	public FileDispatcher(String file) {
		this.file = file;
	}
	
	@Override
	public void dispatchGet(HttpServletRequest request,
			HttpServletResponse response) 
					throws ServletException, IOException{
		RequestDispatcher view = request.getRequestDispatcher(file);
		view.forward(request, response);
	}

	@Override
	public void dispatchPost(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
	}
}