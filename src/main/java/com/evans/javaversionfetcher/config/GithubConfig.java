package com.evans.javaversionfetcher.config;

import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GithubConfig {

  @Bean
  public GitHub gitHub() throws IOException {
    return GitHubBuilder
        .fromPropertyFile("/Users/jonathan/git/java-version-fetcher/src/main/resources/.github")
        .build();
  }
}
