package sk.hudak.prco.test

import sk.hudak.prco.manager.impl.GroupProductResolverImpl

object GroupProductResolverImplTest {

    @JvmStatic
    fun main(args: Array<String>) {

        val productName = "Pampers Active Baby-dry 4 Maxi 36 ks 7-14 kg"

        val groupProductKeywords = GroupProductResolverImpl().resolveGroup(productName)
        groupProductKeywords?.let {
            println(groupProductKeywords)
        } ?: println("not found")


    }
}
