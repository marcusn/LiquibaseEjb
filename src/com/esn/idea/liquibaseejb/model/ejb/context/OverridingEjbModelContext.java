package com.esn.idea.liquibaseejb.model.ejb.context;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.intellij.javaee.model.common.persistence.mapping.ColumnBase;
import com.intellij.javaee.model.common.persistence.mapping.JoinColumnBase;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-04
 * Time: 07:54:47
 */
public class OverridingEjbModelContext extends EjbModelContext
{
    private EjbModelContext parent;
    private String tableName;
    private HashMap<String, ColumnBase> attributeOverrides = new HashMap<String, ColumnBase>();
    private HashMap<String, List<JoinColumnBase>> associationOverrides = new HashMap<String, List<JoinColumnBase>>();
    private Boolean isId;
    private String columnPrefix = null;
    private Boolean notId = null;
    private Boolean overrideUnique = null;
    private Boolean overrideNullable= null;
    private List<String> secondaryTableNames = new ArrayList<String>();
    private Boolean onlyId = null;

    public OverridingEjbModelContext()
    {
        this(new DefaultEjbModelContext());
    }

    public OverridingEjbModelContext(EjbModelContext parent)
    {
        this.parent = parent;
    }

    public String getTableName()
    {
        return tableName != null ? tableName : parent.getTableName();
    }

    public List<String> getSecondaryTableNames()
    {
        List<String> res = new ArrayList<String>(parent.getSecondaryTableNames());
        res.addAll(secondaryTableNames);
        return res;
    }

    public void addSecondaryTableName(String tableName)
    {
        secondaryTableNames.add(tableName);
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public boolean isId()
    {
        return isId != null ? isId : parent.isId();
    }

    public void setId(Boolean id)
    {
        isId = id;
    }

    public String getColumnPrefix()
    {
        return columnPrefix != null ? columnPrefix : parent.getColumnPrefix();
    }

    public void setColumnPrefix(String columnPrefix)
    {
        this.columnPrefix = columnPrefix;
    }

    public void setNotId(Boolean notId)
    {
        this.notId = notId;
    }

    public boolean isNotId()
    {
        return notId != null ? notId : parent.isNotId();
    }

    public void setOverrideUnique(Boolean overrideUnique)
    {
        this.overrideUnique = overrideUnique;
    }

    public Boolean getOverrideUnique()
    {
        return overrideUnique != null ? overrideUnique : parent.getOverrideUnique();
    }

    public Boolean getOverrideNullable()
    {
        return overrideNullable != null ? overrideNullable : parent.getOverrideNullable();
    }

    public void setAttributeOverride(String name, ColumnBase column)
    {
        attributeOverrides.put(name, column);
    }

    public ColumnBase getAttributeOverride(String name)
    {
        ColumnBase value = attributeOverrides.get(name);
        return value != null ? value : parent.getAttributeOverride(name);
    }

    public void setAssociationOverride(String name, List<JoinColumnBase> joinColumns)
    {
        associationOverrides.put(name, joinColumns);
    }

    public List<JoinColumnBase> getAssociationOverride(String name)
    {
        List<JoinColumnBase> value = associationOverrides.get(name);
        return value != null ? value : parent.getAssociationOverride(name);
    }

    public boolean isOnlyId()
    {
        return onlyId != null ? onlyId : parent.isOnlyId();
    }

    public void setOverrideNullable(Boolean overrideNullable)
    {
        this.overrideNullable = overrideNullable;
    }

    @Override
    public void execute(DatabaseModel databaseModel)
    {
        super.execute(databaseModel);
        parent.execute(databaseModel);
    }

    public void setOnlyId(Boolean onlyId)
    {
        this.onlyId = onlyId;
    }
}
