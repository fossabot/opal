/*
 * Copyright (c) 2013 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.opal.core.runtime.upgrade.support;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.obiba.opal.core.cfg.OpalConfiguration;
import org.obiba.opal.core.cfg.OpalConfigurationService;
import org.obiba.opal.core.domain.database.SqlDatabase;
import org.obiba.opal.core.runtime.database.DatabaseRegistry;
import org.obiba.opal.core.runtime.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpgradeUtils {

  private final static Logger log = LoggerFactory.getLogger(UpgradeUtils.class);

  private final DataSourceFactory dataSourceFactory;

  private final OpalConfigurationService configurationService;

  private final DatabaseRegistry databaseRegistry;

  @Autowired
  public UpgradeUtils(OpalConfigurationService configurationService, DataSourceFactory dataSourceFactory,
      DatabaseRegistry databaseRegistry) {
    this.configurationService = configurationService;
    this.dataSourceFactory = dataSourceFactory;
    this.databaseRegistry = databaseRegistry;
  }

  public static boolean hasHibernateDatasource(DataSource dataSource) {
    try {
      DatabaseMetaData meta = dataSource.getConnection().getMetaData();
      ResultSet res = meta.getTables(null, null, null, new String[] { "TABLE" });
      while(res.next()) {
        if("value_set_value".equalsIgnoreCase(res.getString("TABLE_NAME"))) return true;
      }
    } catch(SQLException e) {
      log.error("Cannot check if database has an HibernateDatasource", e);
    }
    return false;
  }

  public Map<DataSource, String> getConfiguredDatasources() {
    Map<DataSource, String> dataSourceNames = new LinkedHashMap<DataSource, String>();

    OpalConfiguration configuration = configurationService.getOpalConfiguration();
    try {
      for(SqlDatabase database : databaseRegistry.list(SqlDatabase.class)) {
        dataSourceNames.put(dataSourceFactory.createDataSource(database), database.getName());
      }
    } catch(NoSuchElementException e) {
      // ignore
    }
    return dataSourceNames;
  }

}
