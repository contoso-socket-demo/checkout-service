package com.contoso.checkout;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.security.AnyTypePermission;

/**
 * Imports legacy order exports handed off by a discontinued EDI partner, in
 * that partner's XML format.
 *
 * XStream ships a default-deny type allowlist since ~1.4.18, so a bare
 * "new XStream()" now rejects this app's own OrderRecord with a
 * ForbiddenClassException. The common real-world fix - reopening it with
 * addPermission(AnyTypePermission.ANY) - is exactly what re-exposes the
 * original unrestricted-deserialization issue: type selection goes back to
 * being driven entirely by the XML, not by the caller's expected POJO.
 */
public class LegacyOrderXmlImporter {

    public static class OrderRecord {
        public String id;

        @Override
        public String toString() {
            return "OrderRecord{id='" + id + "'}";
        }
    }

    public Object importOrders(String xml) {
        // PureJavaReflectionProvider avoids sun.misc.Unsafe so this runs cleanly on
        // modern JDKs; it has no bearing on the vulnerability below.
        XStream xstream = new XStream(new PureJavaReflectionProvider());
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("order", OrderRecord.class);
        return xstream.fromXML(xml);
    }
}
