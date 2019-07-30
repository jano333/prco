package sk.hudak.prco.service;

import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dto.ErrorCreateDto;
import sk.hudak.prco.dto.ErrorFindFilterDto;
import sk.hudak.prco.dto.ErrorListDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface ErrorService {

    // TODO cron na odmazavanie

    Long createError(ErrorCreateDto createDto);

    List<ErrorListDto> findAll();

    /**
     * Najde maximalne <code>limit</code> error zotriedenych od najnovsieho po najstarsi
     * @param limit
     * @return
     */
    List<ErrorListDto> findErrorByMaxCount(int limit, ErrorType errorType);

    List<ErrorListDto> findErrorsByTypes(ErrorType... errorTypes);

    List<ErrorListDto> findErrorsByFilter(ErrorFindFilterDto findDto);

    Map<ErrorType, Long> getStatisticForErrors();

    /**
     * Odmaze:
     * - vsetky chyby ktore maju update date starsi ako 30 dni.
     * - vsetky chyby, ktore maju URL rovnaku ako v not interested produkts
     * -
     */
    Future<Void> startErrorCleanUp();
}
