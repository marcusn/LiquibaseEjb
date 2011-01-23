package com.esn.idea.liquibaseejb.javafix;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-25
 * Time: 11:08:43
 */
public abstract class AbstractAnnotationFix extends AbstractFix
{
    public AbstractAnnotationFix(String name)
    {
        super(name, "Annotation");
    }
}
