package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.esn.idea.liquibaseejb.model.liquibase.ActionContext;
import com.intellij.util.xml.ConvertContext;

import java.util.Collection;
import java.util.Collections;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-24
 * Time: 15:52:42
 */
public class ColumnConverter extends AbstractConverter
{

    protected Collection<? extends String> getVariantsFromActionContext(ConvertContext convertContext, ActionContext actionContext)
    {
        TableHolderAction tableHolder = convertContext.getInvocationElement().getParentOfType(TableHolderAction.class, false);

        if (tableHolder == null) return Collections.emptyList();

        return actionContext.getColumnNames(tableHolder.getTableName().getStringValue());
    }
}