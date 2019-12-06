import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryableMap<K, V> {
    private Map<K, V> map = new ConcurrentHashMap<>();
    private Map<Function<V, Object>, MultiBiMap<Object, K>> indexes = new ConcurrentHashMap<>();
    private final Function<V, K> keyFunction;

    public QueryableMap(Function<V, K> keyFunction) {
        this.keyFunction = keyFunction;
    }

    public void addIndex(Function<V, Object> function) {
        indexes.put(function, new MultiBiMap<>());
    }

    public void put(V value) {
        K key = keyFunction.apply(value);

        delete(key);
        map.put(key, value);

        indexes.forEach((function, indexMap) -> {
            Object indexKey = function.apply(value);
            if (indexKey instanceof Collection) {
                ((Collection) indexKey).stream()
                        .forEach(eachIndexKey -> indexMap.put(eachIndexKey, key));
            } else {
                indexMap.put(indexKey, key);
            }
        });
    }

    public void delete(K key) {
        V value = map.remove(key);

        if (value != null) {
            indexes.forEach((function, indexMap) -> {
                indexMap.removeValue(key);
            });
        }
    }

    public V get(String key) {
        return map.get(key);
    }

    public Collection<V> query(Function<V, Object> function, Object value) {
        MultiBiMap<Object, K> indexMap = indexes.get(function);

        return indexMap.get(value)
                .stream()
                .map(map::get)
                .collect(Collectors.toList());
    }
}
