package com.esn.idea.liquibaseejb.model.ejb.context;

import com.intellij.javaee.model.common.persistence.mapping.ColumnBase;
import com.intellij.javaee.model.common.persistence.mapping.JoinColumnBase;

import java.util.List;
import java.util.ArrayList;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-04
 * Time: 07:53:31
 */
public class DefaultEjbModelContext extends EjbModelContext
{
    public String getTableName()
    {
        return null;
    }

    public List<String> getSecondaryTableNames()
    {
        return new ArrayList<String>();
    }

    public boolean isId()
    {
        return false;
    }

    public String getColumnPrefix()
    {
        return "";
    }

    public boolean isNotId()
    {
        return false;
    }

    public Boolean getOverrideUnique()
    {
        return null;
    }

    public Boolean getOverrideNullable()
    {
        return null;
    }

    public ColumnBase getAttributeOverride(String name)
    {
        return null;
    }

    public List<JoinColumnBase> getAssociationOverride(String name)
    {
        return null;
    }

    public boolean isOnlyId()
    {
        return false;
    }

}
