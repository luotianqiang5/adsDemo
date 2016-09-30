package com.common.android.newsblast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class XMLParser extends DefaultHandler {
	private NewsBean news;
	private StringBuffer strBuffer;
	private boolean haContent;

	public XMLParser() {
		news = new NewsBean();
		strBuffer = new StringBuffer();
		haContent = false;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// super.characters(ch, start, length);
		Log.e("XMLParser", "-----------characters------------");
		strBuffer.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		Log.e("XMLParser", "-----------endElement------------");
		if (localName.equals("id")) {
			news.setId(strBuffer.toString());
		} else if (localName.equals("title")) {
			news.setTitle(strBuffer.toString());
		} else if (localName.equals("content")) {
			news.setContent(strBuffer.toString());
			haContent = true;
		} else if (localName.equals("link")) {
			news.setLink(strBuffer.toString());
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		strBuffer.delete(0, strBuffer.length());
		Log.e("XMLParser", "-----------startElement------------");
	}

	public NewsBean getNews() {
		if (haContent)
			return news;
		else
			return null;
	}

}
