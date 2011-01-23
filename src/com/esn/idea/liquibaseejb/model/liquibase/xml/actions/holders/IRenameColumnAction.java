package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Convert;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:48:25
 */
public interface IRenameColumnAction
{
    @Convert (ColumnConverter.class)
	@Attribute ("oldColumnName") GenericAttributeValue<String> getOldColumnName();

    @Convert (ColumnConverter.class)
	@Attribute ("newColumnName") GenericAttributeValue<String> getNewColumnName();

    @Attribute ("columnDataType") GenericAttributeValue<String> getColumnDataType();


}
