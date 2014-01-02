package twitama.bot.character;

/**
 * 肉を多く食べると進化するキャラクタークラス
 * @author kohta
 *
 */
public class Carnivora extends Character{
	/**
	 * コンストラクタ
	 * @param name
	 */
	public Carnivora(String name){
		super(name);
		setBreedName("meet");
	}

	public Carnivora(CharacterStatus status){
		super(status);
		setBreedName("meet");
	}
}
