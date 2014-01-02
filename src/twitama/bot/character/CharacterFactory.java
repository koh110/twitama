package twitama.bot.character;

import java.util.ArrayList;

/**
 * ステータスに応じたキャラクターを生成するファクトリークラス
 * @author kohta
 *
 */
public class CharacterFactory {
	// 通常キャラクターの比較用基底ステータス
	private final static CharacterStatus NORMAL_STATUS = CharacterStatus.createCharacterStatus("");
	// 草食キャラクターの比較用基底ステータス
	private final static CharacterStatus GRAZER_STATUS = CharacterStatus.createGrazerStatus("");
	// 肉食キャラクターの比較用基底ステータス
	private final static CharacterStatus CARNIVORA_STATUS = CharacterStatus.createCarnivoraStatus("");

	/**
	 * 引数に与えられたキャラクターステータスに応じてキャラクターを生成する
	 * @param characterStatus
	 * @return
	 */
	public static Character create(CharacterStatus characterStatus){
		ArrayList<Double> distances = new ArrayList<Double>();
		// 通常の基底ステータスと比較した距離
		double compareNormal = comparison(characterStatus,NORMAL_STATUS);
		distances.add(compareNormal);
		// 肉の基底ステータスと比較した距離
		double compareMeet = comparison(characterStatus,CARNIVORA_STATUS);
		distances.add(compareMeet);
		// 野菜の基底ステータスと比較した距離
		double compareVegetable = comparison(characterStatus,GRAZER_STATUS);
		distances.add(compareVegetable);

		// 一番低い(近い)値を取得
		double near = compareNormal;
		for(int i=0;i<distances.size();i++){
			// 比較する値
			double crtDistance = distances.get(i);
			if(near>crtDistance){	// 比較する値がより近ければ
				near = crtDistance;
			}
		}

		// 一番近い値に対応するキャラクターステータスを返す
		if(near == compareNormal){	// ノーマルに近ければ通常キャラクターを生成
			return new Character(characterStatus);
		}else if(near == compareMeet){	// 肉に近ければ肉キャラクターを生成
			return new Carnivora(characterStatus);
		}else if(near == compareVegetable){	// 野菜に近ければ野菜キャラクターを生成
			return new Grazer(characterStatus);
		}
		return new Character(characterStatus);
	}

	/**
	 * キャラクターステータスがどれだけ近いか比較するメソッド
	 * ユークリッド距離から求める
	 * @param a 比較するステータス
	 * @param b 比較するステータス
	 * @return キャラクターステータスの空間距離
	 */
	private static double comparison(CharacterStatus a,CharacterStatus b){
		// aの肉を食べた数
		int meetNum_a = a.getMeet_num();
		// bの肉を食べた数
		int meetNum_b = b.getMeet_num();
		// aの野菜を食べた数
		int vegetableNum_a = a.getVegetable_num();
		// bの野菜を食べた数
		int vegetableNum_b = b.getVegetable_num();
		// 肉のユークリッド距離
		double meetDistance = (meetNum_a-meetNum_b)*(meetNum_a-meetNum_b);
		// 野菜のユークリッド距離
		double vegetableDistance = (vegetableNum_a-vegetableNum_b)*(vegetableNum_a-vegetableNum_b);

		// ユークリッド距離を計算
		return Math.sqrt(meetDistance+vegetableDistance);
	}
}
