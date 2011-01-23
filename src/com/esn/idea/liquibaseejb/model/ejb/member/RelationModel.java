package com.esn.idea.liquibaseejb.model.ejb.member;

import com.intellij.javaee.model.common.persistence.mapping.RelationAttributeBase;
import com.intellij.javaee.model.common.persistence.mapping.JoinColumnBase;
import com.intellij.psi.PsiClass;
import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseModel;
import com.esn.idea.liquibaseejb.model.database.DatabaseField;

import java.util.*;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-22
 * Time: 12:33:05
 */
public abstract class RelationModel<T extends RelationAttributeBase> extends MemberModel<T>
{
    public RelationModel(ModuleModel moduleModel, T attribute)
    {
        super(moduleModel, attribute);
    }

}
