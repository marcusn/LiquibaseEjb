package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IDropForeignKeyConstraintAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class DropForeignKeyConstraintAction implements ChangeSetAction, IDropForeignKeyConstraintAction
{
	public void execute(DatabaseModel model)
	{
		model.dropConstraint(getBaseTableName().getStringValue(), getConstraintName().getStringValue());
	}
}