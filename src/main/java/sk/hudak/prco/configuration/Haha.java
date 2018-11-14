package sk.hudak.prco.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class Haha {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static MailSender mailSender(){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
//        sender.setHost("smtp.gmail.com");
//        sender.setUsername("hudakjan83@gmail.com");
//        sender.setPassword("???");
//        sender.setProtocol("smtp");
//        sender.setPort(465);



        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.user", "hudakjan83@gmail.com"); // User name
        properties.put("mail.smtp.password", "???"); // password
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");


        sender.setJavaMailProperties(properties);

        return sender;
    }



//    <bean id="mailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
//        <property name="host" value="${batch.mail.host}"/>
//        <property name="username" value="${batch.mail.username}"/>
//        <property name="password" value="${batch.mail.password}"/>
//        <property name="protocol" value="${batch.mail.protocol}"/>
//        <property name="port" value="${batch.mail.port}"/>
//    </bean>
}
