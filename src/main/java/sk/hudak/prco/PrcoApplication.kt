package sk.hudak.prco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import sk.hudak.prco.starter.Starter;

@SpringBootApplication
public class PrcoApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(PrcoApplication.class, args);

        Starter starter = ctx.getBean(Starter.class);
        starter.run();

    }
}
