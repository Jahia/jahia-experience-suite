import org.jahia.osgi.BundleUtils
import org.jahia.registries.ServicesRegistry
import org.jahia.services.mail.MailService
import org.jahia.services.mail.MailSettings
import org.osgi.service.cm.ConfigurationAdmin

// Jahia 8.2.3.8+ and 8.2.4.0+ ship the org.jahia.api.mail API and send mail through the mail-service
// module (PID org.jahia.modules.mail), where MailService.store() persists nothing.
def mailApiAvailable
try {
    Class.forName("org.jahia.api.mail.MailService", false, MailService.class.classLoader)
    mailApiAvailable = true
} catch (ClassNotFoundException ignored) {
    mailApiAvailable = false
}

if (mailApiAvailable) {
    def configAdmin = BundleUtils.getOsgiService(ConfigurationAdmin.class, null)

    def mailProps = new Hashtable<String, Object>()
    mailProps.put("smtp.host", "mail")
    mailProps.put("smtp.port", "25")
    mailProps.put("smtp.auth", "false")
    mailProps.put("smtp.starttls", "false")
    mailProps.put("smtp.ssl", "false")
    mailProps.put("default.from", "noreply.jahia-experience-suite@jahia.localhost")
    mailProps.put("default.recipient", "admin.jahia-experience-suite@jahia.localhost")
    mailProps.put("disabled", "false")
    // "?" is a multi-location binding, so the mail-service bundle receives the configuration.
    configAdmin.getConfiguration("org.jahia.modules.mail", "?").update(mailProps)

    def notification = configAdmin.getConfiguration("org.jahia.bundles.notification", "?")
    def notificationProps = notification.getProperties() ?: new Hashtable<String, Object>()
    notificationProps.put("notificationLevel", "Paranoid")
    notification.update(notificationProps)
} else {
    MailSettings mailSettings = new MailSettings()
    mailSettings.setServiceActivated(true)
    mailSettings.setUri("smtp://mail:25")
    mailSettings.setFrom("noreply.jahia-experience-suite@jahia.localhost")
    mailSettings.setTo("admin.jahia-experience-suite@jahia.localhost")
    mailSettings.setNotificationLevel("Paranoid")
    ServicesRegistry.instance.mailService.store(mailSettings)
}
