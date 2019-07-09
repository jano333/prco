package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.db.WatchDogEntityDao;
import sk.hudak.prco.dto.WatchDogAddDto;
import sk.hudak.prco.dto.WatchDogDto;
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.model.WatchDogEntity;
import sk.hudak.prco.parser.EshopUuidParser;
import sk.hudak.prco.service.WatchDogService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

@Slf4j
@Service("watchDogService")
public class WatchDogServiceImpl implements WatchDogService {

    @Autowired
    private EshopUuidParser eshopUuidParser;

    @Autowired
    private WatchDogEntityDao watchDogEntityDao;

    @Autowired
    private MailSender mailSender;

    @Override
    public Long addNewProductToWatch(WatchDogAddDto addDto) {
        notNull(addDto, "addDto");
        notNullNotEmpty(addDto.getProductUrl(), "productUrl");
        notNull(addDto.getMaxPriceToBeInterestedIn(), "maxPriceToBeInterestedIn");

        // check if already exist with this URL
        if (watchDogEntityDao.existWithUrl(addDto.getProductUrl())) {
            throw new PrcoRuntimeException("Product with URL " + addDto.getProductUrl() + " already exist");
        }

        WatchDogEntity entity = new WatchDogEntity();
        entity.setEshopUuid(eshopUuidParser.parseEshopUuid(addDto.getProductUrl()));
        entity.setProductUrl(addDto.getProductUrl());
        entity.setMaxPriceToBeInterestedIn(addDto.getMaxPriceToBeInterestedIn());

        Long id = watchDogEntityDao.save(entity);
        log.debug("create new entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());

        return id;
    }

    @Override
    public Map<EshopUuid, List<WatchDogDto>> findProductsForWatchDog() {
        List<WatchDogEntity> watchDogEntityList = watchDogEntityDao.findAll();
        log.debug("count of all watch dog products: {}", watchDogEntityList.size());

        List<WatchDogDto> collect = watchDogEntityList.stream().map(t -> {
            //FIXME orika
            WatchDogDto dto = new WatchDogDto();
            dto.setId(t.getId());
            dto.setEshopUuid(t.getEshopUuid());
            dto.setProductUrl(t.getProductUrl());
            dto.setMaxPriceToBeInterestedIn(t.getMaxPriceToBeInterestedIn());
            return dto;
        }).collect(Collectors.toList());

        if (collect.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<EshopUuid, List<WatchDogDto>> result = new EnumMap<>(EshopUuid.class);

        for (WatchDogDto watchDogDto : collect) {
            List<WatchDogDto> watchDogDtos = result.get(watchDogDto.getEshopUuid());
            if (watchDogDtos == null) {
                result.put(watchDogDto.getEshopUuid(), new ArrayList<>(1));
            }
            watchDogDtos = result.get(watchDogDto.getEshopUuid());
            watchDogDtos.add(watchDogDto);
        }
        return result;
    }

    @Override
    public void notifyByEmail(List<WatchDogNotifyUpdateDto> toBeNotified) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("noreply@sk.hudak.prco.com");
        simpleMessage.setSubject("watch dog products");
        simpleMessage.setTo("hudakjan83@gmail.com");
        simpleMessage.setText(buildTextBody(toBeNotified));
        log.warn("not yet implemeted");

        System.out.println(toBeNotified);

//        mailSender.send(simpleMessage);
//        log.debug("email sent successfully");

    }

    private String buildTextBody(List<WatchDogNotifyUpdateDto> toBeNotified) {
        StringBuilder sb = new StringBuilder();
        //TODO
        sb.append("aloha");
        return sb.toString();
    }
}
