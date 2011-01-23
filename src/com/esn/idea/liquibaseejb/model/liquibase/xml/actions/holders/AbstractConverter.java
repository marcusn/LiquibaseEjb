package com.esn.idea.liquibaseejb.model.liquibase.xml.actions.holders;

import com.intellij.util.xml.ResolvingConverter;
import com.intellij.util.xml.ConvertContext;
import com.esn.idea.liquibaseejb.LiquibaseModuleComponent;
import com.esn.idea.liquibaseejb.model.liquibase.LiquibaseModel;
import com.esn.idea.liquibaseejb.model.liquibase.ActionContext;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.ChangeSetAction;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-24
 * Time: 17:24:39
 */
public abstract class AbstractConverter extends ResolvingConverter<String>
{
    public String fromString(@Nullable String s, ConvertContext convertContext)
    {
        return s;
    }

    public String toString(@Nullable String s, ConvertContext convertContext)
    {
        return s;
    }

    @NotNull
    public Collection<? extends String> getVariants(ConvertContext convertContext)
    {
        LiquibaseModuleComponent liquibaseModuleComponent = LiquibaseModuleComponent.getInstance(convertContext.getInvocationElement().getModule());
        ChangeSetAction action = convertContext.getInvocationElement().getParentOfType(ChangeSetAction.class, false);



        if (action != null && liquibaseModuleComponent != null)
        {
            LiquibaseModel model = liquibaseModuleComponent.findLiquibaseModel();

            if (model != null)
            {
                ActionContext actionContext = model.getActionContext(action);
                if (actionContext != null)
                {
                    return getVariantsFromActionContext(convertContext, actionContext);
                }
            }
        }

        return Collections.emptySet();
    }

    protected abstract Collection<? extends String> getVariantsFromActionContext(ConvertContext convertContext, ActionContext actionContext);
}
