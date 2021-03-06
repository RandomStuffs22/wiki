/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.wiki.service.wysiwyg;

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.EntityType;
import org.xwiki.model.internal.reference.DefaultStringEntityReferenceSerializer;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceValueProvider;

@Component("compact")
public class CompactStringEntityReferenceSerializer extends DefaultStringEntityReferenceSerializer {

  @Inject
  @Named("current")
  private EntityReferenceValueProvider provider;

  /**
   * {@inheritDoc}
   * 
   * @see org.xwiki.model.internal.reference.DefaultStringEntityReferenceSerializer#serializeEntityReference(org.xwiki.model.reference.EntityReference, java.lang.StringBuilder, boolean, java.lang.Object[])
   */
  @Override
  protected void serializeEntityReference(EntityReference currentReference, StringBuilder representation,
      boolean isLastReference, Object... parameters)
  {
      boolean shouldPrint = false;

      // Only serialize if:
      // - the current entity reference has a different value than the passed reference
      // - the entity type being serialized is not the last type of the chain
      // In addition an entity reference isn't printed only if all parent references are not printed either,
      // otherwise print it. For example "wiki:page" isn't allowed for a Document Reference.

      if (isLastReference || representation.length() > 0) {
          shouldPrint = true;
      } else {
          String defaultName = resolveDefaultValue(currentReference.getType(), parameters);
          if (defaultName == null || !defaultName.equals(currentReference.getName())) {
              shouldPrint = true;
          }
      }

      if (shouldPrint) {
          super.serializeEntityReference(currentReference, representation, isLastReference);
      }
  }

  protected String resolveDefaultValue(EntityType type, Object... parameters)
  {
      String resolvedDefaultValue = null;
      if (parameters.length > 0 && parameters[0] instanceof EntityReference) {
          // Try to extract the type from the passed parameter.
          EntityReference referenceParameter = (EntityReference) parameters[0];
          EntityReference extractedReference = referenceParameter.extractReference(type);
          if (extractedReference != null) {
              resolvedDefaultValue = extractedReference.getName();
          }
      }

      if (resolvedDefaultValue == null) {
          resolvedDefaultValue = this.provider.getDefaultValue(type);
      }

      return resolvedDefaultValue;
  }

  
}
