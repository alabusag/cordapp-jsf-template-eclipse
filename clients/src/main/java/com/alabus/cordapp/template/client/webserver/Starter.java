package com.alabus.cordapp.template.client.webserver;

import static org.springframework.boot.WebApplicationType.SERVLET;

import java.util.EnumSet;

import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;

import org.apache.myfaces.webapp.StartupServletContextListener;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Our Spring Boot application.
 */
@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@ServletComponentScan
@ComponentScan({ "com.alabus.cordapp.template.client" })
public class Starter extends SpringBootServletInitializer {

	/**
	 * Starts our Spring Boot application.
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Starter.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.setWebApplicationType(SERVLET);
		app.run(args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Starter.class, ServletCustomContextInitializer.class);
    }
	
	@Bean
	public ServletListenerRegistrationBean<StartupServletContextListener> jsfConfigureListener() {
		return new ServletListenerRegistrationBean<StartupServletContextListener>(new StartupServletContextListener());
	}

	@Bean
	public ServletRegistrationBean facesServletRegistration() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new FacesServlet(), "*.xhtml");
		servletRegistrationBean.addUrlMappings("/node/*");
		servletRegistrationBean.setLoadOnStartup(1);
		return servletRegistrationBean;
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		FacesServlet servlet = new FacesServlet();
		return new ServletRegistrationBean(servlet, "/node/*");
	}

	@Bean
	public FilterRegistrationBean rewriteFilter() {
		FilterRegistrationBean rwFilter = new FilterRegistrationBean(new RewriteFilter());
		rwFilter.setDispatcherTypes(EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR));
		rwFilter.addUrlPatterns("/*");
		return rwFilter;
	}

}
