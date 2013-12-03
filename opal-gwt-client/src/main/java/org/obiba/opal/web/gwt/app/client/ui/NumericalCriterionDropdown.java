/*
 * Copyright (c) 2013 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.web.gwt.app.client.ui;

import org.obiba.opal.web.model.client.magma.VariableDto;
import org.obiba.opal.web.model.client.search.QueryResultDto;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.common.base.Joiner;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.watopi.chosen.client.event.ChosenChangeEvent;

public abstract class NumericalCriterionDropdown extends CriterionDropdown {

  private Chooser operatorChooser;

  private Chooser rangeValueChooser;

  private ControlLabel minLabel;

  private TextBox min;

  private ControlLabel maxLabel;

  private TextBox max;

  private ControlLabel valuesLabel;

  private TextBox values;

  public NumericalCriterionDropdown(VariableDto variableDto, String fieldName, QueryResultDto termDto) {
    super(variableDto, fieldName, termDto);
  }

  @Override
  public Widget getSpecificControls() {
    operatorChooser = new Chooser();
    rangeValueChooser = new Chooser();
    min = new TextBox();
    max = new TextBox();
    valuesLabel = new ControlLabel();
    values = new TextBox();

    initMinMaxControls();
    initValuesControls();

    ListItem specificControls = new ListItem();
    specificControls.addStyleName("controls");

    specificControls.add(getOperatorsChooserPanel());
    specificControls.add(getRangeValuesChooserPanel());
    specificControls.add(getRangeValuePanel());

    resetSpecificControls();
    return specificControls;
  }

  private void initValuesControls() {
    valuesLabel = new ControlLabel(translations.criterionFiltersMap().get("values"));
    values.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        updateRangeValuesCriterionFilter();
      }
    });
  }

  private void initMinMaxControls() {
    minLabel = new ControlLabel(translations.criterionFiltersMap().get("min"));
    minLabel.setFor(min.getId());
    min.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        updateRangeValuesCriterionFilter();
      }
    });

    maxLabel = new ControlLabel(translations.criterionFiltersMap().get("max"));
    maxLabel.setFor(max.getId());
    max.addKeyUpHandler(new KeyUpHandler() {
      @Override
      public void onKeyUp(KeyUpEvent event) {
        updateRangeValuesCriterionFilter();
      }
    });
  }

  private Widget getRangeValuePanel() {
    FlowPanel panel = new FlowPanel();

    // TODO: Round digit
    min.setPlaceholder(">= " + queryResult.getFacetsArray().get(0).getStatistics().getMin());
    min.setWidth("100px");

    // TODO: Round digit
    max.setPlaceholder("<= " + queryResult.getFacetsArray().get(0).getStatistics().getMax());
    max.setWidth("100px");

    panel.add(createControlGroup(minLabel, min));
    panel.add(createControlGroup(maxLabel, max));
    panel.add(createControlGroup(valuesLabel, values));

    return panel;
  }

  private ControlGroup createControlGroup(ControlLabel label, TextBox textBox) {
    ControlGroup c = new ControlGroup();
    c.addStyleName("inline-block");
    c.add(label);
    c.add(textBox);
    return c;
  }

  private FlowPanel getOperatorsChooserPanel() {
    FlowPanel panel = new FlowPanel();

    operatorChooser.addItem(translations.criterionFiltersMap().get("select_operator"));
    operatorChooser.addItem(translations.criterionFiltersMap().get("in"));
    operatorChooser.addItem(translations.criterionFiltersMap().get("not_in"));
    operatorChooser.addFocusHandler(new FocusHandler() {
      @Override
      public void onFocus(FocusEvent event) {
        resetRadioControls();
      }
    });
    operatorChooser.addChosenChangeHandler(new ChosenChangeEvent.ChosenChangeHandler() {
      @Override
      public void onChange(ChosenChangeEvent event) {
        rangeValueChooser.setEnabled(operatorChooser.getSelectedIndex() > 0);

        if(operatorChooser.getSelectedIndex() > 0) {
          resetRadioControls();
          updateRangeValuesCriterionFilter();
        }
      }
    });

    panel.add(operatorChooser);
    return panel;
  }

  private FlowPanel getRangeValuesChooserPanel() {
    FlowPanel panel = new FlowPanel();

    rangeValueChooser.addItem(translations.criterionFiltersMap().get("select"));
    rangeValueChooser.addItem(translations.criterionFiltersMap().get("range"));
    rangeValueChooser.addItem(translations.criterionFiltersMap().get("values"));
    rangeValueChooser.addFocusHandler(new FocusHandler() {
      @Override
      public void onFocus(FocusEvent event) {
        resetRadioControls();
      }
    });
    rangeValueChooser.addChosenChangeHandler(new UpdateFilterChosenHandler());
    rangeValueChooser.setEnabled(false);

    panel.add(rangeValueChooser);
    return panel;
  }

  @Override
  public void resetSpecificControls() {
    operatorChooser.setItemSelected(0, true);
    rangeValueChooser.setItemSelected(0, true);
    rangeValueChooser.setEnabled(false);
    minLabel.setVisible(false);
    min.setVisible(false);
    maxLabel.setVisible(false);
    max.setVisible(false);
    valuesLabel.setVisible(false);
    values.setVisible(false);
  }

  @Override
  public String getQueryString() {
    String emptyNotEmpty = super.getQueryString();
    if(emptyNotEmpty != null) return emptyNotEmpty;

    // RANGE
    if(rangeValueChooser.isItemSelected(1)) {
      String rangeQuery = fieldName + ":[" + (min.getText().isEmpty() ? "*" : min.getText()) + " TO " +
          (max.getText().isEmpty() ? "*" : max.getText()) + "]";

      if(operatorChooser.isItemSelected(2)) {
        return "NOT " + rangeQuery;
      }
      return rangeQuery;
    }

    // VALUES
    if(rangeValueChooser.isItemSelected(2) && values.getText().length() > 0) {
      // Parse numbers
      String[] numbers = values.getText().split(",");
      String valuesQuery = fieldName + ":(" + Joiner.on(" OR ").join(numbers) + ")";
      if(operatorChooser.isItemSelected(2)) {
        return "NOT " + valuesQuery;
      }
      return valuesQuery;
    }

    return "";
  }

  private void updateRangeValuesCriterionFilter() {
    if(operatorChooser.getSelectedIndex() > 0) {
      String filter = operatorChooser.getItemText(operatorChooser.getSelectedIndex());

      if(rangeValueChooser.getSelectedIndex() > 0) {
        filter += " " + rangeValueChooser.getItemText(rangeValueChooser.getSelectedIndex()).toLowerCase();

        filter += rangeValueChooser.isItemSelected(1) ? "[" + (min.getText().isEmpty() ? "*" : min.getText()) + " " +
            translations.criterionFiltersMap().get("to") + " " +
            (max.getText().isEmpty() ? "*" : max.getText()) + "]" : "(" + values.getText() + ")";
      }

      updateCriterionFilter(filter);
      doFilterValueSets();
    }
  }

  private class UpdateFilterChosenHandler implements ChosenChangeEvent.ChosenChangeHandler {
    @Override
    public void onChange(ChosenChangeEvent chosenChangeEvent) {
      resetRadioControls();

      // Show/Hide Range-value textbox
      if(rangeValueChooser.isItemSelected(1)) {
        minLabel.setVisible(true);
        min.setVisible(true);
        maxLabel.setVisible(true);
        max.setVisible(true);
        valuesLabel.setVisible(false);
        values.setVisible(false);
      } else if(rangeValueChooser.isItemSelected(2)) {
        minLabel.setVisible(false);
        min.setVisible(false);
        maxLabel.setVisible(false);
        max.setVisible(false);
        valuesLabel.setVisible(true);
        values.setVisible(true);
      }

      updateRangeValuesCriterionFilter();
    }
  }

}