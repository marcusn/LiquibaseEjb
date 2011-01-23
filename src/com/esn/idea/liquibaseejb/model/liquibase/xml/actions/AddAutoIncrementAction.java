package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IAddAutoIncrementAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class AddAutoIncrementAction extends AbstractTableColumnAction implements IAddAutoIncrementAction
{
	protected void executeOnColumn(DatabaseColumnModel columnModel)
	{
		columnModel.setType(getColumnDataType().getStringValue());
		columnModel.setAutoIncrement(true);
	}
}