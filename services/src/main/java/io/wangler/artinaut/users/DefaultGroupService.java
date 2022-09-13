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
package io.wangler.artinaut.users;

import io.micronaut.transaction.annotation.ReadOnly;
import io.wangler.artinaut.Group;
import io.wangler.artinaut.GroupRepository;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class DefaultGroupService implements GroupService {

  private final GroupRepository groupRepository;
  private final GroupMapper groupMapper;

  @Override
  @ReadOnly
  public List<GroupDto> findAllGroups() {
    return groupRepository.findAll().stream().map(groupMapper::toGroupDto).toList();
  }

  @Override
  @Transactional
  public GroupDto createGroup(String name) {
    Group group = groupRepository.save(groupMapper.toGroup(name));
    return groupMapper.toGroupDto(group);
  }

  @Override
  @Transactional
  public void deleteGroup(UUID groupId) {
    groupRepository.deleteById(groupId);
  }
}
