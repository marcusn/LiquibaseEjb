package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.AddColumnAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 11:52:41
 */
public class AbstractAddColumnsFix extends AbstractAddChangesetFix
{
	protected Collection<DatabaseField> fields;

	public AbstractAddColumnsFix(LiquibaseModuleComponent liquibaseModuleComponent, String changeSetDesc, Collection<DatabaseField> fields)
	{
		super(liquibaseModuleComponent, changeSetDesc);
		this.fields = fields;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = liquibaseModuleComponent.newChangeSet();

		Map<String, Collection<DatabaseField>> tableToFields = groupFieldsByTable(fields);

		for (Map.Entry<String, Collection<DatabaseField>> entry : tableToFields.entrySet())
		{
			String tableName = entry.getKey();
			Collection<DatabaseField> tableFields = entry.getValue();

			AddColumnAction addColumn = changeSet.addAddColumn();
			addColumn.getTableName().setStringValue(tableName);

			for (DatabaseField tableField : tableFields)
			{
				addColumnTag(tableField, changeSet, addColumn, true);
			}
		}

	}
}
