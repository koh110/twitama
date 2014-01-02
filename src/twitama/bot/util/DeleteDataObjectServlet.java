package twitama.bot.util;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteDataObjectServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // PMFオブジェクトの取得
		Query query = pm.newQuery(Token.class); // クエリの生成
		query.setFilter("botName == botNameParam");
		query.declareParameters("String botNameParam");

		try {
			List<Token> results = (List<Token>) query.execute(
					req.getParameter("name"));// nameパラメータで指定されてる値で探索したものをリストに追加
			pm.deletePersistentAll(results); // リストの全てを削除
		} finally {
			query.closeAll();
		}

		resp.sendRedirect("/admin/config.jsp");
	}
}
