package com.esn.idea.liquibaseejb.model.diff;

import java.util.Collection;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 15:47:32
 */
public abstract class CollectionDiff<T>
{
    private Collection<T> collection1;
    private Collection<T> collection2;

    public CollectionDiff(Collection<T> collection1, Collection<T> collection2)
    {
        this.collection1 = collection1;
        this.collection2 = collection2;
    }

    public void diff()
    {
        for (T e : collection1)
        {
            if (collection2.contains(e))
            {
                inBoth(e);
            }
            else
            {
                inFirst(e);
            }
        }

        for (T e : collection2)
        {
            if (!collection1.contains(e))
            {
                inSecond(e);
            }
        }

    }

    public abstract void inFirst(T e);
    public abstract void inSecond(T e);
    public abstract void inBoth(T e);
}
