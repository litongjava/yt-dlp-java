package nexus.io.yt.utils;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import nexus.io.tio.utils.commandline.ProcessResult;

public class YtDlpUpdateTest {
  private ProcessResult run(String status) throws Exception {
    String java = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
    return YtDlp.runUpdate(List.of(java, "-cp", System.getProperty("java.class.path"),
        FakeUpdater.class.getName(), status));
  }

  @Test
  public void successfulUpdatePreservesOutput() throws Exception {
    ProcessResult result = run("0");
    assertEquals(0, result.getExitCode());
    assertTrue(result.getStdOut().contains("updated successfully"));
    assertNotNull(result.getTaskId());
  }

  @Test
  public void failedUpdatePreservesExitCodeAndError() throws Exception {
    ProcessResult result = run("7");
    assertEquals(7, result.getExitCode());
    assertTrue(result.getStdErr().contains("update denied"));
  }

  @Test(expected = java.io.IOException.class)
  public void missingExecutableFails() throws Exception {
    YtDlp.runUpdate(List.of(Paths.get("target", "missing-updater").toAbsolutePath().toString()));
  }

  public static class FakeUpdater {
    public static void main(String[] args) {
      if (args.length != 3 || !"--ignore-config".equals(args[1]) || !"--update".equals(args[2])) {
        System.exit(99);
      }
      int status = Integer.parseInt(args[0]);
      if (status == 0) {
        System.out.println("updated successfully");
      } else {
        System.err.println("update denied");
      }
      System.exit(status);
    }
  }
}
