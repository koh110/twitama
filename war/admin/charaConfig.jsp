<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="javax.jdo.PersistenceManager"%>
<%@page import="javax.jdo.Query"%>
<%@page import="twitama.bot.util.PMF"%>
<%@page import="twitama.bot.character.CharacterStatus"%>
<%@page import="java.util.List"%>

<%@page import="com.google.appengine.api.users.User"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>character config</title>
</head>
<body>

	<B>User Information</B><%-- UserServiceオブジェクトを利用してユーザー情報を表示する --%>
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

	<br /><br />

	<B>regist CharaStatus</B><%-- 入力したname,hitPoint,meet_num,vegetable_numをデータストアに保存する --%>
	<br />

	<form method="POST" action="/admin/characterConfig">
		name:<input id="name" type="text" name="name" /><br />
		死亡フラグ:<input id="dead" type="text" name = "name" /><br />
		HP:<input id="hitPoint" type="text" name="hitPoint" /><br />
		肉を食べた数:<input id="meet_num" type="text" name="meet_num" /><br />
		野菜を食べた数:<input id="vegetable_num" type="text" name="vegetable_num" /><br />
		<input type="submit" value="post" />
	</form>

	<%
		PersistenceManager pm = PMF.get().getPersistenceManager(); // 登録済みのCharaStatusを表示する
		Query query = pm.newQuery(CharacterStatus.class);
		CharacterStatus tokenData = null;

		try {
			List<CharacterStatus> charaStatusResult = (List<CharacterStatus>) query.execute();
			out.print("name<br>");
			out.print("<table>");

			for (CharacterStatus chara : charaStatusResult) {
				out.print("<tr><td>");
				out.print(chara.getName() + "<br />"
						+ chara.isDead() + "<br />"
						+ chara.getHitPoint() + "<br />"
						+ chara.getMeet_num() + "<br />"
						+ chara.getVegetable_num() + "<br />");
				out.print("</td><td>");
				out.print("<form method=\"POST\" action=\"/admin/deleteCharaStatus\">");
				out.print("<input type=\"hidden\" name=\"name\" value=\""
						+ chara.getName() + "\" >");
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

