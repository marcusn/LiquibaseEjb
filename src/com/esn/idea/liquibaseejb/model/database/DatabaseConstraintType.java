package com.esn.idea.liquibaseejb.model.database;

/**
 * Constraint type. This is used to treat all types of constraints in the same way.
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-16
 * Time: 10:00:18
 */
public enum DatabaseConstraintType
{
	UNIQUE,
	FOREIGNKEY, PRIMARYKEY
}
