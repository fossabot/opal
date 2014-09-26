/*
 * Copyright (c) 2013 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.opal.core.cfg;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.obiba.opal.core.domain.taxonomy.Taxonomy;
import org.obiba.opal.core.domain.taxonomy.Vocabulary;
import org.obiba.opal.core.service.SystemService;

/**
 * Create, update and delete {@link Taxonomy}.
 */
public interface TaxonomyService extends SystemService {

  /**
   * Get all {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}s.
   *
   * @return
   */
  Iterable<Taxonomy> getTaxonomies();

  @Nullable
  Taxonomy getTaxonomy(@NotNull String name);

  /**
   * Save or update a {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}.
   *
   * @param taxonomy
   */
  void saveTaxonomy(@NotNull Taxonomy taxonomy);

  /**
   * Delete a {@link org.obiba.opal.core.domain.taxonomy.Taxonomy} from name.
   *
   * @param name
   */
  void deleteTaxonomy(@NotNull String name);

  /**
   * Get all {@link org.obiba.opal.core.domain.taxonomy.Vocabulary}s of a {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}.
   *
   * @param taxonomy
   * @return
   * @throws NoSuchTaxonomyException
   */
  Iterable<Vocabulary> getVocabularies(@NotNull String taxonomy) throws NoSuchTaxonomyException;

  /**
   * Check if there is a {@link org.obiba.opal.core.domain.taxonomy.Vocabulary} in the {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}.
   *
   * @param taxonomy
   * @param vocabulary
   * @return
   * @throws NoSuchTaxonomyException
   */
  boolean hasVocabulary(@NotNull String taxonomy, @NotNull String vocabulary) throws NoSuchTaxonomyException;

  /**
   * Get {@link org.obiba.opal.core.domain.taxonomy.Vocabulary} in the {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}.
   *
   * @param taxonomy
   * @param vocabulary
   * @return
   * @throws NoSuchTaxonomyException
   * @throws NoSuchVocabularyException
   */
  Vocabulary getVocabulary(@NotNull String taxonomy, @NotNull String vocabulary)
      throws NoSuchTaxonomyException, NoSuchVocabularyException;

  /**
   * Save a {@link org.obiba.opal.core.domain.taxonomy.Vocabulary} in the {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}.
   *
   * @param taxonomy
   * @param vocabulary
   * @throws NoSuchTaxonomyException
   */
  void saveVocabulary(@NotNull String taxonomy, @NotNull Vocabulary vocabulary) throws NoSuchTaxonomyException;

  /**
   * Delete a {@link org.obiba.opal.core.domain.taxonomy.Vocabulary} from the {@link org.obiba.opal.core.domain.taxonomy.Taxonomy}.
   *
   * @param taxonomy
   * @param vocabulary
   * @throws NoSuchTaxonomyException
   */
  void deleteVocabulary(@NotNull String taxonomy, @NotNull String vocabulary) throws NoSuchTaxonomyException;

}
