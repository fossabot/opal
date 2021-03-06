/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.web.gwt.app.client.ui;

import com.google.gwt.user.client.ui.HasEnabled;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RadioButton;

public class DynamicRadioGroup extends Composite implements TakesValue<String>, HasEnabled {

  private String value;
  private final String key;

  private final List<RadioButton> radios;

  private boolean enabled;

  @Override
  public void setValue(String value) {
    this.value = value;

    for (RadioButton radioButton : radios) {
      radioButton.setValue(radioButton.getFormValue().equals(value), false);
    }
  }

  @Override
  public String getValue() {
    return value;
  }

  public String getKey() {
    return key;
  }

  public DynamicRadioGroup(String key, List<String> items) {
    this.enabled = true;
    this.key = key;
    radios = new ArrayList<>();

    FlowPanel panel = new FlowPanel();

    if (items != null) {
      for(String item: items) {
        RadioButton radioButton = new RadioButton(item);
        radioButton.setName(key);
        radioButton.setFormValue(item);

        radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            RadioButton source = (RadioButton) event.getSource();
            Boolean value = event.getValue();

            if (value != null && value) {
              setValue(source.getFormValue());
            }
          }
        });

        radios.add(radioButton);
        radioButton.setText(item);

        FlowPanel radioPanel = new FlowPanel();
        radioPanel.getElement().addClassName("radio");
        radioPanel.add(radioButton);

        panel.add(radioPanel);
      }
    }

    initWidget(panel);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;

    for (RadioButton button : radios) {
      button.setEnabled(enabled);
    }
  }
}
