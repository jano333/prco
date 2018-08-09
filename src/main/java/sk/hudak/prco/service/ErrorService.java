package sk.hudak.prco.service;

import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dto.error.ErrorCreateDto;
import sk.hudak.prco.dto.error.ErrorListDto;

import java.util.List;
import java.util.Map;

public interface ErrorService {

    // TODO cron na odmazavanie

    Long createError(ErrorCreateDto createDto);

    List<ErrorListDto> findAll();

    List<ErrorListDto> findByTypes(ErrorType... errorTypes);

    Map<ErrorType, Long> getStatisticForErrors();

}
