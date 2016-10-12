/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.core.runtime;

/**
 * A service listener is called when service state changes, allowing to perform clean-up operations.
 */
public interface ServiceListener<S extends Service> {

  /**
   * Called after service is started.
   * @param service
   */
  void onServiceStart(S service);

  /**
   * Called before service is stopped.
   * @param service
   */
  void onServiceStop(S service);

}
