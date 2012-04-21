package com.brianreber.gitstats;
import java.util.Date;


public class Commit {

	private String commitNum;
	private Date date;
	private String authorName;
	private String authorEmail;
	private String message;

	/**
	 * @param commitNum
	 * @param date
	 * @param authorName
	 * @param authorEmail
	 * @param message
	 */
	public Commit(String commitNum, Date date, String authorName, String authorEmail, String message) {
		this.commitNum = commitNum;
		this.date = date;
		this.authorName = authorName;
		this.authorEmail = authorEmail;
		this.message = message;
	}

	/**
	 * @return the authorName
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @param authorName the authorName to set
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	/**
	 * @return the authorEmail
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}

	/**
	 * @param authorEmail the authorEmail to set
	 */
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}

	/**
	 * @return the commitNum
	 */
	public String getCommitNum() {
		return commitNum;
	}

	/**
	 * @param commitNum the commitNum to set
	 */
	public void setCommitNum(String commitNum) {
		this.commitNum = commitNum;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Commit [commitNum=" + commitNum + ", date=" + date
				+ ", authorName=" + authorName + ", authorEmail=" + authorEmail
				+ ", message=" + message + "]";
	}

}
