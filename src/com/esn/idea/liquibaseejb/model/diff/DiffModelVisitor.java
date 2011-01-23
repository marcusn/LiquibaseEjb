package com.esn.idea.liquibaseejb.model.diff;

import com.esn.idea.liquibaseejb.model.database.DatabaseConstraint;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraintType;
import com.esn.idea.liquibaseejb.model.database.DatabaseIndex;

import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 12:25:14
 */
@SuppressWarnings ({"UnusedDeclaration"})
public abstract class DiffModelVisitor
{
    public void visitDropTable(String tableName) {}

    public void visitCreateTable(String tableName) {}

    public void visitDropColumn(String tableName, String columnName) {}

    public void visitCreateColumn(String tableName, String columnName) {}

    public void visitColumnDefaultValue(String tableName, String columnName, String oldDefaultValue, String newDefaultValue) {}

    public void visitColumnNotNull(String tableName, String columnName, boolean oldNotNull, boolean newNotNull) {}

    public void visitColumnType(String tableName, String columnName, String oldType, String newType) {}

    public void visitColumnAutoIncrement(String tableName, String columnName, boolean oldAutoIncrement, boolean newAutoIncrement) {}

    public void visitColumnIndex(String tableName, String columnName, boolean oldIndex, boolean newIndex) {}

    public void visitColumnPrimaryKey(String tableName, String columnName, boolean oldPrimaryKey, boolean newPrimaryKey) {}

    public void visitColumnUnique(String tableName, String columnName, boolean oldUnique, boolean newUnique) {}

    public void visitTableEnd(String tableName) {}

    public void visitTableBegin(String tableName) {}

    public void visitColumnBegin(String tableName, String columnName) {}

    public void visitColumnEnd(String tableName, String columnName) {}

    public void visitTablePrimaryKey(String tableName, DatabaseConstraint oldPrimaryKeyConstraint, DatabaseConstraint newPrimaryKeyConstraint)
    {
    }

    public void visitDropConstraint(String tableName, String name, DatabaseConstraint databaseConstraint)
    {
    }

    public void visitCreateConstraint(String tableName, String name, DatabaseConstraint databaseConstraint)
    {
    }

    public void visitConstraintType(String tableName, String name, DatabaseConstraintType oldType, DatabaseConstraintType newType)
    {
    }

    public void visitConstraintColumnNames(String tableName, String name, List<String> oldColumnNames, List<String> newColumnNames)
    {
    }

    public void visitDropIndex(String tableName, String name, DatabaseIndex databaseIndex)
    {
    }

    public void visitCreateIndex(String tableName, String name, DatabaseIndex databaseIndex)
    {
    }

    public void visitIndexColumnNames(String tableName, String name, List<String> oldColumnNames, List<String> newColumnNames)
    {
    }
}
