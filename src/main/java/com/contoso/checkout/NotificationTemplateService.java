package com.contoso.checkout;

import org.apache.commons.text.StringSubstitutor;

/**
 * Renders merchant-configurable notification templates, e.g.
 * "Hi ${customer.name}, your order shipped."
 *
 * CVE-2022-42889 (Text4Shell, commons-text 1.5-1.9): StringSubstitutor's
 * interpolator registers script/dns/url lookups by default, so running it
 * over an admin-editable template string can execute arbitrary lookups.
 * createInterpolator() is the exact vulnerable configuration.
 */
public class NotificationTemplateService {

    public String renderTemplate(String template) {
        StringSubstitutor substitutor = StringSubstitutor.createInterpolator();
        return substitutor.replace(template);
    }
}
