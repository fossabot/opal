/*
 * Copyright (c) 2014 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.web.gwt.app.client.administration.taxonomies.vocabulary.edit;

import org.obiba.opal.web.gwt.app.client.administration.taxonomies.event.TaxonomyUpdatedEvent;
import org.obiba.opal.web.gwt.app.client.administration.taxonomies.event.VocabularyDeletedEvent;
import org.obiba.opal.web.gwt.app.client.administration.taxonomies.event.VocabularyUpdatedEvent;
import org.obiba.opal.web.gwt.app.client.event.ConfirmationEvent;
import org.obiba.opal.web.gwt.app.client.event.NotificationEvent;
import org.obiba.opal.web.gwt.app.client.i18n.Translations;
import org.obiba.opal.web.gwt.app.client.presenter.ModalPresenterWidget;
import org.obiba.opal.web.gwt.rest.client.ResourceCallback;
import org.obiba.opal.web.gwt.rest.client.ResourceRequestBuilderFactory;
import org.obiba.opal.web.gwt.rest.client.ResponseCodeCallback;
import org.obiba.opal.web.gwt.rest.client.UriBuilders;
import org.obiba.opal.web.model.client.opal.GeneralConf;
import org.obiba.opal.web.model.client.opal.LocaleTextDto;
import org.obiba.opal.web.model.client.opal.TaxonomyDto;
import org.obiba.opal.web.model.client.opal.VocabularyDto;
import org.obiba.opal.web.model.client.ws.ClientErrorDto;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;

public class VocabularyEditModalPresenter extends ModalPresenterWidget<VocabularyEditModalPresenter.Display>
    implements VocabularyEditModalUiHandlers {

  private final Translations translations;

  private TaxonomyDto originalTaxonomy;

  private VocabularyDto originalVocabulary;

  private RemoveRunnable removeConfirmation;

  private EDIT_MODE mode;

  public enum EDIT_MODE {
    CREATE,
    EDIT
  }

  @Inject
  public VocabularyEditModalPresenter(EventBus eventBus, Display display, Translations translations) {
    super(eventBus, display);
    this.translations = translations;
    getView().setUiHandlers(this);
  }

  @Override
  public void onBind() {
    // Register event handlers
    registerHandler(getEventBus().addHandler(ConfirmationEvent.getType(), new RemoveConfirmationEventHandler()));
  }

  @Override
  public void onSave(String name, boolean repeatable, JsArray<LocaleTextDto> titles,
      JsArray<LocaleTextDto> descriptions) {
    final VocabularyDto dto = VocabularyDto.create();
    dto.setName(name);
    dto.setTitleArray(titles);
    dto.setDescriptionArray(descriptions);
    dto.setRepeatable(repeatable);

    if(mode == EDIT_MODE.EDIT) {
      dto.setTermsArray(originalVocabulary.getTermsArray());

      ResourceRequestBuilderFactory.<TaxonomyDto>newBuilder().forResource(
          UriBuilders.SYSTEM_CONF_TAXONOMY_VOCABULARY.create()
              .build(originalTaxonomy.getName(), originalVocabulary.getName()))//
          .withResourceBody(VocabularyDto.stringify(dto))//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              getView().hide();
              getEventBus().fireEvent(new VocabularyUpdatedEvent(originalTaxonomy.getName(), dto.getName()));
            }
          }, Response.SC_OK, Response.SC_CREATED)//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              if(response.getText() != null && response.getText().length() != 0) {
                fireEvent(NotificationEvent.newBuilder().error(response.getText()).build());
              }
            }
          }, Response.SC_BAD_REQUEST, Response.SC_INTERNAL_SERVER_ERROR)//
          .put().send();
    } else {
      ResourceRequestBuilderFactory.<TaxonomyDto>newBuilder().forResource(
          UriBuilders.SYSTEM_CONF_TAXONOMY_VOCABULARIES.create().build(originalTaxonomy.getName()))//
          .withResourceBody(VocabularyDto.stringify(dto))//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              getView().hide();
              getEventBus().fireEvent(new TaxonomyUpdatedEvent(originalTaxonomy.getName()));
            }
          }, Response.SC_OK, Response.SC_CREATED)//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              if(response.getText() != null && response.getText().length() != 0) {
                fireEvent(NotificationEvent.newBuilder().error(response.getText()).build());
              }
            }
          }, Response.SC_BAD_REQUEST, Response.SC_INTERNAL_SERVER_ERROR)//
          .post().send();
    }
  }

  public void initView(final TaxonomyDto taxonomyDto, final VocabularyDto vocabularyDto) {
    originalTaxonomy = taxonomyDto;
    originalVocabulary = vocabularyDto;
    mode = vocabularyDto.hasName() ? EDIT_MODE.EDIT : EDIT_MODE.CREATE;

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
            getView().setVocabulary(vocabularyDto, locales);
          }
        }).get().send();
  }

  private boolean uniqueVocabularyName(String name) {
    for(int i = 0; i < originalTaxonomy.getVocabulariesCount(); i++) {
      if(originalTaxonomy.getVocabularies(i).equals(name)) {
        showMessage("VOCABULARY", translations.userMessageMap().get("VocabularyNameMustBeUnique"));
        return false;
      }
    }
    return true;
  }

  void showMessage(String id, String message) {
    getView().showError(Display.FormField.valueOf(id), message);
  }

  public interface Display extends PopupView, HasUiHandlers<VocabularyEditModalUiHandlers> {

    enum FormField {
      VOCABULARY
    }

    void setMode(EDIT_MODE editionMode);

    void setVocabulary(VocabularyDto vocabulary, JsArrayString locales);

    void showError(FormField formField, String message);
  }

  // Remove group/user confirmation event
  private class RemoveRunnable implements Runnable {

    private final String name;

    RemoveRunnable(String name) {
      this.name = name;
    }

    @Override
    public void run() {
      ResourceRequestBuilderFactory.newBuilder().forResource(
          UriBuilders.SYSTEM_CONF_TAXONOMY_VOCABULARY.create().build(originalTaxonomy.getName(), name))//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              getView().hide();
              getEventBus().fireEvent(new VocabularyDeletedEvent(originalTaxonomy.getName(), name));
            }
          }, Response.SC_OK)//
          .withCallback(new ResponseCodeCallback() {
            @Override
            public void onResponseCode(Request request, Response response) {
              if(response.getText() != null && response.getText().length() != 0) {
                ClientErrorDto error = JsonUtils.unsafeEval(response.getText());
                getEventBus().fireEvent(
                    NotificationEvent.newBuilder().error(error.getStatus()).args(error.getArgumentsArray()).build());
              }
            }
          }, Response.SC_BAD_REQUEST, Response.SC_INTERNAL_SERVER_ERROR)//
          .delete().send();
    }
  }

  private class RemoveConfirmationEventHandler implements ConfirmationEvent.Handler {

    @Override
    public void onConfirmation(ConfirmationEvent event) {
      if(removeConfirmation != null && event.getSource().equals(removeConfirmation) && event.isConfirmed()) {
        removeConfirmation.run();
        removeConfirmation = null;
      }
    }
  }

}
