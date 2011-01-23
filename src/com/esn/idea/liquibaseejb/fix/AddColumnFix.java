package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;

import java.util.Collection;
import java.util.Arrays;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 17:02:04
 */
public class AddColumnFix extends AbstractAddColumnsFix
{

	public AddColumnFix(LiquibaseModuleComponent liquibaseModuleComponent, Collection<DatabaseField> fields, String changeSetDesc)
	{
		super(liquibaseModuleComponent, changeSetDesc, fields);
	}

	public AddColumnFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field, String changeSetDesc)
	{
		this(liquibaseModuleComponent, Arrays.asList(field), changeSetDesc);
	}


}
