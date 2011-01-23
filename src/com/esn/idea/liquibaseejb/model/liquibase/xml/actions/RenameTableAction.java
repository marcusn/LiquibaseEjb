package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IRenameTableAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 13:24:32
 */
public abstract class RenameTableAction implements ChangeSetAction, IRenameTableAction
{
	public void execute(DatabaseModel model)
	{
		model.renameTable(getOldTableName().getStringValue(), getNewTableName().getStringValue());

	}
}
