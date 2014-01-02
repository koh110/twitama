package twitama.bot.util;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistTokenServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException,IOException{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Token token = new Token(req.getParameter("botname"),
				req.getParameter("token"),
				req.getParameter("secret"));
		pm.makePersistent(token);
		pm.close();
		resp.sendRedirect("/admin/config.jsp");
	}
}
