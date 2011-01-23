package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.CreateTableAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.AddPrimaryKeyAction;
import com.esn.idea.liquibaseejb.util.StringUtils;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 10:53:18
 */
public class CreateTableFix extends AbstractAddChangesetFix
{
	private Collection<DatabaseField> fields;

	public CreateTableFix(LiquibaseModuleComponent liquibaseModuleComponent, Collection<DatabaseField> fields, String changeSetDesc)
	{
		super(liquibaseModuleComponent, changeSetDesc);
		this.fields = fields;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{

		Map<String, Collection<DatabaseField>> tableToFields = groupFieldsByTable(fields);

		ChangeSet changeSet = liquibaseModuleComponent.newChangeSet();

		for (Map.Entry<String,Collection<DatabaseField>> entry : tableToFields.entrySet())
		{
			String tableName = entry.getKey();
			Collection<DatabaseField> tableFields = entry.getValue();

			CreateTableAction createTable = changeSet.addCreateTable();
			createTable.getTableName().setStringValue(tableName);

            List<String> primaryKey = new ArrayList<String>();
            for (DatabaseField tableField : tableFields)
			{
				addColumnTag(tableField, changeSet, createTable, true);
                if (tableField.getColumnModel().isPrimaryKey()) primaryKey.add(tableField.getColumnName());
            }

            if (primaryKey.size() > 1)
            {
                AddPrimaryKeyAction addPrimaryKeyAction = changeSet.addAddPrimaryKey();
                addPrimaryKeyAction.getColumnNames().setStringValue(StringUtils.join(primaryKey, ","));
                addPrimaryKeyAction.getTableName().setStringValue(tableName);
                addPrimaryKeyAction.getConstraintName().setStringValue("pk" + tableName);
            }
        }
	}

}
