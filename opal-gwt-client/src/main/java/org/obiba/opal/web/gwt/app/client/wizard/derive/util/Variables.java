/*******************************************************************************
 * Copyright (c) 2011 OBiBa. All rights reserved.
 *  
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.obiba.opal.web.gwt.app.client.wizard.derive.util;

import org.obiba.opal.web.gwt.app.client.js.JsArrays;
import org.obiba.opal.web.model.client.magma.AttributeDto;
import org.obiba.opal.web.model.client.magma.CategoryDto;
import org.obiba.opal.web.model.client.magma.VariableDto;

import com.google.gwt.core.client.JsArray;

//TODO move this class out to derive package (or in magma ?)
public class Variables {
  public static boolean hasCategories(VariableDto variable) {
    return variable.getCategoriesArray() != null && variable.getCategoriesArray().length() > 0;
  }

  /**
   * Return false if variableDto contains at least one non-missing category, otherwise true
   * @param variableDto
   * @return
   */
  public static boolean allCategoriesMissing(VariableDto variableDto) {
    JsArray<CategoryDto> categoriesArray = variableDto.getCategoriesArray();
    if(categoriesArray == null) return true;
    for(int i = 0; i < categoriesArray.length(); i++) {
      if(!categoriesArray.get(i).getIsMissing()) return false;
    }
    return true;
  }

  public static String getScript(VariableDto derived) {
    AttributeDto scriptAttr = null;
    for(AttributeDto attr : JsArrays.toIterable(JsArrays.toSafeArray(derived.getAttributesArray()))) {
      if(attr.getName().equals("script")) {
        scriptAttr = attr;
        break;
      }
    }
    return scriptAttr != null ? scriptAttr.getValue() : "null";
  }

  public static void setScript(VariableDto derived, String script) {
    AttributeDto scriptAttr = getScriptAttribute(derived);
    scriptAttr.setValue(script);
  }

  public static AttributeDto getScriptAttribute(VariableDto derived) {
    AttributeDto scriptAttr = null;
    for(AttributeDto attr : JsArrays.toIterable(derived.getAttributesArray())) {
      if(attr.getName().equals("script")) {
        scriptAttr = attr;
        break;
      }
    }
    if(scriptAttr == null) {
      scriptAttr = AttributeDto.create();
      scriptAttr.setName("script");
      scriptAttr.setValue("null");
      // make sure attributes array is defined
      derived.setAttributesArray(JsArrays.toSafeArray(derived.getAttributesArray()));
      derived.getAttributesArray().push(scriptAttr);
    }
    return scriptAttr;
  }

  public enum ValueType {
    TEXT, DECIMAL, INTEGER, BINARY, BOOLEAN, DATETIME, DATE, LOCALE;

    String label;

    private ValueType() {
      label = name().toLowerCase();
    }

    public String getLabel() {
      return label;
    }

    public boolean is(String value) {
      return label.equals(value.toLowerCase());
    }
  }
}
