package com.esn.idea.liquibaseejb.model.liquibase.xml;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 09:16:36
 */
public interface LiquibaseConstraints extends DomElement
{
	@Attribute ("nullable")	GenericAttributeValue<String> getNullable();
	@Attribute ("primaryKey")	GenericAttributeValue<String> getPrimaryKey();
	@Attribute ("primaryKeyName")	GenericAttributeValue<String> getPrimaryKeyName();
	@Attribute ("unique")	GenericAttributeValue<String> getUnique();
	@Attribute ("uniqueConstraintName")	GenericAttributeValue<String> getUniqueConstraintName();
	@Attribute ("references")	GenericAttributeValue<String> getReferences();
	@Attribute ("foreignKeyName")	GenericAttributeValue<String> getForeignKeyName();
	@Attribute ("deleteCascade")	GenericAttributeValue<String> getDeleteCascade();
	@Attribute ("deferrable")	GenericAttributeValue<String> getDeferrable();
	@Attribute ("initiallyDeferred")	GenericAttributeValue<String> getInitiallyDeferred();
}
