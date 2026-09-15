package com.contoso.checkout;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Restores shopping-cart sessions from the shared session cache.
 *
 * CVE-2015-7501 (commons-collections 3.2.1 and earlier): InvokerTransformer
 * implements Serializable and calls an arbitrary method by reflection during
 * readObject(), the classic ysoserial gadget chain. Any ObjectInputStream
 * that deserializes bytes from a cache or session store an attacker could
 * ever influence is the vulnerable sink, with or without commons-collections
 * on the classpath - the library is what turns it into a gadget chain.
 */
public class SessionCacheDeserializer {

    public Object restoreSession(byte[] sessionBytes) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(sessionBytes))) {
            return in.readObject();
        }
    }
}
