package twitama.bot.util;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class GetaccesstokenServlet extends HttpServlet {

	private static final Logger log = Logger
			.getLogger(GetaccesstokenServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			// getAccessToken.htmlで入力した「bot name」をセッションに格納
			String botname = req.getParameter("name");
			req.getSession().setAttribute("botname", botname);

			// twitterオブジェクトをセッションに格納
			Twitter twitter = new TwitterFactory().getInstance();
			req.getSession().setAttribute("twitter", twitter);

			// callback用のURLを生成して格納
			StringBuffer callbackURL = req.getRequestURL();
			int index = callbackURL.lastIndexOf("/");
			callbackURL.replace(index, callbackURL.length(), "").append("/callback");

			// RequestTokenを取得してセッションに格納、アプリケーション認可画面に移動
			RequestToken requestToken =
					twitter.getOAuthRequestToken(callbackURL.toString());
			req.getSession().setAttribute("requestToken", requestToken);
			resp.sendRedirect(requestToken.getAuthenticationURL());

		} catch (TwitterException e) {
			log.info(e.getMessage());
		}
	}
}
