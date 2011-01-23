package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 14:12:27
 */
public interface ColumnHolderAction
{
	@Attribute ("columnName")
    @Convert (ColumnConverter.class)
    GenericAttributeValue<String> getColumnName();
}
