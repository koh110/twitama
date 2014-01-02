package twitama.bot.util;

import java.util.logging.Logger;
import javax.cache.Cache;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * 自動フォロー、リムーブを行うクラス
 *
 * @author kohta
 *
 */
public class FriendsListUpdate {
	private static final Logger log = Logger.getLogger(FriendsListUpdate.class
			.getName());

	private enum FriendsListMode {
		GETFRIENDS, GETFOLLOWERS, DOREMOVE, DOFOLLOW
	};

	/**
	 * フォロー、リムーブ処理をbotnameごとに行う
	 *
	 * @param botname
	 */
	public static void doFriendsListUpdate(String botname) {
		Twitter twitter = BotUtil.getTwitter(botname);

		String tempFriendsList = getIdsString(twitter,
				FriendsListMode.GETFRIENDS);
		String tempFollowerList = getIdsString(twitter,
				FriendsListMode.GETFOLLOWERS);

		String[] removeList = getTargetIds(tempFollowerList, tempFriendsList);
		String[] followList = getTargetIds(tempFriendsList, tempFollowerList);

		doListProcess(twitter, FriendsListMode.DOREMOVE, removeList);
		doListProcess(twitter, FriendsListMode.DOFOLLOW, followList);
	}

	/**
	 * フォロー、リムーブ処理
	 *
	 * @param twitter
	 * @param mode
	 * @param targetList
	 */
	private static void doListProcess(Twitter twitter, FriendsListMode mode,
			String[] targetList) {
		Cache cache = BotUtil.getCache();

		if (targetList != null && targetList.length > 0) {
			for (int i = 0; i < targetList.length; i++) {
				if (targetList[i].length() > 0
						&& ((String) cache.get("not_process_user")).indexOf("/"
								+ targetList[i] + "/") == -1) {
					try{
						switch(mode){
							case DOREMOVE:
								// 処理対象のuserId名のキーがキャッシュ内にある場合はその値に1を加算する
								if(cache.get(targetList[i])!= null){
									cache.put(targetList[i], Integer.valueOf(cache.get(targetList[i]).toString())+1);
								}else{	// ない場合は新規に作成する
									cache.put(targetList[i], 0);
								}

								if(Integer.parseInt(cache.get(targetList[i]).toString())>3){
									twitter.destroyFriendship(Integer.parseInt(targetList[i]));
									log.info("remove:"+targetList[i]);
									cache.remove(targetList[i]);
								}
								break;
							case DOFOLLOW:
								twitter.createFriendship(Integer.parseInt(targetList[i]));
								log.info("follow:" + targetList[i]);
								break;
						}
					}catch(TwitterException e){
						log.info(e.getMessage());

						String cacheData = (String)cache.get("not_process_user");

						// フォロー出来ないuserIdをnot_process_userに追加して処理を除外する
						if(e.getMessage().indexOf("Could not follow user") != -1){
							cache.put("not_process_user", cacheData+targetList[i]+"/");
							log.info("Add not process user:"+targetList[i]);
						}

						log.info("cacheData:" + cacheData);
					}
				}
			}
		}
	}

	/**
	 * twitterオブジェクトに対応するIDを取得
	 *
	 * @param twitter
	 * @param mode
	 * @return
	 */
	private static String getIdsString(Twitter twitter, FriendsListMode mode) {
		StringBuffer idsData = new StringBuffer();

		try {
			long cursor = -1;
			IDs workIds = null;

			do {
				switch (mode) {
				case GETFRIENDS:
					workIds = twitter.getFriendsIDs(cursor);
					break;
				case GETFOLLOWERS:
					workIds = twitter.getFollowersIDs(cursor);
					break;
				}

				long[] targetIds = workIds.getIDs();
				for (int i = 0; i < targetIds.length; i++) {
					idsData.append(targetIds[i]);
					idsData.append("/");
				}

				cursor = workIds.getNextCursor();
			} while (cursor != 0);
		} catch (TwitterException e) {
			log.info(e.getMessage());
		}
		return idsData.toString();
	}

	/**
	 * targetListに含まれていないID群を返す
	 *
	 * @param targetList
	 * @param checkIdsList
	 * @return
	 */
	private static String[] getTargetIds(String targetList, String checkIdsList) {
		String[] returnIDs = null;

		if (checkIdsList != null && checkIdsList.indexOf("/") != -1) {
			String[] checkIDs = checkIdsList.split("/");
			StringBuffer stringIds = new StringBuffer();
			stringIds.append("/");

			for (int i = 0; i < checkIDs.length; i++) {
				if (("/" + targetList).indexOf("/" + checkIDs[i] + "/") == -1) { // 存在しなければ
					stringIds.append(checkIDs[i]);
					stringIds.append("/");
				}
			}
			returnIDs = stringIds.toString().split("/");
		}
		return returnIDs;
	}
}
