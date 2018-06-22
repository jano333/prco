package sk.hudak.prco.utils;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Component
public class UserAgentDataHolder {

    private Map<EshopUuid, String> userAgents = new EnumMap<>(EshopUuid.class);

    @PostConstruct
    public void init() {
        Arrays.stream(EshopUuid.values()).forEach(
                eshopUuid -> userAgents.put(eshopUuid, UserAgentManager.getRandom())
        );
    }

    public String getUserAgentForEshop(@NonNull EshopUuid eshopUuid) {
        return userAgents.get(eshopUuid);
    }

}
