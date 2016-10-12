/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.web.gwt.app.client.administration.taxonomies.edit;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import org.obiba.opal.web.gwt.app.client.administration.taxonomies.event.TaxonomyUpdatedEvent;
import org.obiba.opal.web.gwt.app.client.event.NotificationEvent;
import org.obiba.opal.web.gwt.app.client.presenter.ModalPresenterWidget;
import org.obiba.opal.web.gwt.app.client.validator.FieldValidator;
import org.obiba.opal.web.gwt.app.client.validator.RequiredTextValidator;
import org.obiba.opal.web.gwt.app.client.validator.ViewValidationHandler;
import org.obiba.opal.web.gwt.rest.client.ResourceCallback;
import org.obiba.opal.web.gwt.rest.client.ResourceRequestBuilderFactory;
import org.obiba.opal.web.gwt.rest.client.ResponseCodeCallback;
import org.obiba.opal.web.gwt.rest.client.UriBuilders;
import org.obiba.opal.web.model.client.opal.GeneralConf;
import org.obiba.opal.web.model.client.opal.LocaleTextDto;
import org.obiba.opal.web.model.client.opal.TaxonomyDto;

public class TaxonomyEditModalPresenter extends ModalPresenterWidget<TaxonomyEditModalPresenter.Display>
    implements TaxonomyEditModalUiHandlers {

  private TaxonomyDto originalTaxonomy;

  private EDIT_MODE mode;

  public enum EDIT_MODE {
    CREATE,
    EDIT
  }

  @Inject
  public TaxonomyEditModalPresenter(EventBus eventBus, Display display) {
    super(eventBus, display);
    getView().setUiHandlers(this);
  }

  @Override
  public void onSave(String name, String author, String license, JsArray<LocaleTextDto> titles, JsArray<LocaleTextDto> descriptions) {
    if (!new ViewValidator().validate()) return;

    final TaxonomyDto dto = TaxonomyDto.create();
    dto.setName(name);
    if (!Strings.isNullOrEmpty(author)) dto.setAuthor(author);
    if (!Strings.isNullOrEmpty(license)) dto.setLicense(license);
    dto.setTitleArray(titles);
    dto.setDescriptionArray(descriptions);

    if (mode == EDIT_MODE.EDIT) {
      dto.setVocabulariesArray(originalTaxonomy.getVocabulariesArray());

      ResourceRequestBuilderFactory.<TaxonomyDto>newBuilder().forResource(
          UriBuilders.SYSTEM_CONF_TAXONOMY.create().build(originalTaxonomy.getName()))//
          .withResourceBody(TaxonomyDto.stringify(dto))//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              getView().hide();
              getEventBus().fireEvent(new TaxonomyUpdatedEvent(dto.getName()));
            }
          }, Response.SC_OK, Response.SC_CREATED)//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              if (response.getText() != null && !response.getText().isEmpty()) {
                fireEvent(NotificationEvent.newBuilder().error(response.getText()).build());
              }
            }
          }, Response.SC_BAD_REQUEST, Response.SC_INTERNAL_SERVER_ERROR)//
          .put().send();
    } else {
      ResourceRequestBuilderFactory.<TaxonomyDto>newBuilder().forResource(
          UriBuilders.SYSTEM_CONF_TAXONOMIES.create().build())//
          .withResourceBody(TaxonomyDto.stringify(dto))//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              getView().hide();
              getEventBus().fireEvent(new TaxonomyUpdatedEvent(dto.getName()));
            }
          }, Response.SC_OK, Response.SC_CREATED)//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              if (response.getText() != null && !response.getText().isEmpty()) {
                fireEvent(NotificationEvent.newBuilder().error(response.getText()).build());
              }
            }
          }, Response.SC_BAD_REQUEST, Response.SC_INTERNAL_SERVER_ERROR)//
          .post().send();
    }
  }

  public void initView(final TaxonomyDto taxonomyDto) {
    originalTaxonomy = taxonomyDto;
    mode = taxonomyDto.hasName() ? EDIT_MODE.EDIT : EDIT_MODE.CREATE;

    ResourceRequestBuilderFactory.<GeneralConf>newBuilder()
        .forResource(UriBuilders.SYSTEM_CONF_GENERAL.create().build())
        .withCallback(new ResourceCallback<GeneralConf>() {
          @Override
          public void onResource(Response response, GeneralConf resource) {
            JsArrayString locales = JsArrayString.createArray().cast();
            for(int i = 0; i < resource.getLanguagesArray().length(); i++) {
              locales.push(resource.getLanguages(i));
            }
            getView().setMode(mode);
            getView().setTaxonomy(taxonomyDto, locales);
          }
        }).get().send();
  }

  private final class ViewValidator extends ViewValidationHandler {

    private ViewValidator() {}

    @Override
    protected Set<FieldValidator> getValidators() {
      Set<FieldValidator> validators = new LinkedHashSet<>();
      validators.add(
          new RequiredTextValidator(getView().getName(), "NameIsRequired", Display.FormField.NAME.name()));

      return validators;
    }

    @Override
    protected void showMessage(String id, String message) {
      getView().showError(Display.FormField.valueOf(id), message);
    }
  }

  public interface Display extends PopupView, HasUiHandlers<TaxonomyEditModalUiHandlers> {

    enum FormField {
      NAME
    }

    HasText getName();

    void setMode(EDIT_MODE editionMode);

    void setTaxonomy(TaxonomyDto taxonomy, JsArrayString locales);

    void showError(FormField formField, String message);
  }

}
