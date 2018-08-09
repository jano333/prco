package sk.hudak.prco.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dao.db.ErrorEntityDao;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.error.ErrorListDto;
import sk.hudak.prco.mapper.PrcoOrikaMapper;
import sk.hudak.prco.model.ErrorEntity;
import sk.hudak.prco.service.ErrorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sk.hudak.prco.utils.Validate.notNull;

@Slf4j
@Service("errorService")
public class ErrorServiceImpl implements ErrorService {

    @Autowired
    private ErrorEntityDao errorEntityDao;

    @Autowired
    private PrcoOrikaMapper prcoMapper;

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
        Map<ErrorType, Long> result = new HashMap<>();
        for (ErrorType type : ErrorType.values()) {
            result.put(type, errorEntityDao.getCountOfType(type));
        }
        return result;
    }
}
