package sk.hudak.prco.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.mail.MailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
open class Haha {

    @Bean
    open fun propertyPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }

    @Bean
    open fun mailSender(): MailSender {
        val sender = JavaMailSenderImpl()
        //        sender.setHost("smtp.gmail.com");
        //        sender.setUsername("hudakjan83@gmail.com");
        //        sender.setPassword("???");
        //        sender.setProtocol("smtp");
        //        sender.setPort(465);


        val properties = Properties()
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.smtp.host"] = "smtp.gmail.com"
        properties["mail.smtp.user"] = "hudakjan83@gmail.com" // User name
        properties["mail.smtp.password"] = "???" // password
        properties["mail.smtp.port"] = "587"
        properties["mail.smtp.auth"] = "true"


        sender.javaMailProperties = properties

        return sender
    }


    //    <bean id="mailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
    //        <property name="host" value="${batch.mail.host}"/>
    //        <property name="username" value="${batch.mail.username}"/>
    //        <property name="password" value="${batch.mail.password}"/>
    //        <property name="protocol" value="${batch.mail.protocol}"/>
    //        <property name="port" value="${batch.mail.port}"/>
    //    </bean>
}
