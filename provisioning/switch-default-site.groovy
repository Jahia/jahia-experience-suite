import org.jahia.registries.ServicesRegistry
import org.jahia.services.sites.JahiaSitesService

setResult("remove");
def sitesService = ServicesRegistry.instance.jahiaSitesService
sitesService.setDefaultSite(sitesService.getSiteByKey("luxe"))
