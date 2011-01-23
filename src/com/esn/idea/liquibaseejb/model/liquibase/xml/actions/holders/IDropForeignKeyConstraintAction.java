package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Convert;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:46:12
 */
public interface IDropForeignKeyConstraintAction
{
	@Attribute ("constraintName")	 GenericAttributeValue<String> getConstraintName();
    @Convert (TableConverter.class)
	@Attribute ("baseTableName")	 GenericAttributeValue<String> getBaseTableName();
}
