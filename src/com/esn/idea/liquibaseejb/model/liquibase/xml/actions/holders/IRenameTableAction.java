package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Convert;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:48:53
 */
public interface IRenameTableAction
{
    @Convert (TableConverter.class)
	@Attribute ("oldTableName")	GenericAttributeValue<String> getOldTableName();

	@Attribute ("newTableName") GenericAttributeValue<String> getNewTableName();


}
