package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Service
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.WatchDogEntityDao
import sk.hudak.prco.dto.WatchDogAddCustomDto
import sk.hudak.prco.dto.WatchDogDto
import sk.hudak.prco.dto.WatchDogNotifyUpdateDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.model.WatchDogEntity
import sk.hudak.prco.parser.EshopUuidParser
import sk.hudak.prco.service.WatchDogService
import sk.hudak.prco.utils.Validate.notNull
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.stream.Collectors

@Service("watchDogService")
class WatchDogServiceImpl : WatchDogService {

    @Autowired
    private val eshopUuidParser: EshopUuidParser? = null

    @Autowired
    private val watchDogEntityDao: WatchDogEntityDao? = null

    @Autowired
    private val mailSender: MailSender? = null

    companion object {
        val log = LoggerFactory.getLogger(WatchDogServiceImpl::class.java)!!
    }

    override fun addNewProductToWatch(addDto: WatchDogAddCustomDto): Long? {
        notNull(addDto, "addDto")
        notNullNotEmpty(addDto.productUrl, "productUrl")
        notNull(addDto.maxPriceToBeInterestedIn, "maxPriceToBeInterestedIn")

        // check if already exist with this URL
        if (watchDogEntityDao!!.existWithUrl(addDto.productUrl!!)) {
            throw PrcoRuntimeException("Product with URL " + addDto.productUrl + " already exist")
        }

        val entity = WatchDogEntity()
        entity.eshopUuid = eshopUuidParser!!.parseEshopUuid(addDto.productUrl!!)
        entity.productUrl = addDto.productUrl
        entity.maxPriceToBeInterestedIn = addDto.maxPriceToBeInterestedIn

        val id = watchDogEntityDao.save(entity)
        log.debug("create new entity {} with id {}", entity.javaClass.simpleName, entity.id)

        return id
    }

    override fun findProductsForWatchDog(): Map<EshopUuid, List<WatchDogDto>> {
        val watchDogEntityList = watchDogEntityDao!!.findAll()
        log.debug("count of all watch dog products: {}", watchDogEntityList.size)

        val collect = watchDogEntityList.stream().map { t ->
            //FIXME orika
            val dto = WatchDogDto()
            dto.id = t.id
            dto.eshopUuid = t.eshopUuid
            dto.productUrl = t.productUrl
            dto.maxPriceToBeInterestedIn = t.maxPriceToBeInterestedIn
            dto
        }.collect(Collectors.toList())

        if (collect.isEmpty()) {
            return emptyMap()
        }

        val result = EnumMap<EshopUuid, MutableList<WatchDogDto>>(EshopUuid::class.java)

        for (watchDogDto in collect) {
            var watchDogDtos: MutableList<WatchDogDto>? = result[watchDogDto.eshopUuid]
            if (watchDogDtos == null) {
                result[watchDogDto.eshopUuid] = ArrayList(1)
            }
            watchDogDtos = result[watchDogDto.eshopUuid]
            watchDogDtos!!.add(watchDogDto)
        }
        return result
    }

    override fun notifyByEmail(toBeNotified: List<WatchDogNotifyUpdateDto>) {
        val simpleMessage = SimpleMailMessage()
        simpleMessage.from = "noreply@sk.hudak.prco.com"
        simpleMessage.subject = "watch dog products"
        simpleMessage.setTo("hudakjan83@gmail.com")
        simpleMessage.text = buildTextBody(toBeNotified)
        log.warn("not yet implemeted")

        println(toBeNotified)

        //        mailSender.send(simpleMessage);
        //        log.debug("email sent successfully");

    }

    private fun buildTextBody(toBeNotified: List<WatchDogNotifyUpdateDto>): String {
        val sb = StringBuilder()
        //TODO
        sb.append("aloha")
        return sb.toString()
    }
}
