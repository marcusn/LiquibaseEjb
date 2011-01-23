package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.fix.AddIndexFix;
import com.esn.idea.liquibaseejb.model.database.DatabaseIndex;
import com.esn.idea.liquibaseejb.util.StringUtils;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 16:52:26
 */
public class IndexInspection extends DiffModelInspection
{
    public void visitCreateIndex(String tableName, String name, DatabaseIndex databaseIndex)
    {
        registerProblem("Index named " + name + " on columns " + StringUtils.join(databaseIndex.getColumnNames(), ",") + " is not created by Liquibase", new AddIndexFix(getLiquibaseModuleComponent(), tableName, name, databaseIndex));
    }
}