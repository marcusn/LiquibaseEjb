package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.IAddNotNullConstraintAction;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:14:16
 */
public abstract class AddNotNullConstraintAction extends AbstractTableColumnAction implements IAddNotNullConstraintAction
{
	protected void executeOnColumn(DatabaseColumnModel columnModel)
	{
		columnModel.setNotNull(true);
		columnModel.setDefaultValue(getDefaultNullValue().getStringValue());
		String type = getColumnDataType().getStringValue();
		if (type != null)
		{
			columnModel.setType(type);
		}
	}
}
