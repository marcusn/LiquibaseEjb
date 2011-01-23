package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.DefaultValueHolderAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class AddDefaultValueAction extends AbstractTableColumnAction implements DefaultValueHolderAction
{
	protected void executeOnColumn(DatabaseColumnModel columnModel)
	{
		columnModel.setDefaultValue(getDefaultValue().getStringValue());
	}
}