package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IndexNameHolderAction;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 17:48:25
 */
public abstract class DropIndexAction implements TableHolderAction, ChangeSetAction, IndexNameHolderAction
{
	public void execute(DatabaseModel model)
	{
		model.dropIndex(getTableName().getStringValue(), getIndexName().getStringValue());
	}
}