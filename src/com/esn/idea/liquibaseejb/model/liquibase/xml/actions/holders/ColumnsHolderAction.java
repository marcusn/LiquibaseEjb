package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.SubTagList;
import com.esn.idea.liquibaseejb.model.liquibase.xml.LiquibaseColumn;

import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 10:42:45
 */
public interface ColumnsHolderAction
{
	@SubTagList ("column")
	List<LiquibaseColumn> getColumns();

	@SubTagList ("column")
	LiquibaseColumn addColumn();
}
