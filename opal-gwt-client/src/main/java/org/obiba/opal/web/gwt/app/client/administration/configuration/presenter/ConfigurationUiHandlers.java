package org.obiba.opal.web.gwt.app.client.administration.configuration.presenter;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ConfigurationUiHandlers extends UiHandlers {

  void onEditGeneralSettings();

  void onDownloadCertificate();

  void onCreateKeyPair();

  void onImportKeyPair();
}