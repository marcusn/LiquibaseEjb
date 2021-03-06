package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 16:42:32
 */
public interface DefaultValueHolderAction
{
	@Attribute ("defaultValue") GenericAttributeValue<String> getDefaultValue();
}
