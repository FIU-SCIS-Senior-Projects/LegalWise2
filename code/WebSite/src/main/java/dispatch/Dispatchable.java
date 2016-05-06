package dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * All classes tha will serve as dispatcher should implement
 * this interface
 * @author Fernando
 * @date	02/08/2015
 *
 */
public interface Dispatchable {
	/**
	 * @author Fernando
	 * @date	02/08/2015 
	 * @param request
	 * @param response
	 */
	public void dispatchGet(HttpServletRequest request, 
			HttpServletResponse response) 
					throws ServletException, IOException;
	
	/**
	 * @author Fernando
	 * @date	02/08/2015 
	 * @param request
	 * @param response
	 */
	public void dispatchPost(HttpServletRequest request, 
			HttpServletResponse response)
					throws ServletException, IOException;
}
