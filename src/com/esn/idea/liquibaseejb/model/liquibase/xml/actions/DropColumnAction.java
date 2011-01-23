package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ColumnHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:51:57
 */
public abstract class DropColumnAction implements TableHolderAction, ColumnHolderAction, ChangeSetAction
{

	public void execute(DatabaseModel model)
	{
		 model.dropColumn(getTableName().getStringValue(), getColumnName().getStringValue());
	}
}
