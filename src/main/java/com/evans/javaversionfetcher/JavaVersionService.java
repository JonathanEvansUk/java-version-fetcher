package com.evans.javaversionfetcher;

import com.evans.javaversionfetcher.JavaVersionService.JavaVersions.JavaVersionFileResult;
import com.evans.javaversionfetcher.JavaVersionService.JavaVersions.JavaVersionFileResult.JavaVersionFile;
import com.evans.javaversionfetcher.JavaVersionService.JavaVersions.JavaVersionFileResult.JavaVersionFileEmpty;
import com.evans.javaversionfetcher.JavaVersionService.JavaVersions.JavaVersionFileResult.JavaVersionFileNotFound;
import com.evans.javaversionfetcher.extractor.PomExtractor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GHTreeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JavaVersionService {

  private static final Logger log = LoggerFactory.getLogger(JavaVersionService.class);
  private static final int FETCH_RECURSIVELY = 1;

  private final RepositoryFetcher repositoryFetcher;
  private final PomExtractor pomExtractor;

  public JavaVersionService(RepositoryFetcher repositoryFetcher, PomExtractor pomExtractor) {
    this.repositoryFetcher = repositoryFetcher;
    this.pomExtractor = pomExtractor;
  }

  record RepositorySummary(String name,
                           String url,
                           String defaultBranch) {}

  public List<RepositorySummary> getJavaRepositories() {
    try {
      var repos = repositoryFetcher.getAllRepos();

      record RepositoryByName(String name,
                              GHRepository repository) {}

      return repos.entrySet()
          .stream()
          .map(entry -> new RepositoryByName(entry.getKey(), entry.getValue()))
          .map(repositoryByName ->
              new RepositorySummary(repositoryByName.name(),
                  repositoryByName.repository().getHtmlUrl().toString(),
                  repositoryByName.repository().getDefaultBranch()))
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<JavaVersions> fetchJavaVersions() {
    try {
      Map<String, GHRepository> repos = repositoryFetcher.getAllRepos();
      return repos.values().parallelStream()
          .filter(repo -> "Java".equals(repo.getLanguage()))
          .map(this::identifyJavaVersion)
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JavaVersions fetchJavaVersion(String repositoryName) {
    try {
      GHRepository repository = repositoryFetcher.getRepositoryByName(repositoryName);
      return identifyJavaVersion(repository);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private JavaVersions identifyJavaVersion(GHRepository repository) {

    log.info("Processing repository: {}", repository.getName());
    try {
      GHBranch defaultBranch = repository.getBranch(repository.getDefaultBranch());
      GHTree treeRecursive = repository.getTreeRecursive(defaultBranch.getSHA1(),
          FETCH_RECURSIVELY);

      GHTreeBuilder treeBuilder = repository.createTree().baseTree(treeRecursive.getSha());

      List<GHTreeEntry> allFiles = treeRecursive.getTree().stream()
          .filter(tree -> !"tree".equals(tree.getType())).toList();

      List<GHTreeEntry> pomFiles = allFiles.stream()
          .filter(file -> getFilename(file.getPath()).equals("pom.xml")).toList();

      var javaVersionFiles = allFiles.stream()
          .filter(file -> getFilename(file.getPath()).equals(".java-version"))
          .findAny()
          .map(this::extractJavaVersionFromJavaVersionFile)
          .orElseGet(JavaVersionFileNotFound::new);

      Map<String, Map<String, String>> informationFromPoms = pomFiles.stream()
          .collect(Collectors.toMap(
              GHTreeEntry::getPath,
              file -> {
                try {
                  return pomExtractor.extract(file.readAsBlob());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
          ));

      return new JavaVersions(informationFromPoms, javaVersionFiles);

      //to create new branch with updated files do below
//        String newTreeSha = treeBuilder.create().getSha();
//
//        String newRefName = "refs/heads/" + "java-upgrade-" + LocalDate.now();
//
//        // remove existing refs with same name
//        Arrays.stream(repository.getRefs()).filter(ref -> ref.getRef().contains("java-upgrade"))
//            .forEach(ref -> {
//              try {
//                ref.delete();
//              } catch (IOException e) {
//                throw new RuntimeException(e);
//              }
//            });
//
//        // create new branch
//        GHRef newBranchRef = repository.createRef(newRefName, defaultBranch.getSHA1());
//
//        String commitSha = repository.createCommit().message("Test commit").tree(newTreeSha)
//            .parent(newBranchRef.getObject().getSha()).create().getSHA1();
//
//        newBranchRef.updateTo(commitSha);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  private String getFilename(String path) {
    if (path.contains("/")) {
      String[] pathComponents = path.split("/");
      return pathComponents[pathComponents.length - 1];
    }

    return path;
  }

  private JavaVersionFileResult extractJavaVersionFromJavaVersionFile(GHTreeEntry file) {
    try (InputStream inputStream = file.readAsBlob()) {
      var javaVersion = new String(inputStream.readAllBytes());

      if (javaVersion.isEmpty()) {
        return new JavaVersionFileEmpty();
      }

      return new JavaVersionFile(javaVersion);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public record JavaVersionInformation(String serviceName,
                                       Map<String, Map<String, String>> versionsByFiles) {}

  public record JavaVersions(Map<String, Map<String, String>> pomFileVersionsByPomName,
                             JavaVersionFileResult javaVersionFileResult) {

    sealed interface JavaVersionFileResult {

      @JsonProperty
      Status status();

      enum Status {
        NOT_FOUND,
        EMPTY_FILE,
        FOUND
      }

      record JavaVersionFileNotFound() implements JavaVersionFileResult {

        @Override
        public Status status() {
          return Status.NOT_FOUND;
        }
      }

      record JavaVersionFileEmpty() implements JavaVersionFileResult {

        @Override
        public Status status() {
          return Status.EMPTY_FILE;
        }
      }

      record JavaVersionFile(String version) implements JavaVersionFileResult {

        @Override
        public Status status() {
          return Status.FOUND;
        }
      }
    }
  }


}
