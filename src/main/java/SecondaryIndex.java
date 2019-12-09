import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecondaryIndex<K, V> {
    private Map<K, Collection<V>> map = new ConcurrentHashMap<>();

    public void put(K k, V v) {
        Collection<V> vs = map.computeIfAbsent(k, k1 -> new HashSet<>());
        vs.add(v);
    }

    public Collection<V> get(K k) {
        return map.getOrDefault(k, Collections.EMPTY_SET);
    }

    public void remove(K key, V value) {
        Collection<V> values = map.get(key);
        if (values != null) {
            values.remove(value);
            if (values.isEmpty()) {
                map.remove(key);
            }
        }
    }

    public void clear() {
        map.clear();
    }
}
