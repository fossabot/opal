/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.opal.spi.r;

import com.google.common.base.Strings;
import org.obiba.opal.spi.resource.Resource;

import java.net.URISyntaxException;

/**
 * Bind the rows of R objects using dplyr and assign result to a symbol in R.
 */
public class ResourceAssignROperation extends AbstractROperation {

  private final String symbol;

  private final Resource resource;
  
  private final String requiredPackage;

  public ResourceAssignROperation(String symbol, Resource resource, String requiredPackage) {
    this.symbol = symbol;
    this.resource = resource;
    this.requiredPackage = requiredPackage;
  }

  @Override
  public void doWithConnection() {
    if (symbol == null) return;
    Resource.Credentials credentials = resource.getCredentials();

    try {
      ensureGitHubPackage("obiba", "resourcer", "master");
      if (!Strings.isNullOrEmpty(requiredPackage))
        loadPackage(requiredPackage);
      String script = String.format("resourcer::newResourceClient(resourcer::newResource(name='%s', url='%s', identity=%s, secret=%s, format=%s))",
          resource.getName(),
          resource.toURI().toString(),
          credentials.getIdentity() == null ? "NULL" : "'" + credentials.getIdentity() + "'",
          credentials.getSecret() == null ? "NULL" : "'" + credentials.getSecret() + "'",
          resource.getFormat() == null ? "NULL" : "'" + resource.getFormat() + "'");
      eval(String.format("is.null(base::assign('%s', %s))", symbol, script), false);
    } catch (URISyntaxException e) {
      throw new RRuntimeException(e);
    }
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append(symbol).append(" <- resource(").append(resource.getName()).append(")\n");
    return buffer.toString();
  }

}
