package twitama.bot;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import twitama.bot.character.Character;
import twitama.bot.character.CharacterFactory;
import twitama.bot.character.CharacterStatus;
import twitama.bot.character.CharacterStatusUpdateServlet;
import twitama.bot.util.BotUtil;
import twitama.bot.util.PMF;
import twitter4j.Status;

@SuppressWarnings("serial")
public class TwitamabotServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(TwitamabotServlet.class
			.getName());

	// キャラクターの名前
	private final static String NAME = "ついたまご";
	// botのキャラクター
	private static Character character;

	/**
	 * 定期的に呼ばれるメソッド
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// キャラクターの初期化
		character = new Character(NAME);

		// キャラクターのステータスをデータストアから取得する
		CharacterStatus charaStatus = CharacterStatusUpdateServlet
				.getCharacterStatus(character);

		// ステータスに応じたキャラクターの生成
		character = CharacterFactory.create(charaStatus);

		characterAction(); // キャラクターに行動させる
		//Mentions(); // 返信処理
		// FriendsTimeLine(); // 友人TL処理

		// キャラクターのステータスをデータストアにアップデートする
		CharacterStatusUpdateServlet.updateCharacterStatus(character);
	}

	/**
	 * botキャラクターに行動させる
	 */
	private void characterAction() {
		log.info("before:" + character.toString()); // キャラクターの状態をログに出力
		// キャラクターに行動を起こさせる
		character.action();
		log.info("after:" + character.toString()); // キャラクターの状態をログに出力

		// tweetを行う
		String tweetMsg = character.getTweetMsg(); // キャラクターの発言を取得
		log.info("能動発言：" + tweetMsg); // ログへ書き出し
		if (!tweetMsg.equals("")) { // 何かしらの発言があれば
			BotUtil.doTweet(character.getName(), 0, tweetMsg); // tweetする
		}

	}

	/**
	 * friendTLの処理
	 */
	private void FriendsTimeLine() {
		StringBuffer strBuff = new StringBuffer(); // log書き出し用

		// friendTLを取得
		@SuppressWarnings("unchecked")
		// 警告無視
		List<Status> statuses = (List<Status>) BotUtil.getTimeLine(
				character.getName(), BotUtil.timeLine.FRIENDS);

		// TL全てに対する処理
		if (statuses != null) { // statusesがnullでなければ
			for (Status status : statuses) {
				// logへ表示
				strBuff.append(status.getUser().getScreenName());
				strBuff.append(":");
				strBuff.append(status.getText());
				strBuff.append("\r\n");
			}
			log.info(strBuff.toString());
		}
	}

	/**
	 * Mentionの処理
	 */
	private void Mentions() {
		StringBuffer strBuff = new StringBuffer(); // log書き出し用

		// Mentionの内容を保持するリスト
		@SuppressWarnings("unchecked")
		// 警告無視
		List<Status> statuses = (List<Status>) BotUtil.getTimeLine(
				character.getName(), BotUtil.timeLine.MENTIONS);

		// データストアからtweetdataを取得
		List<TweetData> tweetDataList = getTweetData();
		// 直近で返信したツイート
		TweetData lastReply = null;
		if (tweetDataList != null) {
			if (tweetDataList.size() > 0) { // 存在すれば
				lastReply = tweetDataList.get(0);
			}
		}

		// Mention全てに対する処理
		if (statuses != null) { // statusesがnullでなければ
			for (Status status : statuses) {
				// すでに返信したリプライは返信しない
				if (lastReply != null) { // データストアにデータが存在して
					if (status.getCreatedAt().compareTo(lastReply.getDate()) <= 0) { // 取得した発言の日時が直近で返信したツイートの日時より古かったら
						continue;
					}
				}
				// リプライの文字列を生成
				String character_rep = character.replyAction(status.getText()); // キャラクターからのリプライテキストを受け取る
				String replyText = "@" + status.getUser().getScreenName() + " "
						+ character_rep; // リプライ内容の生成
				// リプライ情報を加えて発言
				BotUtil.doTweet(character.getName(), status.getId(), replyText);
				log.info("発言内容:" + replyText);
				log.info(character.toString()); // キャラクターの状態をログに出力

				// logへ表示するためのバッファへ書き出し
				strBuff.append(status.getUser().getScreenName());
				strBuff.append(":");
				strBuff.append(status.getText());
				strBuff.append("\r\n");
			}
			log.info(strBuff.toString()); // logへ出力

			// データストアの内容を削除
			delteTweetData();
			// 返信した最新のつぶやきをデータストアに書きこむ
			writeTweetDataToDataStore(statuses.get(0));
		}
	}

	/**
	 * データストアからtweetDataを取得する
	 *
	 * @return
	 */
	private List<TweetData> getTweetData() {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // データストアアクセスの準備
		List<TweetData> tdlist = null; // 取得したデータを保管するリストのポインタ

		Query query = pm.newQuery(TweetData.class); // 検索用インスタンス
		try {
			tdlist = (List<TweetData>) query.execute(); // TweetData型のデータを取得
			pm.detachCopyAll(tdlist); // persistencemanagerが閉じた後にもデータにアクセスできるようにする
		} finally {
			query.closeAll(); // クエリを閉じる
			pm.close(); // 書き込みの終了
		}
		return tdlist;
	}

	/**
	 * データストアからtweetDataを削除する
	 *
	 * @return
	 */
	private void delteTweetData() {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // データストアアクセスの準備
		List<TweetData> tdlist = null; // 取得したデータを保管するリストのポインタ

		Query query = pm.newQuery(TweetData.class); // 検索用インスタンス
		try {
			tdlist = (List<TweetData>) query.execute(); // TweetData型のデータを取得
			query.deletePersistentAll(tdlist); // データを削除
		} finally {
			query.closeAll(); // クエリを閉じる
			pm.close(); // 書き込みの終了
		}
	}

	/**
	 * データストアにつぶやきのデータを書き込む
	 *
	 * @param status
	 *            書きこむつぶやきの情報
	 */
	private void writeTweetDataToDataStore(Status status) {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // データストアのアクセスの準備
		// 書込するデータの内容を引数の情報から生成
		TweetData data = new TweetData(String.valueOf(status.getId()), status
				.getUser().getScreenName(), status.getText(),
				status.getCreatedAt());
		try {
			// データストアに書きこむ
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}

	/**
	 * キャラクターのgetter
	 *
	 * @return character botに使用されているキャラクター
	 */
	public static Character getCharacter() {
		return character;
	}
}
