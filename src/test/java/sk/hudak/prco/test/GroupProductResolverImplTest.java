package sk.hudak.prco.test;

import sk.hudak.prco.api.GroupProductKeywords;
import sk.hudak.prco.manager.GroupProductResolver;
import sk.hudak.prco.manager.impl.GroupProductResolverImpl;

import java.util.Optional;

public class GroupProductResolverImplTest {

    public static void main(String[] args) {

        String productName = "Pampers Active Baby-dry 4 Maxi 36 ks 7-14 kg";

        GroupProductResolver resolver = new GroupProductResolverImpl();
        Optional<GroupProductKeywords> groupProductKeywords = resolver.resolveGroup(productName);
        if (groupProductKeywords.isPresent()) {
            System.out.println(groupProductKeywords.get());
        } else {
            System.out.println("not found");
        }
    }
}
