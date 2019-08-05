package sk.hudak.prco.configuration

import org.springframework.context.annotation.Configuration

//import org.springframework.social.UserIdSource;
//import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
//import org.springframework.social.config.annotation.EnableSocial;
//import org.springframework.social.config.annotation.SocialConfigurer;
//import org.springframework.social.connect.ConnectionFactoryLocator;
//import org.springframework.social.connect.UsersConnectionRepository;
//import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
//import org.springframework.social.facebook.connect.FacebookConnectionFactory;

@Configuration
//@EnableSocial
open class SocialConfig /*implements SocialConfigurer*///    @Override
//    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
//        cfConfig.addConnectionFactory(new FacebookConnectionFactory(
//                env.getProperty("facebook.appKey"),
//                env.getProperty("facebook.appSecret")));
//    }
//
//    @Override
//    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
//        InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
//
//        //TODO continue https://github.com/spring-projects/spring-social-samples/blob/master/spring-social-quickstart/src/main/java/org/springframework/social/quickstart/config/SocialConfig.java
////        repository.setConnectionSignUp(new SimpleConnectionSignUp);
//        return repository;
//    }
//
//    @Override
//    public UserIdSource getUserIdSource() {
//        return new UserIdSource() {
//            @Override
//            public String getUserId() {
//                return SecurityContext.getCurrentUser().getId();
//            }
//        };
//    }
