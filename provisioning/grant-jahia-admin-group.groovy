import org.jahia.registries.ServicesRegistry
import org.jahia.services.content.JCRCallback
import org.jahia.services.content.JCRSessionWrapper
import org.jahia.services.content.JCRTemplate

import javax.jcr.RepositoryException

Properties searchProperties = new java.util.Properties()
searchProperties.setProperty("groupname", "jahia-admin")
def service = ServicesRegistry.instance.jahiaGroupManagerService
def first = service.searchGroups(null, searchProperties, "ldap.config.dockerldap.groups").first()
service.lookupGroupByPath("/groups/administrators").addMember(first).getSession().save()
service.lookupGroupByPath("/sites/digitall/groups/site-administrators").addMember(first).getSession().save()
service.lookupGroupByPath("/sites/luxe/groups/site-administrators").addMember(first).getSession().save()

JCRCallback<Object> callback = new JCRCallback<Object>() {
    @Override
    Object doInJCR(JCRSessionWrapper session) throws RepositoryException {
        def node = session.getNode("/")
        def roles = new java.util.HashSet()
        roles.add("server-administrator")
        roles.add("system-administrator")
        node.grantRoles("g:jahia-admin", roles)
        def tools = session.getNode("/tools")
        tools.grantRoles("g:jahia-admin", roles)
        session.save()
    }
}
JCRTemplate.instance.doExecuteWithSystemSession(callback);


