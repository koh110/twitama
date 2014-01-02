package twitama.bot.character;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import java.util.logging.Logger;

/**
 * botのキャラクターを表すクラス
 *
 * @author kohta
 *
 */
public class Character {
	private static final Logger log = Logger.getLogger(Character.class
			.getName());

	/**
	 * キャラクターの名前
	 */
	final private String name;

	/**
	 * キャラクターの最大HP
	 */
	final private int MAX_HP = 10;

	/**
	 * キャラクターの空腹を感じる閾値
	 */
	final private int HUNGER = 4;

	/**
	 * キャラクターの種族の名前
	 */
	private String breedName;

	/**
	 * キャラクターのステータス
	 */
	CharacterStatus status;

	/**
	 * コンストラクタ
	 */
	public Character(String name) {
		this.name = name;
		this.breedName = "normal";
		// ステータスの初期化
		status = CharacterStatus.createCharacterStatus(name);
	}
	/**
	 * ステータスから生成するコンストラクタ
	 * @param status
	 */
	public Character(CharacterStatus status){
		this(status.getName());	// ステータスから名前を取得して生成
		setCharacterStatus(status);	// ステータスをセット
	}

	/**
	 * キャラクターが行動を起こすメソッド
	 */
	public void action() {
		// HPの取得
		int hp = status.getHitPoint();
		if (hp <= 0) { // HPが0を切っていたら
			log.info("死んでる！");
			status.setDead(true); // 死亡
			relive(); // 生き返る
			hp = status.getHitPoint();	// hpが変わるのでhpをもう一度取得
		} else {
			log.info("生きてるからお腹減るよ");
			hp--; // HPを減らす
		}
		// HPをセット
		status.setHitPoint(hp);
	}

	/**
	 * ステータスの内容から発言を生成する
	 *
	 * @return 発言する内容
	 */
	public String getTweetMsg() {
		String tweetMsg = ""; // 発言する内容
		final int hp = status.getHitPoint(); // HPの取得
		if (status.isDead()) {// 死亡時
			tweetMsg = "お腹がすき過ぎて死にました";
		} else if (0 < hp && hp < HUNGER) { // 空腹を感じてる時
			tweetMsg = "お腹すいた～\nnow HP(" + status.getHitPoint() + ")";
		} else if (hp == MAX_HP) { // HPが満タンの時
			tweetMsg = "完全復活！\nnew HP("+status.getHitPoint()+")";
		}else{
			tweetMsg = "私は元気です\nnew HP("+status.getHitPoint()+")";
		}

		// すべての発言を変えるために現在時刻を付け加える
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EEE HH:mm:ss z");
	    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		tweetMsg += "\n["+Calendar.getInstance().getTime()+"]";
		return tweetMsg;
	}

	/**
	 * リプライされてきたメッセージに対する対応
	 *
	 * @param rep
	 *            送られてきた内容
	 * @return 返信内容
	 */
	public String replyAction(String rep) {
		String repMsg = new String(); // 返信する内容用文字列
		if(status.isDead()){	// 死んでいる時に受け付ける命令群
			if(rep.contains("いきかえれ")){
				if (status.isDead()) {
					repMsg = "生き返りました";
					relive(); // 生き返らせる処理
				} else {
					repMsg = "すでに生き返ってます";
				}
			}else{
				repMsg = "死亡なう";
			}
		}
		else {	// 死んでない時に受け付ける命令群
			if (rep.contains("おにく")) {
				eatMeat(); // 肉を食べる処理
				repMsg = "おにくもぐもぐ。\nnow HP(" + status.getHitPoint() + ")";
			} else if (rep.contains("やさい")) {
				eatVegetable(); // 野菜を食べる処理
				repMsg = "やさいもぐもぐ。\nnow HP(" + status.getHitPoint() + ")";
			} else if (rep.contains("hitpoint")) {
				repMsg = "今のHPは(" + status.getHitPoint() + ")です";
			}else{
				repMsg = "うるせーっす";
			}
		}

		// すべての発言を変えるために現在時刻を付け加える
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd EEE HH:mm:ss z");
	    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		repMsg += "\n["+Calendar.getInstance().getTime()+"]";
		return repMsg;
	}

	/**
	 * 生き返る時の処理
	 */
	public void relive() {
		log.info("死んでるから回復するよ！");
		int hp = status.getHitPoint();
		status.setDead(false); // 死亡フラグを消す
		status.setHitPoint(MAX_HP); // HPを最大値にする
		log.info("HP:"+hp+"->"+status.getHitPoint());
	}

	/**
	 * 肉を食べる処理
	 */
	public void eatMeat() {
		if (status.isDead()) { // 死んでる時は処理しない
			return;
		}
		int meet = status.getMeet_num(); // 肉を食べた数の取得
		meet++; // 食べた数を増加
		cureHP(2); // HPを2回復させる
		status.setMeet_num(meet); // ステータスに反映
	}

	/**
	 * 野菜を食べる処理
	 */
	public void eatVegetable() {
		if (status.isDead()) { // 死んでる時は処理しない
			return;
		}
		int vegetable = status.getVegetable_num(); // 野菜を食べた数の取得
		vegetable++; // 食べた数を増加
		cureHP(2); // HP2回復させる
		status.setVegetable_num(vegetable); // ステータスに反映
	}

	/**
	 * HPを回復させる処理
	 *
	 * @param value
	 */
	public void cureHP(int value) {
		int hp = status.getHitPoint(); // HPの取得
		log.info("beforeHP:"+hp);
		if (hp + value > MAX_HP) { // 回復させた値が最大HPを超える場合
			hp = MAX_HP; // HPを最大HPにする
		} else {
			hp += value; // HPをvalue分回復させる
		}
		log.info("afterHP:"+hp);
		status.setHitPoint(hp); // ステータスに反映
	}

	// getter,setter----------------------------------------------------
	public String getName() {
		return name;
	}

	public void setCharacterStatus(CharacterStatus cs) {
		status = cs;
	}

	public CharacterStatus getCharacterStatus() {
		return status;
	}

	public void setBreedName(String breedName){
		this.breedName = breedName;
	}

	public String getBreedName(){
		return this.breedName;
	}

	public String toString(){
		return "name,"+getName()+"\nbreed,"+breedName+"\n"+status.toString();
	}
}
