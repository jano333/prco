package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.NotInterestedProductDbDao
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.service.NotInterestedProductService

@Service("notInterestedProductService")
class NotInterestedProductServiceImpl(
        @Autowired private val notInterestedProductDbDao: NotInterestedProductDbDao
) : NotInterestedProductService {

    companion object {
        val log = LoggerFactory.getLogger(NotInterestedProductServiceImpl::class.java)!!
    }

    override fun deleteNotInterestedProducts(vararg notInterestedProductIds: Long) {
        for (notInterestedProductId in notInterestedProductIds) {
            notInterestedProductDbDao.delete(notInterestedProductDbDao.findById(notInterestedProductId))
            log.debug("product with id {} was removed", notInterestedProductId)
        }
    }

    override fun deleteNotInterestedProducts(eshopUuid: EshopUuid) {
        for (entity in notInterestedProductDbDao.findAll(NotInterestedProductFindDto(eshopUuid))) {
            notInterestedProductDbDao.delete(entity)
        }
    }
}
