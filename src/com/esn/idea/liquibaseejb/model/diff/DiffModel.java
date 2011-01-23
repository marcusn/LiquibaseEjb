package com.esn.idea.liquibaseejb.model.diff;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseColumnModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseConstraint;
import com.esn.idea.liquibaseejb.model.database.DatabaseIndex;

import java.util.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 12:24:23
 */
public class DiffModel
{
    private DatabaseModel oldModel;
    private DatabaseModel newModel;

    public DiffModel(DatabaseModel oldModel, DatabaseModel newModel)
    {
        this.oldModel = oldModel;
        this.newModel = newModel;
    }

    public void accept(final DiffModelVisitor visitor)
    {
        List<String> oldTableNames = oldModel.getTableNames();
        List<String> newTableNames = newModel.getTableNames();

        new CollectionDiff<String>(oldTableNames, newTableNames)
        {

            public void inFirst(String e)
            {
                visitor.visitDropTable(e);

            }

            public void inSecond(String e)
            {
                visitor.visitCreateTable(e);

            }

            public void inBoth(String e)
            {
                acceptTable(visitor, e);
            }
        }.diff();
    }

    private void acceptTable(final DiffModelVisitor visitor, final String tableName)
    {
        visitor.visitTableBegin(tableName);

        List<String> oldColumnNames = oldModel.getColumnNames(tableName);

        List<String> newColumnNames = newModel.getColumnNames(tableName);

        for (String oldColumnName : oldColumnNames)
        {
            if (!newColumnNames.contains(oldColumnName))
            {
                visitor.visitDropColumn(tableName, oldColumnName);
            }
        }

        for (String newColumnName : newColumnNames)
        {
            if (!oldColumnNames.contains(newColumnName))
            {
                visitor.visitCreateColumn(tableName, newColumnName);
            }
            else
            {
                acceptColumn(visitor, tableName, newColumnName);
            }
        }

        DatabaseConstraint oldPrimaryKeyConstraint = oldModel.getPrimaryKeyConstraint(tableName);
        DatabaseConstraint newPrimaryKeyConstraint = newModel.getPrimaryKeyConstraint(tableName);

        if (notEqualsNullable(oldPrimaryKeyConstraint, newPrimaryKeyConstraint))
        {
            visitor.visitTablePrimaryKey(tableName, oldPrimaryKeyConstraint, newPrimaryKeyConstraint);
        }

        new MapDiff<String, DatabaseConstraint>(oldModel.getConstraints(tableName), newModel.getConstraints(tableName))
        {

            public void inFirst(String name, DatabaseConstraint databaseConstraint)
            {
                visitor.visitDropConstraint(tableName, name, databaseConstraint);

            }

            public void inSecond(String name, DatabaseConstraint databaseConstraint)
            {
                visitor.visitCreateConstraint(tableName, name, databaseConstraint);
            }

            public void inBoth(String name, DatabaseConstraint databaseConstraint1, DatabaseConstraint databaseConstraint2)
            {
                acceptConstraint(visitor, tableName, name, databaseConstraint1, databaseConstraint2);

            }
        }.diff();

        new MapDiff<String, DatabaseIndex>(oldModel.getIndexes(tableName), newModel.getIndexes(tableName))
        {
            public void inFirst(String name, DatabaseIndex databaseIndex)
            {
                visitor.visitDropIndex(tableName, name, databaseIndex);

            }

            public void inSecond(String name, DatabaseIndex databaseIndex)
            {
                visitor.visitCreateIndex(tableName, name, databaseIndex);
            }

            public void inBoth(String name, DatabaseIndex databaseIndex1, DatabaseIndex databaseIndex2)
            {
                acceptIndex(visitor, tableName, name, databaseIndex1, databaseIndex2);

            }
        }.diff();

        visitor.visitTableEnd(tableName);
    }

    private void acceptIndex(DiffModelVisitor visitor, String tableName, String name, DatabaseIndex oldDatabaseIndex, DatabaseIndex newDatabaseIndex)
    {
        if (notEqualsNullable(oldDatabaseIndex.getColumnNames(), newDatabaseIndex.getColumnNames()))
        {
            visitor.visitIndexColumnNames(tableName, name, oldDatabaseIndex.getColumnNames(), newDatabaseIndex.getColumnNames());
        }
    }

    private void acceptConstraint(DiffModelVisitor visitor, String tableName, String name, DatabaseConstraint oldDatabaseConstraint, DatabaseConstraint newDatabaseConstraint)
    {
        if (notEqualsNullable(oldDatabaseConstraint.getType(), newDatabaseConstraint.getType()))
        {
            visitor.visitConstraintType(tableName, name, oldDatabaseConstraint.getType(), newDatabaseConstraint.getType());
        }
        if (notEqualsNullable(oldDatabaseConstraint.getColumnNames(), newDatabaseConstraint.getColumnNames()))
        {
            visitor.visitConstraintColumnNames(tableName, name, oldDatabaseConstraint.getColumnNames(), newDatabaseConstraint.getColumnNames());
        }
    }

    public boolean notEqualsNullable(Object o1, Object o2)
    {
        if (o1 == null) return o2 != null;

        return !o1.equals(o2);
    }

    private void acceptColumn(DiffModelVisitor visitor, String tableName, String columnName)
    {
        visitor.visitColumnBegin(tableName, columnName);

        DatabaseColumnModel oldColumnModel = oldModel.getColumnModel(tableName, columnName);
        DatabaseColumnModel newColumnModel = newModel.getColumnModel(tableName, columnName);

        if (notEqualsNullable(oldColumnModel.getDefaultValue(), newColumnModel.getDefaultValue()))
        {
            visitor.visitColumnDefaultValue(tableName, columnName, oldColumnModel.getDefaultValue(), newColumnModel.getDefaultValue());
        }

        if (notEqualsNullable(oldColumnModel.getNotNull(), newColumnModel.getNotNull()))
        {
            visitor.visitColumnNotNull(tableName, columnName, oldColumnModel.getNotNull(), newColumnModel.getNotNull());
        }

        if (notEqualsNullable(oldColumnModel.getType(), newColumnModel.getType()))
        {
            visitor.visitColumnType(tableName, columnName, oldColumnModel.getType(), newColumnModel.getType());
        }

        if (notEqualsNullable(oldColumnModel.isAutoIncrement(), newColumnModel.isAutoIncrement()))
        {
            visitor.visitColumnAutoIncrement(tableName, columnName, oldColumnModel.isAutoIncrement(), newColumnModel.isAutoIncrement());
        }

        if (notEqualsNullable(oldColumnModel.isIndex(), newColumnModel.isIndex()))
        {
            visitor.visitColumnIndex(tableName, columnName, oldColumnModel.isIndex(), newColumnModel.isIndex());
        }

        if (notEqualsNullable(oldColumnModel.isPrimaryKey(), newColumnModel.isPrimaryKey()))
        {
            visitor.visitColumnPrimaryKey(tableName, columnName, oldColumnModel.isPrimaryKey(), newColumnModel.isPrimaryKey());
        }

        if (notEqualsNullable(oldColumnModel.isUnique(), newColumnModel.isUnique()))
        {
            visitor.visitColumnUnique(tableName, columnName, oldColumnModel.isUnique(), newColumnModel.isUnique());
        }

        visitor.visitColumnEnd(tableName, columnName);
    }
}
