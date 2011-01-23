package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Convert;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:47:39
 */
public interface IMergeColumnsAction
{
    @Convert (ColumnConverter.class)
	@Attribute ("column1Name") GenericAttributeValue<String> getColumn1Name();
	@Attribute ("joinString") GenericAttributeValue<String> getJoinString();
    @Convert (ColumnConverter.class)
	@Attribute ("column2Name") GenericAttributeValue<String> getColumn2Name();
	@Attribute ("finalColumnName") GenericAttributeValue<String> getFinalColumnName();
	@Attribute ("finalColumnType") GenericAttributeValue<String> getFinalColumnType();


}
