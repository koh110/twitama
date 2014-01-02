package twitama.bot.access.outgoing;

import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitama.bot.character.Character;
import twitama.bot.character.CharacterFactory;
import twitama.bot.character.CharacterStatus;
import twitama.bot.character.CharacterStatusUpdateServlet;

/**
 * 外部からHttpリクエストを通じてGAEのデータストアにアクセスするための窓口クラス
 * @author kohta
 *
 */
@SuppressWarnings("serial")
public class OutgoingAccessServlet extends HttpServlet{
	// log出力のためのインスタンス
	private static final Logger log = Logger
			.getLogger(OutgoingAccessServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req,HttpServletResponse resp){
		// リクエストから取得したキャラクターの名前からキャラクターインスタンスを生成する
		Character chara = new Character("ついたまご");
		// キャラクターのステータスをデータストアから取得する
		CharacterStatus charaStatus = CharacterStatusUpdateServlet.getCharacterStatus(chara);
		// キャラクターに応じたキャラをセット
		chara = CharacterFactory.create(charaStatus);

		// 返信するタイプをテキストで、テキストのタイプをUTF-8に設定
		resp.setContentType("text/html; charset=utf-8");
		// ヘッダー値を設定
		resp.setHeader("CharacterStatus", "character-status");
		try{
			// リクエストに返信するためのwriter
			PrintWriter out = resp.getWriter();
			// キャラクターの情報を表示する
			out.println(chara.toString());
		}catch(Exception e){
			// エラーはログに書く
			log.info(e.toString());
		}
	}
}
