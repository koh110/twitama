<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="javax.jdo.PersistenceManager"%>
<%@page import="javax.jdo.Query"%>
<%@page import="twitama.bot.util.PMF"%>
<%@page import="twitama.bot.util.Token"%>
<%@page import="java.util.List"%>

<%@page import="com.google.appengine.api.users.User"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>config</title>
</head>
<body>

	<B>User Information</B>
	<%-- UserServiceオブジェクトを利用してユーザー情報を表示する --%>
	<br />
	<%
		UserService service = UserServiceFactory.getUserService();

		if (!service.isUserLoggedIn()) {
			((HttpServletResponse) response).sendRedirect(service
					.createLoginURL(request.getRequestURI()));
			return;
		}

		User user = service.getCurrentUser();
		out.println("isAdmin: " + service.isUserAdmin() + ("<br>"));
		out.println("nickname: " + user.getNickname() + ("<br>"));
		out.println("mail:" + user.getEmail() + ("<br>"));
		out.println("<a href=\""
				+ service.createLogoutURL(request.getRequestURI())
				+ "\">logout</a>");
	%>

	<hr />
	<B>Get AccessToken</B>
	<%-- OAuth認証を行い、指定したbotnameと関連するアクセストークン、シークレットをデータストアに保存する --%>
	<br />

	<form method="POST" action="/getaccesstoken">
		bot name:<input id="name" type="text" name="name" /> <input
			type="submit" value="post" />
	</form>

	<hr />
	<B>regist AccessToken</B>
	<%-- 入力したbotname,accessToken,AccessSecretをデータストアに保存する --%>
	<br />

	<form method="POST" action="/admin/registtoken">
		bot name:<input id="botname" type="text" name="botname" /><br />
		Token:<input id="token" type="text" name="token" /><br /> Secret:<input
			id="secret" type="text" name="secret" /><br /> <input type="submit"
			value="post" />
	</form>

	<hr />
	<B>clearcache</B>
	<%-- キャッシュを手動でクリアする --%>
	<br />

	<form method="GET" action="/cron/clearcache">
		<input type="submit" value="clear cache" />
	</form>

	<hr />
	<%
		PersistenceManager pm = PMF.get().getPersistenceManager(); // 登録済みのbotname/accessToken/AccessSecretを表示する
		Query query = pm.newQuery(Token.class);
		Token tokenData = null;

		try {
			List<Token> tokenResult = (List<Token>) query.execute();
			out.print("bot name<br>");
			out.print("<table>");

			for (Token token : tokenResult) {
				out.print("<tr><td>");
				out.print(token.getBotName() + "<br />"
						+ token.getAccessToken() + "<br />"
						+ token.getAccessSecret());
				out.print("</td><td>");
				out.print("<form method=\"POST\" action=\"/admin/deletetoken\">");
				out.print("<input type=\"hidden\" name=\"name\" value=\""
						+ token.getBotName() + "\" >");
				out.print("<input type=\"submit\" value=\"delete\" >");
				out.print("</form>");
				out.print("</td></tr>");
			}

			out.print("</table>");

		} finally {
			query.closeAll();
		}
	%>

</body>
</html>

