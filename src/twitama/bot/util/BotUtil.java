package twitama.bot.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * キャッシュからtwitterオブジェクトを生成するクラス
 * @author kohta
 *
 */
public class BotUtil {

	private static final Logger log = Logger.getLogger(BotUtil.class.getName());

	private static String cachedtoken = "_access_token";
	private static String cachedsecret = "_access_secret";
	public enum timeLine{PUBLIC, FRIENDS, MENTIONS, DIRECTMESSAGE};

	public BotUtil(){}

	/**
	 * 引数の名前を持つbotのtwitterオブジェクトを返すメソッド
	 * @param botname
	 * @return
	 */
	public static Twitter getTwitter(String botname){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Cache cache = getCache();

		// cache内にaccesstoken/secretがない場合はDataStoreから読み込む
		if((String)cache.get(botname + cachedtoken)==null ||
				(String)cache.get(botname + cachedsecret)==null){

			// Tokenクラスから主キーを生成する
			Query query = pm.newQuery(Token.class);
			List<Token> tokens = (List<Token>)query.execute();

			for(Token token:tokens){
				if(token!=null && token.getBotName().equals(botname)){
					cache.put(botname + cachedtoken, token.getAccessToken());
					cache.put(botname+cachedsecret, token.getAccessSecret());
				}
			}
			pm.close();
		}
		// twitterオブジェクトの生成
		Twitter twitter = new TwitterFactory().getInstance(
				new AccessToken((String)cache.get(botname+cachedtoken),
						(String)cache.get(botname+cachedsecret)));
		return twitter;
	}

	/**
	 * キャッシュを取得するメソッド
	 * @return
	 */
	public static Cache getCache(){
		Cache cache = null;

		try{
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		}catch(CacheException e){
			log.info(e.getMessage());
		}

		// 凍結されたユーザや非公開ユーザのidをキャッシュに保存するキーがない場合は作成する
		if(cache.get("not_process_user") == null){
			cache.put("not_process_user", "/");
		}
		return cache;
	}

	/**
	 * botnameのtimeline情報に対応するTLを返す
	 * @param botname
	 * @param tl
	 * @return
	 */
	public static List getTimeLine(String botname,timeLine tl){
		try{
			Twitter twitter = BotUtil.getTwitter(botname);	// twitterオブジェクトの取得

			switch(tl){
				case PUBLIC:
					return twitter.getPublicTimeline();		// publicTimelineを返す
				case FRIENDS:
					return twitter.getFriendsTimeline();	// 公式RTを含まないfriendsTLを返す
				case MENTIONS:
					return twitter.getMentions();			// リプライを返す
				case DIRECTMESSAGE:
					return twitter.getDirectMessages();		// ダイレクトメッセージを返す
			}
		}catch(TwitterException e){
			log.info(e.getMessage());
		}
		return new ArrayList<Status>();
	}

	/**
	 * Tweetを行う
	 * @param botname
	 * @param inreplyToStatusId
	 * @param text
	 */
	public static void doTweet(String botname,long inreplyToStatusId, String text){
		if(text.length()>0){
			Twitter twitter = BotUtil.getTwitter(botname);	// twitterオブジェクトの生成

			// 発言情報を保持
			StatusUpdate statusupdate = new StatusUpdate(text);

			try{
				// リプライ先があればリプライ先のIDを発言情報に追加
				if(inreplyToStatusId != 0){
					statusupdate.setInReplyToStatusId(inreplyToStatusId);
				}

				// 140文字以上であれば切り取る
				if(text.length()>140){
					text = text.substring(0,139);
				}

				// twitterに発言する
				twitter.updateStatus(statusupdate);
			}catch(TwitterException e){
				log.info(e.getMessage());
			}
		}
	}

	/**
	 * ダイレクトメッセージを送る
	 * @param botname
	 * @param userId
	 * @param text
	 */
	public static void doDirectMessage(String botname, int userId, String text){
		if(text.length()>0){
			Twitter twitter = BotUtil.getTwitter(botname);

			if(text.length()>140){
				text = text.substring(0,139);
			}

			try{
				twitter.sendDirectMessage(userId, text);
			}catch(TwitterException e){
				log.info(e.getMessage());
			}
		}
	}
}
