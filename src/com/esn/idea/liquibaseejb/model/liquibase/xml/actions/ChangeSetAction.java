package com.esn.idea.liquibaseejb.model.liquibase.xml.actions;

import com.intellij.util.xml.DomElement;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-14
 * Time: 11:21:54
 */
public interface ChangeSetAction extends DomElement
{
	public void execute(DatabaseModel model);
}
