package com.pramati.webscraper.db.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "WebScrapper")
public class ExtractedDataDetails {
	
	@Id
	private String id;

	private String url;
	
	private int statusCode;
	
	private String fileNameWithPath;

	private String htmlData;	
	
	private Date createdDate;
	
	private Date modifiedDate;
	
	private String createdby;
	
	private String modifiedBy;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the statusCode
	 */
	public Integer getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	/**
	 * @return the fileNameWithPath
	 */
	public String getFileNameWithPath() {
		return fileNameWithPath;
	}

	/**
	 * @param fileNameWithPath the fileNameWithPath to set
	 */
	public void setFileNameWithPath(String fileNameWithPath) {
		this.fileNameWithPath = fileNameWithPath;
	}
	
	/**
	 * @return the htmlData
	 */
	public String getHtmlData() {
		return htmlData;
	}

	/**
	 * @param htmlData the htmlData to set
	 */
	public void setHtmlData(String htmlData) {
		this.htmlData = htmlData;
	}
	
	


	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the createdby
	 */
	public String getCreatedby() {
		return createdby;
	}

	/**
	 * @param createdby the createdby to set
	 */
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public ExtractedDataDetails(){
		
	}
	
	
public ExtractedDataDetails(String url, int statusCode,
					String fileNameWithPath, String htmlData, Date createdDate, Date modifiedDate, String createdby,
					String modifiedBy) {
		this.url = url;
		this.statusCode = statusCode;
		this.fileNameWithPath = fileNameWithPath;
		this.htmlData = htmlData;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdby = createdby;
		this.modifiedBy = modifiedBy;
	}

public ExtractedDataDetails( String url, int statusCode, String fileNameWithPath, String htmlData, Date createdDate, String createdby) {
	this.url = url;
	this.statusCode = statusCode;
	this.fileNameWithPath = fileNameWithPath;
	this.htmlData = htmlData;
	this.createdDate = createdDate;
	this.createdby = createdby;
	
}

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
	return "ExtractedDataDetails [id=" + id + ", url=" + url + ", statusCode=" + statusCode + ", fileNameWithPath=" + fileNameWithPath + ", htmlData=" + htmlData
					+ ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", createdby=" + createdby
					+ ", modifiedBy=" + modifiedBy + "]";
}

	
	

}
