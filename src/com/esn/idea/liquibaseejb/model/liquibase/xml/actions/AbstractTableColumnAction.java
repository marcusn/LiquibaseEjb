package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.TableColumnHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:20:27
 */
public abstract class AbstractTableColumnAction implements ChangeSetAction, TableColumnHolderAction
{
	public void execute(DatabaseModel model)
	{
		DatabaseColumnModel columnModel = model.getColumnModel(getTableName().getStringValue(), getColumnName().getStringValue());

		if (columnModel != null)
		{
			executeOnColumn(columnModel);
		}

	}

	protected abstract void executeOnColumn(DatabaseColumnModel columnModel);
}
