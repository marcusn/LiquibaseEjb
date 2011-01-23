package com.esn.idea.liquibaseejb.model.liquibase.xml;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-13
 * Time: 20:07:59
 */
public interface DatabaseChangeLog extends DomElement
{
	@SubTagList ("changeSet") List<ChangeSet> getChangeSets();

	@SubTagList ("changeSet") ChangeSet addChangeSet();
	
}
