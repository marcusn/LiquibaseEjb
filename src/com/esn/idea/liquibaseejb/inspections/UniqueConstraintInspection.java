package com.esn.idea.liquibaseejb.inspections;

import com.esn.idea.liquibaseejb.fix.AddUniqueConstraintFix;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraint;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraintType;
import com.esn.idea.liquibaseejb.util.StringUtils;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 16:52:26
 */
public class UniqueConstraintInspection extends DiffModelInspection
{
    public void visitCreateConstraint(String tableName, String name, DatabaseConstraint databaseConstraint)
    {
        if (databaseConstraint.getType().equals(DatabaseConstraintType.UNIQUE))
        {
            DatabaseConstraint constraintOnSameColumns = getLiquibaseDatabaseModel().getUniqueConstraint(tableName, databaseConstraint.getColumnNames());

            if (constraintOnSameColumns == null)
            {
                if (getLiquibaseDatabaseModel().existsTableColumns(tableName, databaseConstraint.getColumnNames()))
                {
                    registerProblem("Unique index on columns " + StringUtils.join(databaseConstraint.getColumnNames(), ",") + " is not created by Liquibase", new AddUniqueConstraintFix(getLiquibaseModuleComponent(), tableName, databaseConstraint));
                }
            }
        }
    }
}
