/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.web.project.resource;


import org.obiba.opal.core.domain.ResourceReference;
import org.obiba.opal.core.service.ResourceReferenceService;
import org.obiba.opal.web.model.Projects;
import org.obiba.opal.web.project.Dtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Component
@Path("/project/{project}/resource/{name}")
public class ProjectResourceReferenceResource {

  private final ResourceReferenceService resourceReferenceService;

  @Autowired
  public ProjectResourceReferenceResource(ResourceReferenceService resourceReferenceService) {
    this.resourceReferenceService = resourceReferenceService;
  }

  @GET
  public Projects.ResourceReferenceDto get(@PathParam("project") String project, @PathParam("name") String name) {
    ResourceReference reference = resourceReferenceService.getResourceReference(project, name);
    return Dtos.asDto(reference, resourceReferenceService.createResource(reference));
  }

  @PUT
  public Response update(@PathParam("project") String project, @PathParam("name") String name, Projects.ResourceReferenceDto referenceDto) {
    // check same project
    if (!project.equals(referenceDto.getProject()))
      throw new IllegalArgumentException("Expecting a resource of project: " + project);
    // check it is not a creation
    ResourceReference originalReference = resourceReferenceService.getResourceReference(project, name);
    ResourceReference updatedReference = Dtos.fromDto(referenceDto);
    updatedReference.setCreated(originalReference.getCreated());
    // note that it can be renamed
    resourceReferenceService.delete(originalReference);
    resourceReferenceService.save(updatedReference);
    if (!originalReference.getName().equals(updatedReference.getName())) {
      // TODO change permissions for the new name
    }
    return Response.ok().build();
  }

  @DELETE
  public Response delete(@PathParam("project") String project, @PathParam("name") String name) {
    resourceReferenceService.delete(project, name);
    return Response.noContent().build();
  }

}
