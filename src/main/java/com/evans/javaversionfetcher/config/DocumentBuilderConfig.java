package com.evans.javaversionfetcher.config;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentBuilderConfig {

  @Bean
  public DocumentBuilderFactory documentBuilderFactory() throws ParserConfigurationException {
    return DocumentBuilderFactory.newInstance();

  }
}
