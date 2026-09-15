package com.contoso.checkout;

/**
 * Northwind Order Service - a small demo backend used to show how Socket's
 * reachability analysis tells apart "a vulnerable library is on the
 * classpath" from "this application actually calls the vulnerable code".
 *
 * Only two services below are wired to a real, publicly documented
 * vulnerable API, using benign, hardcoded example data. Nothing here sends
 * network traffic, spawns a process, or touches the filesystem - it is safe
 * to build, run, and scan. See README.md for the CVE list and the matching
 * Socket reachability results.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        OrderCommentLogger commentLogger = new OrderCommentLogger();
        commentLogger.logComment("ORD-10231", "Please deliver after 5pm, thanks!");

        NotificationTemplateService notifications = new NotificationTemplateService();
        String rendered = notifications.renderTemplate("Hi ${sys:user.name}, your order shipped.");
        System.out.println(rendered);
    }
}
