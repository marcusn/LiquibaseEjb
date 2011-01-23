package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.DropIndexAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-21
 * Time: 18:16:51
 */
public class DropIndexFix extends AbstractAddChangesetFix
{
	private DatabaseField field;
	private String indexName;

	public DropIndexFix(LiquibaseModuleComponent liquibaseModuleComponent, DatabaseField field, String indexName)
	{
		super(liquibaseModuleComponent, "Drop index on field " + field.getColumnName() + " in table " + field.getTableName());
		this.field = field;
		this.indexName = indexName;
	}

	public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor problemDescriptor)
	{
		ChangeSet changeSet = newChangeSet();
		DropIndexAction action = changeSet.addDropIndex();
		action.getIndexName().setStringValue(indexName);
		action.getTableName().setStringValue(field.getTableName());
	}
}