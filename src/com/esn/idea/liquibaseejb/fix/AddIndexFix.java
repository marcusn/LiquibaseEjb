package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.database.DatabaseIndex;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseColumn;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.CreateIndexAction;
import com.esn.idea.liquibaseejb.util.StringUtils;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-21
 * Time: 18:16:51
 */
public class AddIndexFix extends AbstractAddChangesetFix
{
	private String indexName;
	private List<String> columnNames;
	private String tableName;

	public AddIndexFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field, String indexName)
	{
		super(liquibaseModuleComponent, "Add index to field " + field.getColumnName() + " in table " + field.getTableName());
		this.indexName = indexName;
		this.columnNames = Arrays.asList(field.getColumnName());
		this.tableName = field.getTableName();
	}

	public AddIndexFix(LiquibaseModuleComponent liquibaseModuleComponent, String tableName, String indexName, DatabaseIndex index)
	{
		super(liquibaseModuleComponent, "Add index to fields " + StringUtils.join(index.getColumnNames(), ",") + " in table " + tableName);
		this.tableName = tableName;
		this.columnNames = index.getColumnNames();
		this.indexName = indexName;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = newChangeSet();
		CreateIndexAction action = changeSet.addCreateIndex();
		action.getIndexName().setStringValue(indexName);
		action.getTableName().setStringValue(tableName);
		for (String columnName : columnNames)
		{
			LiquibaseColumn column = action.addColumn();
			column.getName().setStringValue(columnName);
		}
	}
}
