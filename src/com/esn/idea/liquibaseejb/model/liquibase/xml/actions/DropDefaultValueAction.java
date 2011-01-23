package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class DropDefaultValueAction extends AbstractTableColumnAction
{
	protected void executeOnColumn(DatabaseColumnModel columnModel)
	{
		columnModel.setDefaultValue(null);
	}
}