package com.esn.idea.liquibaseejb.model.liquibase.xml;

import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders.ColumnConverter;
import com.intellij.util.xml.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 11:09:49
 */
public interface LiquibaseColumn extends DomElement
{
    @Convert (ColumnConverter.class)
    @Attribute ("name")	GenericAttributeValue<String> getName();
	@Attribute ("type")	GenericAttributeValue<String> getType();
	@Attribute ("value")	GenericAttributeValue<String> getAttributeValue();
	@Attribute ("valueNumeric")	GenericAttributeValue<String> getValueNumeric();
	@Attribute ("valueBoolean")	GenericAttributeValue<String> getValueBoolean();
	@Attribute ("valueDate")	GenericAttributeValue<String> getValueDate();
	@Attribute ("defaultValue")	GenericAttributeValue<String> getDefaultValue();
	@Attribute ("defaultValueNumeric")	GenericAttributeValue<String> getDefaultValueNumeric();
	@Attribute ("defaultValueBoolean")	GenericAttributeValue<String> getDefaultValueBoolean();
	@Attribute ("defaultValueDate")	GenericAttributeValue<String> getDefaultValueDate();
	@Attribute ("autoIncrement")	GenericAttributeValue<String> getAutoIncrement();

	@SubTag ("constraints")
	LiquibaseConstraints getConstraints();
}
