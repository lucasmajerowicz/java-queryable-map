import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecondaryIndex<K, V> {
    private Map<K, Set<V>> map = new ConcurrentHashMap<>();
    private Map<V, Set<K>> mapReverse = new ConcurrentHashMap<>();

    public void put(K k, V v) {
        Set<V> vs = map.computeIfAbsent(k, k1 -> new HashSet<>());
        vs.add(v);

        Set<K> keys = mapReverse.computeIfAbsent(v, k1 -> new HashSet<>());
        keys.add(k);
    }

    public Collection<V> get(K k) {
        return map.getOrDefault(k, Collections.EMPTY_SET);
    }

    public void removeElement(V v) {
        Set<K> keys = mapReverse.remove(v);
        if (keys != null) {
            keys.forEach(k -> {
                Set<V> vs = map.get(k);
                vs.remove(v);
                if (vs.isEmpty()) {
                    map.remove(k);
                }
            });
        }
    }

    public void clear() {
        map.clear();
        mapReverse.clear();
    }
}
