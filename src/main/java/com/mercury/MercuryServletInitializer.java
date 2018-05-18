package com.mercury;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * web容器中进行部署
 * 
 * @author Sendy
 */
public class MercuryServletInitializer extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        return application.sources( MercuryApplication.class);
    }

}
