package twitama.bot.character;


import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitama.bot.util.PMF;

public class DeleteCharaStatusServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // PMFオブジェクトの取得
		Query query = pm.newQuery(CharacterStatus.class); // クエリの生成
		query.setFilter("name == nameParam");
		query.declareParameters("String nameParam");

		try {
			List<CharacterStatus> results = (List<CharacterStatus>) query.execute(
					req.getParameter("name"));// nameパラメータで指定されてる値で探索したものをリストに追加
			pm.deletePersistentAll(results); // リストの全てを削除
		} finally {
			query.closeAll();
		}

		resp.sendRedirect("/admin/charaConfig.jsp");
	}
}
