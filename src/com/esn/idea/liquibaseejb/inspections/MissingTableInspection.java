package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.fix.CreateTableFix;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
* Date: 2008-nov-01
* Time: 13:15:57
*/
public class MissingTableInspection extends DiffModelInspection
{
    private List<String> ignoreTableNames = new ArrayList<String>();

    public MissingTableInspection()
    {
    }

    public MissingTableInspection(List<String> ignoreTableNames)
    {
        this.ignoreTableNames = ignoreTableNames;
    }

    public void visitCreateTable(String tableName)
    {
        if (ignoreTableNames.contains(tableName)) return;

        Collection<DatabaseField> fields = getDeclaredDatabaseModel().getTableFields(tableName);

        if (!fields.isEmpty())
        {
            registerProblem("Database table " + tableName + " is not created by Liquibase", new CreateTableFix(getLiquibaseModuleComponent(), fields, "Add table " + tableName + " with all columns"));
        }
    }
}
