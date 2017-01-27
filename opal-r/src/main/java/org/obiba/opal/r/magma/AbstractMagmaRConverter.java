/*
 * Copyright (c) 2017 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.r.magma;

import com.google.common.base.Strings;
import org.obiba.magma.*;
import org.obiba.opal.core.magma.IdentifiersMappingView;
import org.rosuda.REngine.REXP;

import java.util.SortedSet;

/**
 * Base implementation of Magma vector providers.
 */
public abstract class AbstractMagmaRConverter implements MagmaRConverter {

  protected MagmaAssignROperation magmaAssignROperation;

  public AbstractMagmaRConverter(MagmaAssignROperation magmaAssignROperation) {
    this.magmaAssignROperation = magmaAssignROperation;
  }

  protected REXP getVector(VariableValueSource vvs, SortedSet<VariableEntity> entities, boolean withMissings) {
    VectorType vt = VectorType.forValueType(vvs.getValueType());
    return vt.asVector(vvs, entities, withMissings);
  }

  protected REXP getVector(Variable variable, Iterable<Value> values, SortedSet<VariableEntity> entities, boolean withMissings) {
    VectorType vt = VectorType.forValueType(variable.getValueType());
    return vt.asVector(variable, values, entities, withMissings);
  }

  protected ValueTable applyIdentifiersMapping(ValueTable table, String idMapping) {
    // If the table contains an entity that requires identifiers separation, create a "identifers view" of the table (replace
    // public (system) identifiers with private identifiers).
    if (!Strings.isNullOrEmpty(idMapping) &&
        magmaAssignROperation.getIdentifiersTableService().hasIdentifiersMapping(table.getEntityType(), idMapping)) {
      // Make a view that converts opal identifiers to unit identifiers
      return new IdentifiersMappingView(idMapping, IdentifiersMappingView.Policy.UNIT_IDENTIFIERS_ARE_PUBLIC, table,
          magmaAssignROperation.getIdentifiersTableService().getIdentifiersTable(table.getEntityType()));
    }
    return table;
  }
}
