package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ConstraintColumnsHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class DropPrimaryKeyAction implements TableHolderAction, ChangeSetAction, ConstraintColumnsHolderAction
{
	public void execute(DatabaseModel model)
	{
        model.dropConstraint(getTableName().getStringValue(), getConstraintName().getStringValue());
    }
}