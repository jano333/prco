package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.dao.db.ErrorEntityDao
import sk.hudak.prco.dao.db.NotInterestedProductDbDao
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.ErrorFindFilterDto
import sk.hudak.prco.dto.ErrorListDto
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.ErrorEntity
import sk.hudak.prco.service.ErrorService
import sk.hudak.prco.utils.Validate.notNull
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service("errorService")
class ErrorServiceImpl(
        @Autowired private val errorEntityDao: ErrorEntityDao,
        @Autowired private val prcoMapper: PrcoOrikaMapper,
        @Autowired private val notInterestedProductDbDao: NotInterestedProductDbDao) : ErrorService {

    private lateinit var executorService: ExecutorService

    companion object {
        val log = LoggerFactory.getLogger(ErrorServiceImpl::class.java)!!
    }

    @PostConstruct
    fun init() {
        executorService = Executors.newSingleThreadExecutor { runnable -> Thread(runnable, "errorCleanUpThread") }
    }

    @PreDestroy
    fun tearDown() {
        executorService.shutdownNow()
    }

    override val statisticForErrors: Map<ErrorType, Long>
        get() {
            val result = EnumMap<ErrorType, Long>(ErrorType::class.java)
            for (type in ErrorType.values()) {
                result[type] = errorEntityDao.getCountOfType(type)
            }
            return result
        }

    override fun createError(createDto: ErrorCreateDto): Long? {
        notNull(createDto.errorType, "errorType")

        if (createDto.fullMsg != null && createDto.fullMsg!!.length >= 4000) {
            createDto.fullMsg = createDto.fullMsg!!.substring(0, 4000)
        }
        if (createDto.message != null && createDto.message!!.length >= 250) {
            createDto.message = createDto.message!!.substring(0, 250)
        }

        if (createDto.url != null) {
            //TODO toto je zle
            val entity = errorEntityDao.findByUrl(createDto.url)
                    ?: // insert
                    return doInsert(createDto)
            // update
            entity.eshopUuid = createDto.eshopUuid
            entity.errorType = createDto.errorType
            entity.statusCode = createDto.statusCode
            entity.message = createDto.message
            entity.fullMsg = createDto.fullMsg
            entity.additionalInfo = createDto.additionalInfo
            errorEntityDao.update(entity)
            log.debug("update entity {} with id {}, type {}, msg '{}'", entity.javaClass.simpleName, entity.id,
                    entity.errorType, entity.message)

            return entity.id

        } else {
            // insert
            return doInsert(createDto)
        }
    }

    private fun doInsert(createDto: ErrorCreateDto): Long? {
        //TODO cez orika mapper
        val entity = ErrorEntity()
        entity.eshopUuid = createDto.eshopUuid
        entity.errorType = createDto.errorType
        entity.statusCode = createDto.statusCode
        entity.message = createDto.message
        entity.fullMsg = createDto.fullMsg
        entity.url = createDto.url
        entity.additionalInfo = createDto.additionalInfo

        val id = errorEntityDao.save(entity)
        log.debug("create entity {} with id {} msg: {}", entity.javaClass.simpleName, entity.id, entity.message)
        return id
    }

    override fun findAll(): List<ErrorListDto> {
        return errorEntityDao.findAll().stream()
                .map { entity -> prcoMapper.map(entity, ErrorListDto::class.java) }
                .collect(Collectors.toList())
    }

    override fun findErrorByMaxCount(limit: Int, errorType: ErrorType): List<ErrorListDto> {
        return errorEntityDao.findByMaxCount(limit, errorType).stream()
                .map { entity -> prcoMapper.map(entity, ErrorListDto::class.java) }
                .collect(Collectors.toList())
    }

    override fun findErrorsByTypes(vararg errorTypes: ErrorType): List<ErrorListDto> {
        notNull(errorTypes, "errorTypes")
        //TODO not empty

        return errorEntityDao.findByTypes(*errorTypes).stream()
                .map { entity -> prcoMapper.map(entity, ErrorListDto::class.java) }
                .collect(Collectors.toList())
    }


    override fun findErrorsByFilter(findDto: ErrorFindFilterDto): List<ErrorListDto> {
        notNull(findDto, "findDto")

        return errorEntityDao.findErrorsByFilter(findDto).stream()
                .map { entity -> prcoMapper.map(entity, ErrorListDto::class.java) }
                .collect(Collectors.toList())
    }

    override fun startErrorCleanUp(): Future<Void>? {
        //TODO pozor na startnutie a commit transakcie
        //        return executorService.submit(() -> {
        //            List<ErrorEntity> errors = errorEntityDao.findOlderThan(30, TimeUnit.DAYS);
        //            //TODO impl
        //
        //
        //            return null;
        //        });


        var toBeDeleted = errorEntityDao.findOlderThan(30, TimeUnit.DAYS)
        var count = 0
        if (!toBeDeleted.isEmpty()) {
            for (errorEntity in toBeDeleted) {
                errorEntityDao.delete(errorEntity)
                count++
            }
        }
        log.debug("removed older errors, count {}", toBeDeleted.size)

        //TODO doc a do osobitnej metody
        val fistTenURL = notInterestedProductDbDao.findFistTenURL()
        if (!fistTenURL.isEmpty()) {
            toBeDeleted = errorEntityDao.findByUrls(fistTenURL)

            if (!toBeDeleted.isEmpty()) {
                for (errorEntity in toBeDeleted) {
                    errorEntityDao.delete(errorEntity)
                }
            }
            log.debug("count of not interested already count {}", toBeDeleted.size)
        }


        // TODO impl future
        return null;
    }
}
