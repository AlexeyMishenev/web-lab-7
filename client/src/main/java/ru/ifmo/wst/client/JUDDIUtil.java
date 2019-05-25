package ru.ifmo.wst.client;


import static java.text.MessageFormat.format;
import static org.apache.juddi.api_v3.AccessPointType.BINDING_TEMPLATE;
import static org.apache.juddi.api_v3.AccessPointType.END_POINT;
import static org.apache.juddi.api_v3.AccessPointType.HOSTING_REDIRECTOR;
import static org.apache.juddi.api_v3.AccessPointType.WSDL_DEPLOYMENT;

import java.util.List;
import java.util.stream.Collectors;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.Name;

class JUDDIUtil {
  static void printBusinessInfo(BusinessInfos businessInfos) {
    if (businessInfos == null) {
      System.out.println("No data returned");
    } else {
      businessInfos.getBusinessInfo().forEach(JUDDIUtil::printBusinessDetail);
    }
  }

  private static void printBusinessDetail(BusinessInfo businessInfo) {
    System.out.println("===============================================");
    System.out.println(format("Business Key: {0}", businessInfo.getBusinessKey()));
    System.out.println(format("Name: {0}",
        businessInfo.getName()
            .stream()
            .map(Name::getValue)
            .collect(Collectors.joining(" ")))
    );

    System.out.println(format("Description: {0}",
        businessInfo.getDescription()
            .stream()
            .map(Description::getValue)
            .collect(Collectors.joining(" ")))
    );
  }


  static void printServicesInfo(List<BusinessService> businessServices) {
    businessServices.forEach(service -> {
      System.out.println("-------------------------------------------");
      printServiceInfo(service);
    });
  }

  private static void printServiceInfo(BusinessService businessService) {
    System.out.println(format("Service Key: {0}", businessService.getServiceKey()));
    System.out.println(format("Owning Business Key: {0}", businessService.getBusinessKey()));
    System.out.println(format("Name: {0}",
        businessService.getName()
            .stream()
            .map(JUDDIUtil::nameToString)
            .collect(Collectors.joining("\n")))
    );
    printBindingTemplates(businessService.getBindingTemplates());
  }

  /**
   * This function is useful for translating UDDI's somewhat complex data format to something that
   * is more useful.
   */
  private static void printBindingTemplates(BindingTemplates bindingTemplates) {
    if (bindingTemplates == null) {
      return;
    }
    bindingTemplates.getBindingTemplate().forEach(bTmpl -> {
      System.out.println(format("Binding Key: {0}", bTmpl.getBindingKey()));
      if (bTmpl.getAccessPoint() != null) {
        System.out.println(format("Access Point: {0} type {1}",
            bTmpl.getAccessPoint().getValue(),
            bTmpl.getAccessPoint().getUseType()));
        if (bTmpl.getAccessPoint().getUseType() != null) {
          if (bTmpl.getAccessPoint().getUseType().equalsIgnoreCase(END_POINT.toString())) {
            System.out.println(
                "Use this access point value as an invocation endpoint.");
          }
          if (bTmpl.getAccessPoint().getUseType().equalsIgnoreCase(BINDING_TEMPLATE.toString())) {
            System.out.println(
                "Use this access point value as a reference to another binding template.");
          }
          if (bTmpl.getAccessPoint().getUseType().equalsIgnoreCase(WSDL_DEPLOYMENT.toString())) {
            System.out.println(
                "Use this access point value as a URL to a WSDL document, which presumably will have a real access point defined.");
          }
          if (bTmpl.getAccessPoint().getUseType().equalsIgnoreCase(HOSTING_REDIRECTOR.toString())) {
            System.out.println(
                "Use this access point value as an Inquiry URL of another UDDI registry, look up the same binding template there (usage varies).");
          }
        }
      }
    });
  }

  private static String nameToString(Name name) {
    return format("Lang: {0}\nValue: {1}", name.getLang(), name.getValue());
  }
}
