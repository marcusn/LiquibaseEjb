package com.esn.idea.liquibaseejb.model.diff;

import java.util.Map;

/**
 * Author: Marcus Nilsson
 * Date: 2008-nov-01
 * Time: 15:52:38
 */
public abstract class MapDiff<K, V>
{
    private Map<K,V> map1;
    private Map<K,V> map2;

    public MapDiff(Map<K, V> map1, Map<K, V> map2)
    {
        this.map1 = map1;
        this.map2 = map2;
    }

    public void diff()
    {
        final MapDiff<K,V> mapDiff = this;

        new CollectionDiff<K>(map1.keySet(), map2.keySet())
        {
            public void inFirst(K e)
            {
                mapDiff.inFirst(e, map1.get(e));

            }

            public void inSecond(K e)
            {
                mapDiff.inSecond(e, map2.get(e));

            }

            public void inBoth(K e)
            {
                mapDiff.inBoth(e, map1.get(e), map2.get(e));
            }
        }.diff();

    }

    public abstract void inFirst(K k, V v);
    public abstract void inSecond(K k, V v);
    public abstract void inBoth(K k, V v1, V v2);
}
