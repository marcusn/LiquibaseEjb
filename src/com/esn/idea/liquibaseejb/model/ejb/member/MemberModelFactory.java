package com.esn.idea.liquibaseejb.model.ejb.member;

import com.esn.idea.liquibaseejb.model.ejb.module.ModuleModel;
import com.intellij.javaee.model.common.persistence.mapping.*;

import java.util.Collection;

/**
 * Author: Marcus Nilsson
* Date: 2008-nov-09
* Time: 19:17:29
*/
public class MemberModelFactory extends AttributeVisitorAdapter
{
    private final Collection<MemberModel> res;
    private ModuleModel moduleModel;

    public MemberModelFactory(ModuleModel moduleModel, Collection<MemberModel> res)
    {
        this.moduleModel = moduleModel;
        this.res = res;
    }

    public boolean visitId(Id id)
    {
        return visitAttributeWithColumn(id);
    }

    public boolean visitEmbeddedId(EmbeddedId embeddedId)
    {
        return visitEmbedded(embeddedId);
    }

    public boolean visitEmbedded(Embedded embedded)
    {
        res.add(new EmbeddedModel(moduleModel, embedded));
        return true;
    }

    public boolean visitManyToOne(ManyToOne manyToOne)
    {
        return visitAnyToOne(manyToOne);
    }

    public boolean visitOneToMany(OneToMany oneToMany)
    {
        return visitAnyToMany(oneToMany);
    }

    public boolean visitOneToOne(OneToOne oneToOne)
    {
        return visitAnyToOne(oneToOne);
    }

    public boolean visitManyToMany(ManyToMany manyToMany)
    {
        return visitAnyToMany(manyToMany);
    }

    public boolean visitAttributeWithColumn(AttributeWithColumn basic)
    {
        res.add(new AttributeWithColumnModel(moduleModel, basic));
        return true;
    }

    private boolean visitAnyToMany(RelationAttributeBase.AnyToManyBase oneToMany)
    {
        res.add(new AnyToManyModel(moduleModel, oneToMany));
        return true;
    }

    private boolean visitAnyToOne(RelationAttributeBase.AnyToOneBase manyToOne)
    {
        res.add(new AnyToOneModel(moduleModel, manyToOne));
        return true;
    }
}
