/*
 * Copyright (c) 2013 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.opal.core.service.security;

import javax.validation.ConstraintViolationException;

import org.obiba.opal.core.domain.user.Group;
import org.obiba.opal.core.domain.user.SubjectCredentials;
import org.obiba.opal.core.service.SystemService;

public interface SubjectCredentialsService extends SystemService {

  /**
   * Returns the list of users
   *
   * @return a list of subjectCredentials instances
   */
  Iterable<SubjectCredentials> getSubjectCredentials();

  /**
   * Returns the subjectCredentials with the specified login
   *
   * @param login the unique login to match
   * @return the subjectCredentials with the specified login or null if none exist
   */
  SubjectCredentials getSubjectCredentials(String name);

  /**
   * Create a subjectCredentials when id is not provided, otherwise, updates the changed fields
   *
   * @param subjectCredentials
   */
  void save(SubjectCredentials subjectCredentials) throws ConstraintViolationException;

  /**
   * Deletes a subjectCredentials from subjectCredentials table and from subject_acl
   *
   * @param subjectCredentials
   */
  void delete(SubjectCredentials subjectCredentials);

  /**
   * Create the given group.
   *
   * @return
   */
  void createGroup(String name) throws ConstraintViolationException;

  /**
   * Returns the list of groups
   *
   * @return
   */
  Iterable<Group> getGroups();

  /**
   * Returns the group with the specified name
   *
   * @param name
   * @return the group with the specified name or null if none exist
   */
  Group getGroup(String name);

  /**
   * Deletes a group from group table and from subject_acl
   *
   * @param group
   */
  void delete(Group group);

  long countGroups();
}