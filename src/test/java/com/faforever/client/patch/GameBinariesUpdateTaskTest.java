package com.faforever.client.patch;

import com.faforever.client.i18n.I18n;
import com.faforever.client.preferences.Preferences;
import com.faforever.client.preferences.PreferencesService;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class GameBinariesUpdateTaskTest {
  @Rule
  public TemporaryFolder faDirectory = new TemporaryFolder();
  @Rule
  public TemporaryFolder fafBinDirectory = new TemporaryFolder();
  @Mock
  private PreferencesService preferencesService;
  @Mock
  private I18n i18n;

  private GameBinariesUpdateTaskImpl instance;
  private Preferences preferences;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    instance = new GameBinariesUpdateTaskImpl(i18n, preferencesService);

    Path faPath = faDirectory.getRoot().toPath();
    java.nio.file.Files.createDirectories(faPath.resolve("bin"));

    preferences = new Preferences();
    preferences.getForgedAlliance().setPath(faPath);

    when(preferencesService.getFafBinDirectory()).thenReturn(fafBinDirectory.getRoot().toPath());
    when(preferencesService.getPreferences()).thenReturn(preferences);
  }

  @Test
  public void name() throws Exception {
    Path dummyExe = fafBinDirectory.getRoot().toPath().resolve("ForgedAlliance.exe");
    createFileWithSize(dummyExe, 12_444_928);
    GameBinariesUpdateTaskImpl.updateVersionInExe(3660, dummyExe);

    assertThat(Files.hash(dummyExe.toFile(), Hashing.md5()).toString(), is("4de5eed29b45b640fe64aa22808631c3"));
  }

  @Test(expected = IllegalStateException.class)
  public void testNoVersionThrowsException() throws Exception {
    instance.call();
  }

  @Test
  public void testCopyGameFilesToFafBinDirectory() throws Exception {
    Path fafBinPath = fafBinDirectory.getRoot().toPath();
    Path faBinPath = faDirectory.getRoot().toPath().resolve("bin");

    for (String fileName : GameBinariesUpdateTaskImpl.BINARIES_TO_COPY) {
      createFileWithSize(faBinPath.resolve(fileName), 1024);
    }
    createFileWithSize(faBinPath.resolve("splash.png"), 1024);

    instance.copyGameFilesToFafBinDirectory();

    List<Path> resultFiles = java.nio.file.Files.list(fafBinPath).collect(Collectors.toList());

    // Expected all files except splash.png to be copied
    assertThat(resultFiles.size(), is(GameBinariesUpdateTaskImpl.BINARIES_TO_COPY.size()));
    for (String fileName : GameBinariesUpdateTaskImpl.BINARIES_TO_COPY) {
      assertTrue(java.nio.file.Files.exists(fafBinPath.resolve(fileName)));
    }
  }

  private void createFileWithSize(Path file, int size) throws IOException {
    try (RandomAccessFile randomAccessFile = new RandomAccessFile(file.toFile(), "rw")) {
      randomAccessFile.setLength(size);
    }
  }
}
