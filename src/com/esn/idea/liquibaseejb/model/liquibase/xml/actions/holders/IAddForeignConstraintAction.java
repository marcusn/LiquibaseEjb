package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:43:13
 */
public interface IAddForeignConstraintAction
{
	@Attribute ("constraintName")	 GenericAttributeValue<String> getConstraintName();
    @Convert (TableConverter.class)
    @Attribute ("baseTableName")	 GenericAttributeValue<String> getBaseTableName();
	@Attribute ("baseColumnNames")	 GenericAttributeValue<String> getBaseColumnNames();
    @Convert (TableConverter.class)
	@Attribute ("referencedTableName")	 GenericAttributeValue<String> getReferencedTableName();
	@Attribute ("referencedColumnNames")	 GenericAttributeValue<String> getReferencedColumnNames();
}
