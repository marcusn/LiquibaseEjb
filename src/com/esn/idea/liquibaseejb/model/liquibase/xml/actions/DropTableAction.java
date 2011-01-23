package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:51:57
 */
public abstract class DropTableAction implements TableHolderAction, ChangeSetAction
{
	public void execute(DatabaseModel model)
	{
		model.dropTable(getTableName().getStringValue());
	}
}