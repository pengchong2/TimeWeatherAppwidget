package cn.flyaudio.weather.xml;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PropertyXMLParaser extends DefaultHandler {
	
	public static final String TAG = "PropertyXMLParaser";

	HashMap<String, String> propertiesHashMap = new HashMap<String, String>();
	String prtTag = null;
	String key = null;
	
	public FlyProperty getProperties(){
		return new FlyProperty(propertiesHashMap);
	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		prtTag = qName;
		key = attributes.getValue("name");
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		key = prtTag = null;
	}

	@Override
	public void endDocument() throws SAXException {
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(prtTag!=null && prtTag.equals("property")){
			String value = new String(ch, start, length);
			propertiesHashMap.put(key, value);
		}
	}

}
