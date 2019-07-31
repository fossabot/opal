/*
 * Copyright (c) 2019 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.obiba.opal.core.domain.security;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.NotBlank;
import org.obiba.opal.core.domain.AbstractTimestamped;
import org.obiba.opal.core.domain.HasUniqueProperties;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubjectProfile extends AbstractTimestamped implements HasUniqueProperties {

  @NotNull
  @NotBlank
  private String principal;

  @NotNull
  @NotBlank
  private String realm;

  private Set<Bookmark> bookmarks = new HashSet<>();

  public SubjectProfile() {
  }

  public SubjectProfile(@NotNull String principal, @NotNull String realm) {
    this.principal = principal;
    this.realm = realm;
  }

  @Override
  public List<String> getUniqueProperties() {
    return Lists.newArrayList("principal");
  }

  @Override
  public List<Object> getUniqueValues() {
    return Lists.newArrayList(principal);
  }

  @NotNull
  public String getPrincipal() {
    return principal;
  }

  public void setPrincipal(@NotNull String principal) {
    this.principal = principal;
  }

  @NotNull
  public String getRealm() {
    return realm;
  }

  public String getFirstRealm() {
    return Splitter.on(",").split(realm).iterator().next();
  }

  public void setRealm(@NotNull String realm) {
    this.realm = realm;
  }

  public Set<Bookmark> getBookmarks() {
    return bookmarks;
  }

  public void setBookmarks(Set<Bookmark> bookmarks) {
    this.bookmarks = bookmarks;
  }

  public boolean addBookmark(String resource) {
    if (bookmarks == null) bookmarks = new HashSet<>();
    return bookmarks.add(new Bookmark(resource));
  }

  public boolean removeBookmark(String resource) {
    return bookmarks != null && bookmarks.remove(new Bookmark(resource));
  }

  public boolean removeBookmark(Bookmark bookmark) {
    return bookmarks != null && bookmarks.remove(bookmark);
  }

  public boolean hasBookmark(String resource) {
    return bookmarks != null && bookmarks.contains(new Bookmark(resource));
  }

  public boolean hasBookmarks() {
    return bookmarks != null;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(principal, realm);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SubjectProfile other = (SubjectProfile) obj;
    return Objects.equal(principal, other.principal) && Objects.equal(realm, other.realm);
  }

  @SuppressWarnings("ParameterHidesMemberVariable")
  public static class Builder {

    private SubjectProfile profile;

    private Builder() {
    }

    public static Builder create() {
      Builder builder = new Builder();
      builder.profile = new SubjectProfile();
      return builder;
    }

    public static Builder create(String principal) {
      Builder builder = create();
      builder.principal(principal);
      return builder;
    }

    public Builder principal(String principal) {
      profile.setPrincipal(principal);
      return this;
    }

    public Builder realm(String realm) {
      profile.setRealm(realm);
      return this;
    }

    public SubjectProfile build() {
      return profile;
    }
  }
}
