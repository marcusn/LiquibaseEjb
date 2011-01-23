package com.esn.idea.liquibaseejb.model.database;

import java.io.PrintWriter;
import java.util.List;

/**
 * Abstract model for a column. The model only contains typing and constraints, the name is stored in DatabaseTableModel.
 *
 * Author: Marcus Nilsson
 * Date: 2008-okt-15
 * Time: 12:58:36
 */
public class DatabaseColumnModel
{
    /**
     * Database column type
     */
    private String type;

    /**
     * The default value of this column
     */
    private String defaultValue = null;

    /**
     * Whether this field can not be null
     */
    private boolean notNull = false;

    /**
     * Whether this field is auto increment
     */
    private boolean autoIncrement = false;

    /**
	 * Whether this field is part of primary key
	 */
	private boolean primaryKey = false;

    /**
     * Whether this field is the only primary key. This field is maintained automatically
     * by database model.
     */
    private boolean singlePrimaryKey = false;

    /**
	 * Whether this field is unique (not part of a composite unique constraint).
	 */
	private boolean unique = false;

	/**
	 * Whether this field has a singleton index (not part of a composite index).
	 */
	private boolean index = false;

	/**
     * Constructor DatabaseColumnModel creates a new DatabaseColumnModel instance.
     *
     * @param type Type of column
     */
    public DatabaseColumnModel(String type)
	{
		this.type = type;
	}

	/**
     * Method getType returns the type of this DatabaseColumnModel object.
     *
     * @return the type (type String) of this DatabaseColumnModel object.
     */
    public String getType()
	{
		return type;
	}

	/**
     * Method setType sets the type of this DatabaseColumnModel object.
     *
     * @param type the type of this DatabaseColumnModel object.
     *
     */
    public void setType(String type)
	{
		this.type = type;
	}

	/**
     * Method getDefaultValue returns the defaultValue of this DatabaseColumnModel object.
     *
     * @return the defaultValue (type String) of this DatabaseColumnModel object.
     */
    public String getDefaultValue()
	{
		return defaultValue;
	}

	/**
     * Method setDefaultValue sets the defaultValue of this DatabaseColumnModel object.
     *
     * @param defaultValue the defaultValue of this DatabaseColumnModel object.
     *
     */
    public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	/**
     * Method getNotNull returns the notNull of this DatabaseColumnModel object.
     *
     * @return the notNull (type boolean) of this DatabaseColumnModel object.
     */
    public boolean getNotNull()
	{
		return notNull;
	}

	/**
     * Method setNotNull sets the notNull of this DatabaseColumnModel object.
     *
     * @param notNull the notNull of this DatabaseColumnModel object.
     *
     */
    public void setNotNull(boolean notNull)
	{
		this.notNull = notNull;
	}

	/**
     * Method setAutoIncrement sets the autoIncrement of this DatabaseColumnModel object.
     *
     * @param autoIncrement the autoIncrement of this DatabaseColumnModel object.
     *
     */
    public void setAutoIncrement(boolean autoIncrement)
	{
		this.autoIncrement = autoIncrement;
	}

	/**
     * Method isAutoIncrement returns the autoIncrement of this DatabaseColumnModel object.
     *
     * @return the autoIncrement (type boolean) of this DatabaseColumnModel object.
     */
    public boolean isAutoIncrement()
	{
		return autoIncrement;
	}

    /**
     * Method describe ...
     *
     * @param w of type PrintWriter
     */
    public void describe(PrintWriter w)
	{
		w.append(type);

		if (notNull) w.append(" NOT NULL");
		if (primaryKey) w.append(" PRIMARY KEY");
		if (autoIncrement) w.append (" AUTOINC");
		if (unique) w.append(" UNIQUE");
	}

	/**
     * Method setIndex sets the index of this DatabaseColumnModel object.
     *
     * @param index the index of this DatabaseColumnModel object.
     *
     */
    public void setIndex(boolean index)
	{
		this.index = index;
	}

	/**
     * Method isIndex returns the index of this DatabaseColumnModel object.
     *
     * @return the index (type boolean) of this DatabaseColumnModel object.
     */
    public boolean isIndex()
	{
		return index;
	}

    /**
     * Method isSinglePrimaryKey returns the singlePrimaryKey of this DatabaseColumnModel object.
     *
     * @return the singlePrimaryKey (type boolean) of this DatabaseColumnModel object.
     */
    public boolean isSinglePrimaryKey()
    {
        return singlePrimaryKey;
    }

    /**
     * Method setSinglePrimaryKey sets the singlePrimaryKey of this DatabaseColumnModel object.
     *
     * @param singlePrimaryKey the singlePrimaryKey of this DatabaseColumnModel object.
     *
     */
    void setSinglePrimaryKey(boolean singlePrimaryKey)
    {
        this.singlePrimaryKey = singlePrimaryKey;
    }

    public static class ForeignConstraint
	{
		private String targetTableName;
		private List<String> targetColumnNames;

		public ForeignConstraint(String targetTableName, List<String> targetColumnNames)
		{
			this.targetTableName = targetTableName;
			this.targetColumnNames = targetColumnNames;
		}

		public String getTargetTableName()
		{
			return targetTableName;
		}

		public List<String> getTargetColumnNames()
		{
			return targetColumnNames;
		}
	}

    /**
     * Method isPrimaryKey returns the primaryKey of this DatabaseColumnModel object.
     *
     * @return the primaryKey (type boolean) of this DatabaseColumnModel object.
     */
    public boolean isPrimaryKey()
	{
		return primaryKey;
	}

	/**
     * Method setPrimaryKey sets the primaryKey of this DatabaseColumnModel object.
     *
     * @param primaryKey the primaryKey of this DatabaseColumnModel object.
     *
     */
    public void setPrimaryKey(boolean primaryKey)
	{
		this.primaryKey = primaryKey;
	}

	/**
     * Method setUnique sets the unique of this DatabaseColumnModel object.
     *
     * @param unique Whether this column has a unique singleton index
     *
     */
    public void setUnique(boolean unique)
	{
		this.unique = unique;
	}

	/**
     * Method isUnique returns the unique of this DatabaseColumnModel object.
     *
     * @return Whether there is a unqiue singleton index on this column
     */
    public boolean isUnique()
	{
		return unique;
	}

}
