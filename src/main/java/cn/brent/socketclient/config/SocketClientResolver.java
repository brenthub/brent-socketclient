package cn.brent.socketclient.config;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SocketClientResolver implements EntityResolver {
	
	public static final String NAMESPACE_PREFIX="http://www.brent.cn/schema/";
	
	public static final String SCHEMA_LANGUAGE_ATTRIBUTE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	public static final String XSD_SCHEMA_LANGUAGE = "http://www.w3.org/2001/XMLSchema";

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if(systemId == null){
			return null;
		}
		String xsd=StringUtils.replace(systemId, NAMESPACE_PREFIX, "/");
		if(StringUtils.isEmpty(xsd)){
			return null;
		}
		try {
			InputSource source = new InputSource(SocketClientResolver.class.getResourceAsStream(xsd));
			source.setPublicId(publicId);
			source.setSystemId(systemId);
			return source;
		} catch (Exception e) {
			return null;
		}
	}

}
