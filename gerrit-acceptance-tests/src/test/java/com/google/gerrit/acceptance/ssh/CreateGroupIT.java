// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.acceptance.ssh;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assert_;

import com.google.gerrit.acceptance.AbstractDaemonTest;
import com.google.gerrit.reviewdb.client.AccountGroup;

import org.junit.Test;

public class CreateGroupIT extends AbstractDaemonTest {

  @Test
  public void withDuplicateInternalGroupCaseSensitiveName_Conflict()
      throws Exception {
    String newGroupName = "dupGroupA";
    adminRestSession.put("/groups/" + newGroupName);
    adminSshSession.exec("gerrit create-group " + newGroupName);
    assert_().withFailureMessage(adminSshSession.getError())
        .that(adminSshSession.hasError()).isTrue();
  }

  @Test
  public void withDuplicateInternalGroupCaseInsensitiveName()
      throws Exception {
    String newGroupName = "dupGroupB";
    String newGroupNameLowerCase = newGroupName.toLowerCase();

    adminRestSession.put("/groups/" + newGroupName);
    adminSshSession.exec("gerrit create-group " + newGroupNameLowerCase);
    assert_().withFailureMessage(adminSshSession.getError())
        .that(adminSshSession.hasError()).isFalse();
    assertThat(groupCache.get(new AccountGroup.NameKey(newGroupName)))
      .isNotNull();
    assertThat(groupCache.get(new AccountGroup.NameKey(newGroupNameLowerCase)))
      .isNotNull();
  }

  @Test
  public void withDuplicateSystemGroupCaseSensitiveName_Conflict()
      throws Exception {
    String newGroupName = "Registered Users";
    adminSshSession.exec("gerrit create-group " + newGroupName);
    assert_().withFailureMessage(adminSshSession.getError())
        .that(adminSshSession.hasError()).isTrue();
  }

  @Test
  public void withDuplicateSystemGroupCaseInsensitiveName_Conflict()
      throws Exception {
    String newGroupName = "Registered Users";
    adminSshSession.exec("gerrit create-group " + newGroupName);
    assert_().withFailureMessage(adminSshSession.getError())
        .that(adminSshSession.hasError()).isTrue();
  }

  @Test
  public void withNonDuplicateGroupName() throws Exception {
    String newGroupName = "newGroupB";
    adminSshSession.exec("gerrit create-group " + newGroupName);
    assert_().withFailureMessage(adminSshSession.getError())
        .that(adminSshSession.hasError()).isFalse();
    AccountGroup accountGroup =
        groupCache.get(new AccountGroup.NameKey(newGroupName));
    assertThat(accountGroup).isNotNull();
    assertThat(accountGroup.getName()).isEqualTo(newGroupName);
  }
}
