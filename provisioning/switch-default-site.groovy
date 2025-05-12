import org.jahia.registries.ServicesRegistry
import org.jahia.services.content.JCRCallback
import org.jahia.services.content.JCRSessionWrapper
import org.jahia.services.content.JCRTemplate
import org.jahia.services.sites.JahiaSitesService

import javax.jcr.RepositoryException

JahiaSitesService sitesService = ServicesRegistry.instance.jahiaSitesService

JCRCallback<Object> callback = new JCRCallback<Object>() {
    @Override
    Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
        sitesService.setDefaultSite(sitesService.getSiteByKey("luxe", session), session)
        session.save()
    }
}
JCRTemplate.instance.doExecuteWithSystemSession(callback);
