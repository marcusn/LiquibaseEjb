package com.esn.idea.liquibaseejb;

import com.esn.idea.liquibaseejb.facet.LiquibaseFacetType;
import com.esn.idea.liquibaseejb.intentions.AddIndexAction;
import com.esn.idea.liquibaseejb.intentions.AddUniqueConstraintAction;
import com.esn.idea.liquibaseejb.inspections.LiquibaseSchemaInspection;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-13
 * Time: 18:14:54
 */
public class LiquibaseComponent implements ApplicationComponent, InspectionToolProvider
{
	public LiquibaseComponent()
	{
	}

	public void initComponent()
	{
        /**
		IntentionManager intentionManager = IntentionManager.getInstance();
		intentionManager.addAction(new AddIndexAction());
		intentionManager.addAction(new AddUniqueConstraintAction());
         **/

        FacetTypeRegistry.getInstance().registerFacetType(LiquibaseFacetType.INSTANCE);

    }

	public void disposeComponent()
	{
		// TODO: insert component disposal logic here
	}

	@NotNull
	public String getComponentName()
	{
		return "LiquibaseEJBComponent";
	}


	public Class[] getInspectionClasses()
	{
		return new Class[]{LiquibaseSchemaInspection.class};
	}
}
