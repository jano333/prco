package sk.hudak.prco

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import sk.hudak.prco.starter.Starter

@SpringBootApplication
open class PrcoApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val ctx = SpringApplication.run(PrcoApplication::class.java, *args)
            val starter = ctx.getBean(Starter::class.java)
            starter.run()
        }
    }
}
