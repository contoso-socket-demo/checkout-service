package com.contoso.checkout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Writes customer-supplied order comments to the audit log.
 *
 * CVE-2021-44228 (Log4Shell, log4j-core &lt;= 2.14.1): a logged message that
 * contains a lookup pattern such as "${jndi:...}" is evaluated by the
 * pattern layout by default, not just printed. Any code that logs an
 * externally-controlled string - exactly what this method does with the
 * storefront's "special instructions" field - is the vulnerable pattern.
 */
public class OrderCommentLogger {

    private static final Logger logger = LogManager.getLogger(OrderCommentLogger.class);

    public void logComment(String orderId, String customerComment) {
        logger.error("Order {} received special instructions: {}", orderId, customerComment);
    }
}
