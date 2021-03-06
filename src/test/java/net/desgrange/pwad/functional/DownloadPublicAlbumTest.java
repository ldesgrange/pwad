/**
 *
 * Copyright 2010-2012 Laurent Desgrange
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.desgrange.pwad.functional;

import static org.junit.Assert.assertEquals;
import static org.uispec4j.assertion.UISpecAssert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.desgrange.pwad.functional.utils.PwadTestCase;

import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Button;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.Window;
import org.uispec4j.interception.FileChooserHandler;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

public class DownloadPublicAlbumTest extends PwadTestCase {
  private Window window;
  private TextBox linkField;
  private Button downloadButton;

  @Before
  public void setUp() {
    window = getMainWindow();
    linkField = window.getInputTextBox("Invitation link");
    downloadButton = window.getButton("Download");
  }

  @Test
  public void testUserCanDownloadPublicAlbumFromInvitationLink() throws Exception {
    assertTrue(window.titleEquals("pwad - Picasa Web Albums Downloader"));
    assertTrue(downloadButton.isEnabled());
    linkField.setText(createInvitationLink());

    final File outputFolder = createTempDirectory();
    WindowInterceptor.init(downloadButton.triggerClick())
                .process(FileChooserHandler.init().titleEquals("Select output folder").assertAcceptsDirectoriesOnly().select(outputFolder))
                .process(new WindowHandler() {
                  @Override
                  public Trigger process(final Window dialog) throws Exception {
                    assertTrue(dialog.titleEquals("Downloading…"));
                    assertTrue(dialog.getTextBox().textContains("Downloading picture", "out of", "…"));
                    assertTrue(dialog.getProgressBar().isVisible());
                    assertTrue(dialog.getProgressBar().completionEquals(0));
                    assertTrue(dialog.getProgressBar().isCompleted());
                    return Trigger.DO_NOTHING;
                  }
                }).run();
    final List<String> actualFiles = Arrays.asList(outputFolder.list());
    Collections.sort(actualFiles);
    assertEquals("[100_0001.JPG, 100_0002.JPG]", actualFiles.toString());
  }

  private String createInvitationLink() {
    final String userId = "dead_kennedys";
    final String albumId = "holiday_in_cambodia";
    final String authenticationKey = "the_authentication_key";
    final String invitationId = "invitation_id";
    return "http://picasaweb.google.fr/lh/sredir?uname=" + userId + "&target=ALBUM&id=" + albumId + "&authkey=" + authenticationKey + "&invite=" + invitationId + "&feat=email";
  }

  private File createTempDirectory() throws IOException {
    final File file = File.createTempFile(getClass().getSimpleName(), "");
    file.delete();
    file.mkdir();
    file.deleteOnExit();
    return file;
  }
}
