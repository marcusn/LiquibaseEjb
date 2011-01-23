package com.esn.idea.liquibaseejb;

import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.toolwindow.DatabaseModelView;
import com.esn.idea.liquibaseejb.refactor.RenameListenerProvider;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.refactoring.listeners.RefactoringListenerManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-13
 * Time: 20:01:29
 */
public class LiquibaseProjectComponent implements ProjectComponent
{
	private Project project;
	private ToolWindow toolWindow;
	private DatabaseModelView liquibaseModelView;
    private DatabaseModelView ejbModelView;
	private RenameListenerProvider renameListener;
	private Icon icon;

    public Icon getIcon()
	{
		return icon;
	}

	public ToolWindow getToolWindow()
	{
		return toolWindow;
	}

	public LiquibaseProjectComponent(Project project)
	{
		this.project = project;
	}

	public void initComponent()
	{
        /**
        DomManager domManager = DomManager.getDomManager(project);
		domManager.registerFileDescription(new DomFileDescription(DatabaseChangeLog.class, "databaseChangeLog"));**/

	}

	public void disposeComponent()
	{
		// TODO: insert component disposal logic here
	}

	@NotNull
	public String getComponentName()
	{
		return "LiquibaseEJBProjectComponent";
	}

	public void projectOpened()
	{
		ToolWindowManager tmManager = ToolWindowManager.getInstance(project);
		toolWindow = tmManager.registerToolWindow("LiquibaseEJBModel", true, ToolWindowAnchor.RIGHT);

        liquibaseModelView = new DatabaseModelView("Liquibase");
        ejbModelView = new DatabaseModelView("EJB");
        JSplitPane tabs = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        //tabs.setDividerLocation(300);
        //tabs = new JTabbedPane();
        tabs.add(liquibaseModelView);
        tabs.add(ejbModelView);

        toolWindow.getComponent().add(tabs);

		RefactoringListenerManager manager = RefactoringListenerManager.getInstance(project);
		renameListener = new RenameListenerProvider();
		manager.addListenerProvider(renameListener);
	}


    public void projectClosed()
	{
		RefactoringListenerManager manager = RefactoringListenerManager.getInstance(project);
		manager.removeListenerProvider(renameListener);
	}

	public void showLiquibaseDatabaseModel(DatabaseModel databaseModel)
	{
        try
        {
            liquibaseModelView.setDatabaseModel(databaseModel);
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

    public void showEjbDatabaseModel(DatabaseModel databaseModel)
    {
        try
        {
            ejbModelView.setDatabaseModel(databaseModel);
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
    }

}
