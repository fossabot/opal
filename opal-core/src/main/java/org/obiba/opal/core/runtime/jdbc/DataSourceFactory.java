/*******************************************************************************
 * Copyright (c) 2012 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.opal.core.runtime.jdbc;

import javax.transaction.TransactionManager;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.managed.BasicManagedDataSource;
import org.obiba.opal.core.domain.database.SqlDatabase;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSourceFactory {

  private final TransactionManager txmgr;

  @Autowired
  public DataSourceFactory(TransactionManager txmgr) {
    this.txmgr = txmgr;
  }

  public BasicDataSource createDataSource(SqlDatabase database) {
    BasicManagedDataSource bmds = new BasicManagedDataSource();

    BeanWrapperImpl bw = new BeanWrapperImpl(bmds);
    // Set values, ignoring unknown/invalid entries
    bw.setPropertyValues(new MutablePropertyValues(database.readProperties()), true, true);

    // Set other properties
    bmds.setTransactionManager(txmgr);
    bmds.setUrl(database.getUrl());
    bmds.setDriverClassName(database.getDriverClass());
    bmds.setUsername(database.getUsername());
    bmds.setPassword(database.getPassword());

    if(bmds.getMaxWait() < 0) {
      // Wait for 10 seconds maximum
      bmds.setMaxWait(10 * 1000);
    }
    return bmds;
  }

}
