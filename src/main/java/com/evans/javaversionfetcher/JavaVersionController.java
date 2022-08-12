package com.evans.javaversionfetcher;

import com.evans.javaversionfetcher.JavaVersionService.JavaVersions;
import com.evans.javaversionfetcher.JavaVersionService.RepositorySummary;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class JavaVersionController {

  private static final Logger log = LoggerFactory.getLogger(JavaVersionController.class);
  private final JavaVersionService javaVersionService;

  public JavaVersionController(JavaVersionService javaVersionService) {
    this.javaVersionService = javaVersionService;
  }

  @GetMapping("/repositories")
  public List<RepositorySummary> getJavaRepositories() {
    return javaVersionService.getJavaRepositories();
  }

  @GetMapping("repositories/{name}")
  public JavaVersions getJavaVersionForRepositoryByName(@PathVariable String name) {
    return javaVersionService.fetchJavaVersion(name);
  }

  @GetMapping("/versions")
  public List<JavaVersions> getJavaVersions() {
    long startTime = System.nanoTime();
    List<JavaVersions> javaVersions = javaVersionService.fetchJavaVersions();
    long endTime = System.nanoTime();

    long duration = (endTime - startTime) / 1000000;

    log.info("Fetched Java Versions, time taken: {}ms", duration);
    return javaVersions;
  }
}
