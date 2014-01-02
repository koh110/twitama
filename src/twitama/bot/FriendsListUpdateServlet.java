package twitama.bot;


import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitama.bot.util.FriendsListUpdate;

/**
 * 自動的にフォロー返し、フォロー解除を行う
 * @author kohta
 *
 */
public class FriendsListUpdateServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		FriendsListUpdate.doFriendsListUpdate(TwitamabotServlet.getCharacter().getName());
	}
}
