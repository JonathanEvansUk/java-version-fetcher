package com.evans.javaversionfetcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class RegexTest {

  @Test
  public void testRegex() {

    String pom = """
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        	<modelVersion>4.0.0</modelVersion>
                
        	<groupId>org.evans</groupId>
        	<artifactId>chess</artifactId>
        	<version>0.0.1-SNAPSHOT</version>
        	<packaging>jar</packaging>
                
        	<name>chess</name>
        	<url>http://maven.apache.org</url>
                
        	<properties>
        		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        		<maven.compiler.source>1.8</maven.compiler.source>
        		<maven.compiler.target>1.8</maven.compiler.target>
        	</properties>
                
        	<dependencies>
        		<dependency>
        			<groupId>junit</groupId>
        			<artifactId>junit</artifactId>
        			<version>3.8.1</version>
        			<scope>test</scope>
        		</dependency>
                
        		<dependency>
        			<groupId>pl.pragmatists</groupId>
        			<artifactId>JUnitParams</artifactId>
        			<version>1.1.0</version>
        		</dependency>
                
        		<dependency>
        			<groupId>org.projectlombok</groupId>
        			<artifactId>lombok</artifactId>
        			<version>1.16.18</version>
        		</dependency>
                
        		<dependency>
        			<groupId>org.apache.logging.log4j</groupId>
        			<artifactId>log4j-slf4j-impl</artifactId>
        			<version>2.7</version>
        		</dependency>
                
        		<dependency>
        			<groupId>org.apache.logging.log4j</groupId>
        			<artifactId>log4j-api</artifactId>
        			<version>2.6.2</version>
        		</dependency>
        		<dependency>
        			<groupId>org.apache.logging.log4j</groupId>
        			<artifactId>log4j-core</artifactId>
        			<version>2.6.2</version>
        		</dependency>
        		<dependency>
        			<groupId>com.fasterxml.jackson.dataformat</groupId>
        			<artifactId>jackson-dataformat-yaml</artifactId>
        			<version>2.5.0</version>
        		</dependency>
        		<dependency>
        			<groupId>junit</groupId>
        			<artifactId>junit</artifactId>
        			<version>RELEASE</version>
        			<scope>test</scope>
        		</dependency>
        		<dependency>
        			<groupId>org.mockito</groupId>
        			<artifactId>mockito-core</artifactId>
        			<version>2.23.4</version>
        		</dependency>
        	</dependencies>
        </project>""";


//    Pattern pattern  = Pattern.compile("<maven.compiler.source>([\\s\\S]*?)</maven.compiler.source>");
    Pattern pattern  = Pattern.compile("<maven.compiler.source>");

    Matcher matcher = pattern.matcher(pom);

    assertEquals(1, matcher.groupCount());
    assertEquals("1.8", matcher.group(0));
  }
}
