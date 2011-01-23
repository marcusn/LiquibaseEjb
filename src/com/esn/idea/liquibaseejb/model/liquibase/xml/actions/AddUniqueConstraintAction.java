package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ConstraintColumnsHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

import java.util.Arrays;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class AddUniqueConstraintAction implements TableHolderAction, ChangeSetAction, ConstraintColumnsHolderAction
{
	public void execute(DatabaseModel model)
	{
		String columnNames = getColumnNames().getStringValue();
		if (columnNames != null)
		{
			model.addUniqueConstraint(
							getTableName().getStringValue(), getConstraintName().getStringValue(),
							Arrays.asList(columnNames.split("\\,")));
		}
	}
}