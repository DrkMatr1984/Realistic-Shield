package com.hotmail.ooosssososos;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateCheck {
	
	private Shield pl;
	private URL fileFeed;
	private String version;
	private String link;
	public UpdateCheck(Shield plugin, String url){
		this.pl = plugin;
		try {
			this.fileFeed = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean updateNeeded(){
		
		try {
			
			InputStream in = this.fileFeed.openConnection().getInputStream();
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			
			Node latest = doc.getElementsByTagName("item").item(0);
			NodeList children = latest.getChildNodes();
			
			this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
			this.link = children.item(3).getTextContent();
			if(!pl.getDescription().getVersion().equals(this.version)){
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public String getVersion(){
		return this.version;
	}
	public String getLink(){
		return this.link;
	}
	
}	
