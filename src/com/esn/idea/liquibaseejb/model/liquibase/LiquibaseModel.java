package com.esn.idea.liquibaseejb.model.liquibase;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.xml.ChangeSet;
import com.esn.idea.liquibaseejb.model.liquibase.xml.DatabaseChangeLog;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.ChangeSetAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 13:21:25
 */
public class LiquibaseModel
{
	private XmlFile xmlFile;
	private DatabaseModel databaseModel;
	private long lastModification;
    private Map<Integer, ActionContext> actionContexts = new HashMap<Integer, ActionContext>();
    private static Key<ActionContext> actionContextKey = Key.create("actionContext");

    public LiquibaseModel(XmlFile xmlFile)
	{
		this.xmlFile = xmlFile;
	}

	public boolean isValid()
	{
		return xmlFile.isValid();
	}

    public ActionContext getActionContext(ChangeSetAction action)
    {
        getDatabaseModel(); // Force update
        return actionContexts.get(getActionContextKey(action));
    }

    private int getActionContextKey(ChangeSetAction action)
    {
        XmlElement xmlElement = action.getXmlElement();
        if (xmlElement != null)
        {
            return xmlElement.getTextOffset();
        }

        return 0;
    }

    public synchronized DatabaseModel getDatabaseModel()
	{
		long lastModification = xmlFile.getModificationStamp();
		if (databaseModel == null || lastModification > this.lastModification)
		{
			databaseModel = new DatabaseModel();
            actionContexts.clear();

            DatabaseChangeLog changeLog = findDatabaseChangeLog();
			this.lastModification = xmlFile.getModificationStamp();
			if (changeLog == null) return null;

			for (ChangeSet changeSet : changeLog.getChangeSets())
			{
                List<ChangeSetAction> changeSetActionList = changeSet.getActions();
				for (ChangeSetAction changeSetAction : changeSetActionList)
				{
                    ActionContext actionContext = new ActionContext(databaseModel);
                    actionContexts.put(getActionContextKey(changeSetAction), actionContext);
                    changeSetAction.execute(databaseModel);
                }
			}
		}

		return databaseModel;
	}

    @Nullable
	public DatabaseChangeLog findDatabaseChangeLog()
	{
		Project project = xmlFile.getProject();

		DomManager domManager = DomManager.getDomManager(project);
		DomFileElement<DatabaseChangeLog> databaseChangeLogDomFileElement = domManager.getFileElement(xmlFile, DatabaseChangeLog.class);
		if (databaseChangeLogDomFileElement == null) return null;

		return databaseChangeLogDomFileElement.getRootElement();
	}

}
