import java.time.LocalDate
import java.util.Calendar

setResult("remove");
org.jahia.services.content.JCRTemplate.getInstance().doExecuteWithSystemSession({ session ->
    LocalDate months = LocalDate.now().plusMonths(3)
    org.jahia.osgi.BundleUtils.getOsgiService("org.jahia.modules.apitokens.TokenService")
        .tokenBuilder("/users/root", "test-token12345", session)
        .setToken("kgHNm05iQV61I+GY3X5HVr13i866HAAsyou8G+eGubk=")
        .setActive(true)
        .setExpirationDate(toCalendar(months))
        .create()
    session.save();
})

Calendar toCalendar(LocalDate localDate) {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
    return calendar;
}
