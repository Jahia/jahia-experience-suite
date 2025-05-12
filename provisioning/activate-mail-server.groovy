import org.jahia.registries.ServicesRegistry
import org.jahia.services.mail.MailSettings

MailSettings mailSettings = new MailSettings()
mailSettings.setServiceActivated(true)
mailSettings.setUri("smtp://mail:25")
mailSettings.setFrom("noreply.jahia-experience-suite@jahia.localhost")
mailSettings.setTo("admin.jahia-experience-suite@jahia.localhost")
mailSettings.setNotificationLevel("Paranoid")
ServicesRegistry.instance.mailService.store(mailSettings)
