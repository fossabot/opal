/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.opal.web.gwt.app.client.magma.event;

import org.obiba.opal.web.gwt.app.client.support.MagmaPath;
import org.obiba.opal.web.model.client.magma.DatasourceDto;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event to indicate that a Magma Datasource has been selected.
 */
public class MagmaPathSelectionEvent extends GwtEvent<MagmaPathSelectionEvent.Handler> {

  public interface Handler extends EventHandler {

    void onMagmaPathSelection(MagmaPathSelectionEvent event);

  }

  private static final Type<Handler> TYPE = new Type<Handler>();

  private final Object source;

  private final String path;


  public MagmaPathSelectionEvent(String path) {
    this(null, path);
  }

  public MagmaPathSelectionEvent(Object source, String path) {
    this.source = source;
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public MagmaPath.Parser getParser() {
    return MagmaPath.Parser.parse(path);
  }

  public Object getSource() {
    return source;
  }

  public static Type<Handler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onMagmaPathSelection(this);
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return getType();
  }
}
