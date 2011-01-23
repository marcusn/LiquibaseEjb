package com.esn.idea.liquibaseejb.fix;

import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseColumn;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseConstraints;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ColumnsHolderAction;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.AddUniqueConstraintAction;
import com.intellij.codeInspection.LocalQuickFix;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 16:58:52
 */
public abstract class AbstractAddChangesetFix implements LocalQuickFix
{
	protected LiquibaseModuleComponent liquibaseModuleComponent;
	private String name;
    static private AtomicLong nextId = new AtomicLong(System.currentTimeMillis() / 1000);

    public AbstractAddChangesetFix(LiquibaseModuleComponent liquibaseModuleComponent, String changeSetDesc)
	{
		this.liquibaseModuleComponent = liquibaseModuleComponent;
		this.name = "Add Liquibase ChangeSet: " + changeSetDesc;
	}

    protected long generateId()
    {
        return nextId.getAndIncrement();
    }


    @NotNull
	public String getName()
	{
		return name;
	}

	@NotNull
	public String getFamilyName()
	{
		return "LiquibaseAction";
	}

	protected void addColumnTag(DatabaseField fieldToAdd, ChangeSet changeSet, ColumnsHolderAction columnHolder, boolean addConstraints)
	{
		DatabaseColumnModel columnModel = fieldToAdd.getColumnModel();

		LiquibaseColumn column = columnHolder.addColumn();
		column.getName().setStringValue(fieldToAdd.getColumnName());
		column.getType().setStringValue(columnModel.getType());

		if (columnModel.getDefaultValue() != null)
		{
			column.getDefaultValue().setStringValue(columnModel.getDefaultValue());
		}

        if (addConstraints)
        {
            LiquibaseConstraints constraints = column.getConstraints();
            if (columnModel.getNotNull())
            {
                constraints.getNullable().setStringValue("false");
            }
            if (columnModel.isAutoIncrement())
            {
                column.getAutoIncrement().setStringValue("true");
            }
            if (columnModel.isSinglePrimaryKey())
            {
                constraints.getPrimaryKey().setStringValue("true");
                constraints.getPrimaryKeyName().setStringValue("pk_" + fieldToAdd.getTableName());
            }
            if (columnModel.isUnique())
            {
                String constraintName = "uq_" + fieldToAdd.getColumnName() + "_" + (generateId());
                AddUniqueConstraintAction action = changeSet.addAddUniqueConstraint();
                action.getTableName().setStringValue(fieldToAdd.getTableName());
                action.getColumnNames().setStringValue(fieldToAdd.getColumnName());
                action.getConstraintName().setStringValue(constraintName);
            }
        }
    }

	public Map<String, Collection<DatabaseField>> groupFieldsByTable(Collection<DatabaseField> databaseFields)
	{
		Map<String, Collection<DatabaseField>> tableToFields = new HashMap<String, Collection<DatabaseField>>();
		for (DatabaseField tableField : databaseFields)
		{
			String tableName = tableField.getTableName();
			Collection<DatabaseField> fieldsForTable = tableToFields.get(tableName);
			if (fieldsForTable == null)
			{
				fieldsForTable = new ArrayList<DatabaseField>();
				tableToFields.put(tableName, fieldsForTable);
			}
			fieldsForTable.add(tableField);
		}
		return tableToFields;
	}

	protected ChangeSet newChangeSet()
	{
		return liquibaseModuleComponent.newChangeSet();
	}
}
