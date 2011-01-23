package com.esn.idea.liquibaseejb.model.ejb.context;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.intellij.javaee.model.common.persistence.mapping.ColumnBase;
import com.intellij.javaee.model.common.persistence.mapping.JoinColumnBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-30
 * Time: 08:44:42
 */
public abstract class EjbModelContext
{
    public abstract String getTableName();

    public abstract List<String> getSecondaryTableNames();

    public List<String> getAllTableNames()
    {
        List<String> res = new ArrayList<String>();

        res.addAll(getSecondaryTableNames());

        String tableName = getTableName();
        if (tableName != null) res.add(tableName);

        return res;
    }

    public abstract boolean isId();

    public abstract String getColumnPrefix();

    public void execute(DatabaseModel databaseModel)
    {
        String tableName = getTableName();
        if (tableName != null)
        {
            if (!databaseModel.existsTable(tableName))
            {
                databaseModel.addTable(tableName);
            }
        }
    }

    public abstract boolean isNotId();

    public abstract Boolean getOverrideUnique();

    public abstract Boolean getOverrideNullable();

    public abstract ColumnBase getAttributeOverride(String name);

    public abstract List<JoinColumnBase> getAssociationOverride(String name);

    public abstract boolean isOnlyId();
}
