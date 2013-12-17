/*******************************************************************************
 * Copyright 2008(c) The OBiBa Consortium. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.opal.core.cfg;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.js.GlobalMethodProvider;
import org.obiba.magma.js.MagmaContextFactory;
import org.obiba.magma.js.MagmaJsExtension;
import org.obiba.magma.xstream.MagmaXStreamExtension;
import org.obiba.opal.core.magma.js.OpalGlobalMethodProvider;
import org.obiba.opal.core.service.security.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

@Component
public class DefaultOpalConfigurationService implements OpalConfigurationService {

  private static final int DATABASE_PASSWORD_LENGTH = 15;

  @Autowired
  private OpalConfigurationIo opalConfigIo;

  @Autowired
  private CryptoService cryptoService;

  private final Lock opalConfigurationLock = new ReentrantLock();

  private final Condition opalConfigAvailable = opalConfigurationLock.newCondition();

  private OpalConfiguration opalConfiguration;

  @Override
  @PostConstruct
  public void start() {
    configureMagma();
    readOpalConfiguration();
    configureSecretKey();
    configureDatabasePassword();
  }

  private void configureMagma() {
    // Add opal specific javascript methods
    Set<GlobalMethodProvider> providers = new HashSet<>();
    providers.add(new OpalGlobalMethodProvider());
    MagmaContextFactory ctxFactory = new MagmaContextFactory();
    ctxFactory.setGlobalMethodProviders(providers);

    MagmaJsExtension jsExtension = new MagmaJsExtension();
    jsExtension.setMagmaContextFactory(ctxFactory);

    // We need these two extensions to read the opal config file
    new MagmaEngine().extend(new MagmaXStreamExtension()).extend(jsExtension);
  }

  private void configureSecretKey() {
    if(Strings.isNullOrEmpty(opalConfiguration.getSecretKey())) {
      modifyConfiguration(new ConfigModificationTask() {
        @Override
        public void doWithConfig(OpalConfiguration config) {
          config.setSecretKey(cryptoService.generateSecretKey());
        }
      });
    }
  }

  private void configureDatabasePassword() {
    if(Strings.isNullOrEmpty(opalConfiguration.getDatabasePassword())) {
      modifyConfiguration(new ConfigModificationTask() {
        @Override
        public void doWithConfig(OpalConfiguration config) {
          String password = new SecureRandomNumberGenerator().nextBytes(DATABASE_PASSWORD_LENGTH).toString();
          config.setDatabasePassword(cryptoService.encrypt(password));
        }
      });
    }
  }

  @Override
  @PreDestroy
  public void stop() {
    MagmaEngine.get().shutdown();
  }

  @Override
  public void readOpalConfiguration() {
    opalConfigurationLock.lock();
    try {
      opalConfiguration = opalConfigIo.readConfiguration();
      opalConfigAvailable.signalAll();
    } finally {
      opalConfigurationLock.unlock();
    }
  }

  @Override
  public OpalConfiguration getOpalConfiguration() {
    opalConfigurationLock.lock();
    try {
      while(opalConfiguration == null) {
        opalConfigAvailable.await();
      }
      return opalConfiguration;
    } catch(InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      opalConfigurationLock.unlock();
    }
  }

  @Override
  public void modifyConfiguration(ConfigModificationTask task) {
    opalConfigurationLock.lock();
    try {
      task.doWithConfig(getOpalConfiguration());
      opalConfigIo.writeConfiguration(opalConfiguration);
    } finally {
      opalConfigurationLock.unlock();
    }
  }

}
