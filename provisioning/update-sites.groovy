
import org.jahia.registries.ServicesRegistry
import org.jahia.services.content.JCRCallback
import org.jahia.services.content.JCRSessionWrapper
import org.jahia.services.content.JCRTemplate
import org.jahia.services.content.decorator.JCRSiteNode
import org.jahia.services.sites.JahiaSitesService

import javax.jcr.RepositoryException

JahiaSitesService sitesService = ServicesRegistry.instance.jahiaSitesService

JCRCallback<Object> callback = new JCRCallback<Object>() {
  @Override
  Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
    def roles = new java.util.HashSet()
    roles.add("editor-in-chief")
    JCRSiteNode siteByKey = sitesService.getSiteByKey("digitall", session)
    siteByKey.grantRoles("g:digitall-editor-in-chief", roles)
    siteByKey.setServerName("digitall.jahia.localhost")
    siteByKey.setServerNameAliases(["digitall.jahiabrowsing.localhost"])
    session.save()
    siteByKey = sitesService.getSiteByKey("luxe", session)
    siteByKey.grantRoles("g:luxe-editor-in-chief", roles)
    siteByKey.setServerName("luxe.jahia.localhost")
    siteByKey.setServerNameAliases(["luxe.jahiabrowsing.localhost"])
    session.save()
  }
}
JCRTemplate.instance.doExecuteWithSystemSession(callback);
