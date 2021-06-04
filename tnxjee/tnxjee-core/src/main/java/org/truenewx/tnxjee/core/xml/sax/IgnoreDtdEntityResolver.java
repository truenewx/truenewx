package org.truenewx.tnxjee.core.xml.sax;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.truenewx.tnxjee.core.Strings;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 忽略DTD内容的EntityResolver
 *
 * @author jianglei
 * 
 */
public class IgnoreDtdEntityResolver implements EntityResolver {

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream(Strings.EMPTY.getBytes()));
    }

}
