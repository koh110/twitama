package twitama.bot.character;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * キャラクターのステータスをデータストアに書きこむためのクラス
 *
 * @author kohta
 *
 */
// JDOでこのクラスを使用できるようにするアノテーション
// IdentityType:アプリケーション内で一意なデータ
// デタッチ（更新）可能
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class CharacterStatus {
	@PrimaryKey
	// 主キーの設定
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	// 永続化するメンバ変数
	private String name; // キャラクターの名前

	@Persistent
	private boolean dead; // 死亡フラグ

	@Persistent
	private int hitPoint; // HP

	@Persistent
	private int meet_num; // 肉を食べた数

	@Persistent
	private int vegetable_num; // 野菜を食べた数

	/**
	 * コンストラクタ
	 *
	 * @param name
	 *            名前
	 * @param dead
	 *            死んでいるかどうか
	 * @param hitPoint
	 *            HP
	 * @param meet_num
	 *            肉を食べた数
	 * @param vegetable_num
	 */
	public CharacterStatus(String name, boolean dead, int hitPoint,
			int meet_num, int vegetable_num) {
		this.name = name;
		this.dead = dead;
		this.hitPoint = hitPoint;
		this.meet_num = meet_num;
		this.vegetable_num = vegetable_num;
	}

	/**
	 * 通常キャラクターステータス生成メソッド
	 *
	 * @param name
	 * @return
	 */
	public static CharacterStatus createCharacterStatus(String name) {
		return new CharacterStatus(name, false, 10, 0, 0);
	}

	/**
	 * 草食キャラクターの基本ステータス生成
	 *
	 * @param name
	 * @return
	 */
	public static CharacterStatus createGrazerStatus(String name){
		return new CharacterStatus(name,false, 10, 0 ,1);
	}

	/**
	 * 肉食キャラクターの基本ステータス生成
	 * @param name
	 * @return
	 */
	public static CharacterStatus createCarnivoraStatus(String name){
		return new CharacterStatus(name,false,10,1,0);
	}

	// getterおよびsetterはこの下
	public int getHitPoint() {
		return hitPoint;
	}

	public void setHitPoint(int hitPoint) {
		this.hitPoint = hitPoint;
	}

	public String getName() {
		return name;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public int getMeet_num() {
		return meet_num;
	}

	public void setMeet_num(int meet_num) {
		this.meet_num = meet_num;
	}

	public int getVegetable_num() {
		return vegetable_num;
	}

	public void setVegetable_num(int vegetable_num) {
		this.vegetable_num = vegetable_num;
	}

	public String toString(){
		return "hp,"+getHitPoint()
				+"\ndead,"+isDead()
				+"\nmeet,"+getMeet_num()
				+"\nvegetable,"+getVegetable_num();
	}
}
