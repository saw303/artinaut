/*
 * MIT License
 * <p>
 * Copyright (c) 2022 Silvio Wangler (silvio@wangler.io)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE
 */
package io.wangler.artinaut.groups;

import static io.micronaut.scheduling.TaskExecutors.IO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.wangler.artinaut.users.GroupDto;
import io.wangler.artinaut.users.GroupService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Controller("/api/v1/groups/")
@RequiredArgsConstructor
public class GroupController implements GroupOperations {

  private final GroupsControllerMapper mapper;
  private final GroupService groupService;

  @Override
  @ExecuteOn(IO)
  public List<GroupGetModel> findAllGroups() {
    return groupService.findAllGroups().stream().map(mapper::toGroupGetModel).toList();
  }

  @Override
  @ExecuteOn(IO)
  public HttpResponse<UUID> createGroup(GroupPostModel model) {
    GroupDto group = groupService.createGroup(model.name());
    return HttpResponse.created(group.id());
  }

  @Override
  @ExecuteOn(IO)
  public void deleteGroup(UUID groupId) {
    groupService.deleteGroup(groupId);
  }
}
