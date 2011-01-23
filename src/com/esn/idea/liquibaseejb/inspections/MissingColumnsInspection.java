package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.fix.AddColumnFix;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: Marcus Nilsson
* Date: 2008-nov-01
* Time: 13:15:57
*/
public class MissingColumnsInspection extends DiffModelInspection
{
    List<DatabaseField> fieldsToCreate;

    public MissingColumnsInspection()
    {
        fieldsToCreate = new ArrayList<DatabaseField>();
    }

    public void visitTableBegin(String tableName)
    {
        fieldsToCreate.clear();
    }

    public void visitCreateColumn(String tableName, String columnName)
    {
        DatabaseColumnModel columnModel = getDeclaredDatabaseModel().getColumnModel(tableName, columnName);
        fieldsToCreate.add(new DatabaseField(tableName, columnName, columnModel));
    }

    public void visitTableEnd(String tableName)
    {
        if (!fieldsToCreate.isEmpty())
        {
            Collection<String> columnNames = new ArrayList<String>();
            for (DatabaseField databaseField : fieldsToCreate)
            {
                columnNames.add(databaseField.getColumnName());
            }

            String columnNamesDesc = StringUtils.join(columnNames, ",");
            registerProblem("Columns " + columnNamesDesc + " are not created by Liquibase in table " + tableName, new AddColumnFix(getLiquibaseModuleComponent(), new ArrayList<DatabaseField>(fieldsToCreate), "Add " + columnNamesDesc + " to table " + tableName));
        }
    }

}