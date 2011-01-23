package com.esn.idea.liquibaseejb.javafix;

import com.intellij.codeInspection.LocalQuickFix;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Marcus Nilsson
 * Date: 2008-sep-25
 * Time: 08:35:58
 */
public abstract class AbstractFix implements LocalQuickFix
{
	protected String name;
    private String familyName;

    public AbstractFix(String name, String familyName)
	{
		this.name = name;
        this.familyName = familyName;
    }

	@NotNull
	public String getName()
	{
		return name;
	}

	@NotNull
	public String getFamilyName()
	{
		return familyName;
	}
}