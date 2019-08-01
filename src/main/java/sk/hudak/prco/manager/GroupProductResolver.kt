package sk.hudak.prco.manager;

import sk.hudak.prco.api.GroupProductKeywords;

import java.util.Optional;

public interface GroupProductResolver {

    Optional<GroupProductKeywords> resolveGroup(String productName);
}
