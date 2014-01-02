package twitama.bot;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import java.util.Date;

/**
 * DataStoreにTweetDataオブジェクトを書きこむためのクラス
 * @author kohta
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)	// JDOでこのクラスを使用できるようにするアノテーション
public class TweetData {
	@PrimaryKey	// 主キーの設定
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)	// 永続化するメンバ変数
	private String statusId;	// ツイートのID

	@Persistent
	private String userName;	// ツイートのユーザ名

	@Persistent
	private String text;	// ツイートの内容

	@Persistent
	private Date date;	// ツイートが作成された日時

	/**
	 * コンストラクタ
	 * @param statusId
	 * @param username
	 * @param text
	 * @param date
	 */
	public TweetData(String statusId, String userName, String text,Date date){
		this.statusId = statusId;
		this.userName = userName;
		this.text = text;
		this.date = date;
	}

	public String getUserName(){
		return userName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}

	public String getStatusId(){
		return statusId;
	}

	public void setStatusId(String statusId){
		this.statusId = statusId;
	}
}
