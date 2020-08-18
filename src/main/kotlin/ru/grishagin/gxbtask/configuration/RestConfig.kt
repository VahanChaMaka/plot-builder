package ru.grishagin.gxbtask.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

@Configuration
@ConfigurationProperties(prefix = "wolframalpha")
open class RestConfig {

    var url = ""
    var id = ""

    @Bean
    open fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    open fun xmlBuilder(factory: DocumentBuilderFactory): DocumentBuilder{
        return factory.newDocumentBuilder()
    }

    @Bean
    open fun xmlBuilderFactory(): DocumentBuilderFactory {
        return DocumentBuilderFactory.newInstance()
    }
}