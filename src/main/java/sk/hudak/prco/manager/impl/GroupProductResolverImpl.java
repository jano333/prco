package sk.hudak.prco.manager.impl;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.GroupProductKeywords;
import sk.hudak.prco.manager.GroupProductResolver;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GroupProductResolverImpl implements GroupProductResolver {

    @Override
    public Optional<GroupProductKeywords> resolveGroup(@NonNull String productName) {
        // spritnem nazov produktu zo zoznamu slov(lower case)
        Set<String> productNameWords = Arrays.stream(StringUtils.split(productName, StringUtils.SPACE))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(String::toLowerCase)
                .map(StringUtils::stripAccents)
                .collect(Collectors.toSet());


        return Arrays.stream(GroupProductKeywords.values())
                .filter(keyword -> resolve(keyword, productNameWords))
                .findFirst();
    }

    private boolean resolve(GroupProductKeywords keyword, Set<String> productNameWords) {
        return keyword.getChoices().stream()
                .filter(choice -> productNameWords.containsAll(choice))
                .findFirst()
                .isPresent();
    }
}
