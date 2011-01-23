package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:37:05
 */
public interface TableHolderAction extends DomElement
{
	@Attribute ("tableName")
    @Convert (TableConverter.class)
    GenericAttributeValue<String> getTableName();
}
