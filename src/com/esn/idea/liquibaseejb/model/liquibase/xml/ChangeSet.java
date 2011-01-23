package com.esn.idea.liquibaseejb.model.liquibase.xml;

import com.intellij.util.xml.*;
import com.esn.idea.liquibaseejb.model.liquibase.xml.actions.*;

import java.util.List;

/**
 * Author: Marcus Nilsson
 * Date: 2008-okt-13
 * Time: 20:38:38
 */
public interface ChangeSet extends DomElement
{
	@Attribute ("author")
	GenericAttributeValue<String> getAuthor();

	@Attribute ("id")
	GenericAttributeValue<String> getId();

	@SubTagsList(value = {
					"createTable",
					"dropTable",
					"addColumn",
					"renameTable",
					"renameColumn",
					"dropColumn",
					"modifyColumn",
					"mergeColumns",
					"createIndex",
					"dropIndex",
					"addNotNullConstraint",
					"dropNotNullConstraint",
					"addForeignKeyConstraint",
					"dropForeignKeyConstraint",
					"addPrimaryKey",
					"dropPrimaryKey",
					"addAutoIncrement",
					"addDefaultValue",
					"dropDefaultValue",
					"addUniqueConstraint",
					"dropUniqueConstraint"
					})
	List<ChangeSetAction> getActions();

	@SubTagList("createTable")
	CreateTableAction addCreateTable();

	@SubTagList("createTable")
	List<CreateTableAction> getCreateTables();

	@SubTagList("dropTable")
	DropTableAction addDropTable();

	@SubTagList("dropTable")
	List<DropTableAction> getDropTables();

	@SubTagList("addColumn")
	AddColumnAction addAddColumn();

	@SubTagList("addColumn")
	List<AddColumnAction> getAddColumns();

	@SubTagList("renameTable")
	RenameTableAction addRenameTable();

	@SubTagList("renameTable")
	List<RenameTableAction> getRenameTables();

	@SubTagList("renameColumn")
	RenameColumnAction addRenameColumn();

	@SubTagList("renameColumn")
	List<RenameColumnAction> getRenameColumns();

	@SubTagList("dropColumn")
	DropColumnAction addDropColumn();

	@SubTagList("dropColumn")
	List<DropColumnAction> getDropColumns();

	@SubTagList("modifyColumn")
	ModifyColumnAction addModifyColumn();

	@SubTagList("modifyColumn")
	List<ModifyColumnAction> getModifyColumns();

	@SubTagList("mergeColumns")
	MergeColumnsAction addMergeColumn();

	@SubTagList("mergeColumns")
	List<MergeColumnsAction> getMergeColumns();

	@SubTagList("createIndex")
	CreateIndexAction addCreateIndex();

	@SubTagList("createIndex")
	List<CreateIndexAction> getCreateIndexes();

	@SubTagList("dropIndex")
	DropIndexAction addDropIndex();

	@SubTagList("dropIndex")
	List<DropIndexAction> getDropIndexes();

	@SubTagList("addNotNullConstraint")
	AddNotNullConstraintAction addAddNotNullConstraint();

	@SubTagList("addNotNullConstraint")
	List<AddNotNullConstraintAction> getAddNotNullConstraints();

	@SubTagList("dropNotNullConstraint")
	DropNotNullConstraintAction addDropNotNullConstraint();

	@SubTagList("dropNotNullConstraint")
	List<DropNotNullConstraintAction> getDropNotNullConstraints();

	@SubTagList("addForeignKeyConstraint")
	AddForeignKeyConstraintAction addAddForeignKeyConstraint();

	@SubTagList("addForeignKeyConstraint")
	List<AddForeignKeyConstraintAction> getAddForeignKeyConstraints();

	@SubTagList("dropForeignKeyConstraint")
	DropForeignKeyConstraintAction addDropForeignKeyConstraint();

	@SubTagList("dropForeignKeyConstraint")
	List<DropForeignKeyConstraintAction> getDropForeignKeyConstraints();

	@SubTagList("addPrimaryKey")
	AddPrimaryKeyAction addAddPrimaryKey();

	@SubTagList("addPrimaryKey")
	List<AddPrimaryKeyAction> getAddPrimaryKeys();

	@SubTagList("dropPrimaryKey")
	DropPrimaryKeyAction addDropPrimaryKey();

	@SubTagList("dropPrimaryKey")
	List<DropPrimaryKeyAction> getDropPrimaryKeys();

	@SubTagList("addAutoIncrement")
	AddAutoIncrementAction addAddAutoIncrement();

	@SubTagList("addAutoIncrement")
	List<AddAutoIncrementAction> getAddAutoIncrements();

	@SubTagList("addDefaultValue")
	AddDefaultValueAction addAddDefaultValue();

	@SubTagList("addDefaultValue")
	List<AddDefaultValueAction> getAddDefaultValues();

	@SubTagList("dropDefaultValue")
	DropDefaultValueAction addDropDefaultValue();

	@SubTagList("dropDefaultValue")
	List<DropDefaultValueAction> getDropDefaultValues();

	@SubTagList("addUniqueConstraint")
	AddUniqueConstraintAction addAddUniqueConstraint();

	@SubTagList("addUniqueConstraint")
	List<AddUniqueConstraintAction> getAddUniqueConstraints();

	@SubTagList("dropUniqueConstraint")
	DropUniqueConstraintAction addDropUniqueConstraint();

	@SubTagList("dropUniqueConstraint")
	List<DropUniqueConstraintAction> getDropUniqueConstraints();
}
