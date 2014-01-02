package twitama.bot.util;


import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class CallbackServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(CallbackServlet.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// セッションからtwitterオブジェクトとRequestTokenの取出
		AccessToken accessToken = null;
		Twitter twitter = (Twitter) req.getSession().getAttribute("twitter");
		RequestToken requestToken = (RequestToken) req.getSession()
				.getAttribute("requestToken");
		String verifier = req.getParameter("oauth_verifier");

		// AccessTokenの取得
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			req.getSession().removeAttribute("requestToken");
		} catch (TwitterException e) {
			log.info(e.getMessage());
		}

		// TokenオブジェクトにAccessToken/Secretと「bot name」を格納しGAE上に保存
		if (accessToken != null) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Token token = new Token();
			token.setAccessToken(accessToken.getToken());
			token.setAccessSecret(accessToken.getTokenSecret());
			token.setBotName((String) req.getSession().getAttribute("botname"));
			pm.makePersistent(token);
			pm.close();
		}

		req.getSession().removeAttribute("botname");
		resp.sendRedirect(req.getContextPath() + "/");
	}
}
