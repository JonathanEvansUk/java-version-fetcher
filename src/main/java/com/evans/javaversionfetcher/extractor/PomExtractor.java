package com.evans.javaversionfetcher.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class PomExtractor {

  private final DocumentBuilderFactory documentBuilderFactory;

  public PomExtractor(DocumentBuilderFactory documentBuilderFactory) {
    this.documentBuilderFactory = documentBuilderFactory;
  }

  record JavaVersion(String source,
                     String version) {}

  public Map<String, String> extract(InputStream pom) {
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document parsedPom = documentBuilder.parse(pom);

      return Stream.of("maven.compiler.source", "maven.compiler.target")
          .map(tag -> new JavaVersion(tag, extractTextFromTag(parsedPom, tag)))
          .filter(javaVersion -> javaVersion.version() != null)
          .collect(Collectors.toMap(JavaVersion::source, JavaVersion::version));

    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  private String extractTextFromTag(Document pom, String tag) {
    NodeList elementsByTagName = pom.getElementsByTagName(tag);
    if (elementsByTagName.getLength() > 0) {
      return elementsByTagName.item(0).getTextContent();
    }

    return null;
  }
}
