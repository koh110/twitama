package twitama.bot.character;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitama.bot.util.PMF;

/**
 * キャラクターのステータスをアップデートするサーブレットクラス
 * データストアとCharacterStatusクラスとの窓口クラスとしての機能も持つ
 *
 * @author kohta
 *
 */
public class CharacterStatusUpdateServlet extends HttpServlet {

	/**
	 * Webから入力されたCharacterStateをデータストアに書きこむ
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		PersistenceManager pm = PMF.get().getPersistenceManager();

		// キャラクターステータスを入力から受け取る
		CharacterStatus charaStatus = new CharacterStatus(
				req.getParameter("name"),
				Boolean.valueOf(req.getParameter("dead")),
				Integer.valueOf(req.getParameter("hitPoint")), Integer.valueOf(req
						.getParameter("meet_num")), Integer.valueOf(req
						.getParameter("vegetable_num")));

		// データストアに書き込む
		pm.makePersistent(charaStatus);

		pm.close();

		resp.sendRedirect("/admin/charaConfig.jsp");
	}

	/**
	 * 引数で与えられたCharacterの名前と一致するデータをデータストアから取得する
	 *
	 * @param charaState
	 * @return データストアから取得された引数の名前と一致するCharacterStatus
	 */
	public static CharacterStatus getCharacterStatus(Character chara) {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // データストアのアクセスの準備
		CharacterStatus charaState = chara.getCharacterStatus(); // 引数のキャラクターのステータス
		CharacterStatus cs = null; // 戻り値用の変数
		try {
			// データストアから引数のCharacterStatusのnameを主キーに持つCharacterStatusを取得する
			cs = pm.getObjectById(CharacterStatus.class, charaState.getName());
		} finally {
			pm.close();
		}
		return cs;
	}

	/**
	 * データストアにある引数のなめと一致するCharacterStateの状態を更新するメソッド
	 *
	 * @param charaState
	 */
	public static void updateCharacterStatus(Character chara) {
		PersistenceManager pm = PMF.get().getPersistenceManager(); // データストアのアクセスの準備
		CharacterStatus charaState = chara.getCharacterStatus(); // 引数のキャラクターのステータス
		try {
			// データストアから引数のCharacterStatusのnameを主キーに持つCharacterStatusを取得する
			CharacterStatus cs = pm.getObjectById(CharacterStatus.class,
					charaState.getName());
			// CharacterStatusのHPを更新する
			cs.setHitPoint(charaState.getHitPoint());
			// 死亡フラグの更新
			cs.setDead(charaState.isDead());
			// 肉を食べた数の更新
			cs.setMeet_num(charaState.getMeet_num());
			// 野菜を食べた数の更新
			cs.setVegetable_num(charaState.getVegetable_num());
		} finally {
			// データストアに書きこみを行う
			pm.close();
		}
	}
}
