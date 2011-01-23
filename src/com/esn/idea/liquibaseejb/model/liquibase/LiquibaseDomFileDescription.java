package com.esn.idea.liquibaseejb.model.liquibase;

import com.esn.idea.liquibaseejb.model.liquibase.xml.DatabaseChangeLog;
import com.intellij.util.xml.DomFileDescription;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-31
 * Time: 09:50:45
 */
public class LiquibaseDomFileDescription extends DomFileDescription<DatabaseChangeLog>
{
    public LiquibaseDomFileDescription()
    {
        super(DatabaseChangeLog.class, "databaseChangeLog");
    }
/**
    @NotNull
      public Set<? extends Object> getDependencyItems(final XmlFile file) {
      final Module module = ModuleUtil.findModuleForPsiElement(file);
      return Collections.singleton(module == null ? null : LiquibaseFacet.getInstance(module));
    }
    **/

}
