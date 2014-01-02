package twitama.bot.util;

import java.io.IOException;
import java.util.Collections;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * キャッシュクリア用サーブレットクラス
 *
 * @author kohta
 *
 */
public class ClearCacheServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		Cache cache = BotUtil.getCache();

		if(cache != null){
			cache.clear();
		}

		resp.sendRedirect("/admin/config.jsp");
	}
}
