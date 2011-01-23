package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.fix.AddForeignKeyConstraintFix;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraint;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraintType;
import com.esn.idea.liquibaseejb.model.database.DatabaseForeignConstraint;
import com.intellij.openapi.util.text.StringUtil;

import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 16:52:26
 */
public class ForeignConstraintInspection extends DiffModelInspection
{
    public void visitCreateConstraint(String tableName, String name, DatabaseConstraint databaseConstraint)
    {
        if (databaseConstraint.getType() == DatabaseConstraintType.FOREIGNKEY)
        {
            DatabaseForeignConstraint foreignConstraint = (DatabaseForeignConstraint) databaseConstraint;

            List<String> columnNames = databaseConstraint.getColumnNames();

            // Constraint may exist under different name
            boolean hasAlreadyFK = getLiquibaseDatabaseModel().getForeignKeyConstraint(tableName, columnNames) != null;
            if (!hasAlreadyFK)
            {
                if (getLiquibaseDatabaseModel().existsTableColumns(foreignConstraint.getTargetTableName(), foreignConstraint.getTargetColumnNames()) &&
                        getLiquibaseDatabaseModel().existsTableColumns(tableName, columnNames))
                {
                    registerProblem(
                            "Liquibase does not create foreign key constraint for columns " + StringUtil.join(columnNames, ",") + " in table " + tableName + " to columns " + StringUtil.join(foreignConstraint.getTargetColumnNames(), ",") + " in table " + foreignConstraint.getTargetTableName(),
                            new AddForeignKeyConstraintFix(getLiquibaseModuleComponent(), tableName, foreignConstraint));
                }
            }
        }
    }
}