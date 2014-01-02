package twitama.bot.character;

/**
 * 野菜を多く食べると進化するクラス
 * @author kohta
 *
 */
public class Grazer extends Character{
	/**
	 * コンストラクタ
	 * @param name
	 */
	public Grazer(String name){
		super(name);
		setBreedName("vegetable");
	}

	public Grazer(CharacterStatus status){
		super(status);
		setBreedName("vegetable");
	}
}
