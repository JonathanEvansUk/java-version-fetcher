package com.evans.javaversionfetcher;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

@Service
public class RepositoryFetcher {

  private final GitHub gitHub;

  public RepositoryFetcher(GitHub gitHub) {
    this.gitHub = gitHub;
  }

  public Map<String, GHRepository> getAllRepos() throws IOException {
    return gitHub.searchRepositories()
        .user(gitHub.getMyself().getLogin())
        .language("Java")
        .list()
        .toList()
        .stream()
        .collect(Collectors.toMap(GHRepository::getName, Function.identity()));
  }

  public GHRepository getRepositoryByName(String name) throws IOException {
    return gitHub.getMyself().getRepository(name);
  }
}
