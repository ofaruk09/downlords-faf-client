package com.faforever.client.patch;

import com.faforever.client.mod.FeaturedModBean;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.task.TaskService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.nio.file.Path;
import java.util.concurrent.CompletionStage;


@Lazy
@Component
@Profile("!local")
public class GitRepositoryFeaturedModUpdater implements FeaturedModUpdater {

  private static final String NON_WORD_CHARACTER_PATTERN = "[^\\w]";

  private final TaskService taskService;
  private final ApplicationContext applicationContext;
  private final PreferencesService preferencesService;

  @Inject
  public GitRepositoryFeaturedModUpdater(TaskService taskService, ApplicationContext applicationContext, PreferencesService preferencesService) {
    this.taskService = taskService;
    this.applicationContext = applicationContext;
    this.preferencesService = preferencesService;
  }

  @Override
  @SuppressWarnings("unchecked")
  public CompletionStage<PatchResult> updateMod(FeaturedModBean featuredMod, @Nullable Integer version) {
    String repoDirName = featuredMod.getGitUrl().replaceAll(NON_WORD_CHARACTER_PATTERN, "");
    Path repositoryDirectory = preferencesService.getGitReposDirectory().resolve(repoDirName);

    GitFeaturedModUpdateTask modUpdateTask = applicationContext.getBean(GitFeaturedModUpdateTask.class);
    modUpdateTask.setRepositoryDirectory(repositoryDirectory);
    modUpdateTask.setGameRepositoryUrl(featuredMod.getGitUrl());

    if (version != null) {
      modUpdateTask.setRef("refs/tags/" + version);
    } else {
      modUpdateTask.setRef("refs/remotes/origin/" + featuredMod.getGitBranch());
    }

    return taskService.submitTask(modUpdateTask).getFuture();
  }

  @Override
  public boolean canUpdate(FeaturedModBean featuredMod) {
    return featuredMod.getGitUrl() != null;
  }
}
