package com.esn.idea.liquibaseejb.model.ejb.clazz;

import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.ejb.context.EjbModelContext;
import com.intellij.javaee.model.common.persistence.mapping.Entity;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-29
 * Time: 17:41:39
 */
public abstract class InheritanceModel extends EntityModel
{
    protected Entity basePersistentObject;

    public InheritanceModel(ModuleModel moduleModel, Entity persistentObject, Entity basePersistentObject)
    {
        super(moduleModel, persistentObject);
        this.basePersistentObject = basePersistentObject;
    }
}
