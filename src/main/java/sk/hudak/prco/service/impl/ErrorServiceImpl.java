package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dao.db.ErrorEntityDao;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.error.ErrorListDto;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.ErrorEntity;
import sk.hudak.prco.service.ErrorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static sk.hudak.prco.utils.Validate.notNull;

@Slf4j
@Service("errorService")
public class ErrorServiceImpl implements ErrorService {

    @Autowired
    private ErrorEntityDao errorEntityDao;

    @Autowired
    private PrcoOrikaMapper prcoMapper;

    private ExecutorService executorService;

    @Autowired
    private NotInterestedProductDbDao notInterestedProductDbDao;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "errorCleanUpThread"));
    }

    @PreDestroy
    public void tearDown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Override
    public Long createError(ErrorCreateDto createDto) {
        notNull(createDto, "createDto");
        notNull(createDto.getErrorType(), "errorType");

        if (createDto.getFullMsg() != null && createDto.getFullMsg().length() >= 4000) {
            createDto.setFullMsg(createDto.getFullMsg().substring(0, 4000));
        }

        if (createDto.getUrl() != null) {
            ErrorEntity entity = errorEntityDao.findByUrl(createDto.getUrl());
            if (entity == null) {
                // insert
                return doInsert(createDto);
            }
            // update
            entity.setErrorType(createDto.getErrorType());
            entity.setStatusCode(createDto.getStatusCode());
            entity.setMessage(createDto.getMessage());
            entity.setFullMsg(createDto.getFullMsg());
            entity.setUrl(createDto.getUrl());
            entity.setAdditionalInfo(createDto.getAdditionalInfo());
            errorEntityDao.update(entity);
            log.debug("update entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());

            return entity.getId();

        } else {
            // insert
            return doInsert(createDto);
        }
    }

    private Long doInsert(ErrorCreateDto createDto) {
        ErrorEntity entity = new ErrorEntity();
        entity.setErrorType(createDto.getErrorType());
        entity.setStatusCode(createDto.getStatusCode());
        entity.setMessage(createDto.getMessage());
        entity.setFullMsg(createDto.getFullMsg());
        entity.setUrl(createDto.getUrl());
        entity.setAdditionalInfo(createDto.getAdditionalInfo());

        Long id = errorEntityDao.save(entity);
        log.debug("create entity {} with id {}", entity.getClass().getSimpleName(), entity.getId());
        return id;
    }

    @Override
    public List<ErrorListDto> findAll() {
        return errorEntityDao.findAll().stream()
                .map(entity -> prcoMapper.map(entity, ErrorListDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ErrorListDto> findByTypes(ErrorType... errorTypes) {
        notNull(errorTypes, "errorTypes");
        //TODO not empty

        return errorEntityDao.findByTypes(errorTypes).stream()
                .map(entity -> prcoMapper.map(entity, ErrorListDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Map<ErrorType, Long> getStatisticForErrors() {
        Map<ErrorType, Long> result = new EnumMap(ErrorType.class);
        for (ErrorType type : ErrorType.values()) {
            result.put(type, errorEntityDao.getCountOfType(type));
        }
        return result;
    }

    @Override
    public Future<Void> startErrorCleanUp() {
        //TODO pozor na startnutie a commit transakcie
//        return executorService.submit(() -> {
//            List<ErrorEntity> errors = errorEntityDao.findOlderThan(30, TimeUnit.DAYS);
//            //TODO impl
//
//
//            return null;
//        });


        List<ErrorEntity> toBeDeleted = errorEntityDao.findOlderThan(30, TimeUnit.DAYS);
        int count = 0;
        if (!toBeDeleted.isEmpty()) {
            for (ErrorEntity errorEntity : toBeDeleted) {
                errorEntityDao.delete(errorEntity);
                count++;
            }
        }
        log.debug("remove older count {}", toBeDeleted.size());

        List<String> fistTenURL = notInterestedProductDbDao.findFistTenURL();
        if (!fistTenURL.isEmpty()) {
            toBeDeleted = errorEntityDao.findByUrls(fistTenURL);

            if (!toBeDeleted.isEmpty()) {
                for (ErrorEntity errorEntity : toBeDeleted) {
                    errorEntityDao.delete(errorEntity);
                }
            }
            log.debug("count of not interested already count {}", toBeDeleted.size());
        }

        // TODO impl future
        return null;
    }
}
