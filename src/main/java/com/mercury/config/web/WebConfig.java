package com.mercury.config.web;

import com.bstek.ureport.console.UReportServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * web 配置类 代替web.xml
 *
 * @author Sendy
 */
@Configuration
public class WebConfig {
	
	@Bean
	@ConditionalOnClass(value=UReportServlet.class)
	public ServletRegistrationBean ureportServletRegistration() {
		ServletRegistrationBean registration = new ServletRegistrationBean();
		registration.setServlet(new com.bstek.ureport.console.UReportServlet());
		registration.addUrlMappings("/ureport/*");
		return registration;
	}
}
