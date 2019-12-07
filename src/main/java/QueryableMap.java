import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryableMap<K, V> {
    private Map<K, V> map = new ConcurrentHashMap<>();
    private Map<String, Index> indices = new ConcurrentHashMap<>();
    private final Function<V, K> keyFunction;

    private QueryableMap(Function<V, K> keyFunction) {
        this.keyFunction = keyFunction;
    }

    private void addIndex(Index index) {
        indices.put(index.getName(), index);
    }

    public void put(V value) {
        K key = keyFunction.apply(value);

        delete(key);
        map.put(key, value);

        indices.values().forEach(index -> {
            Object indexKey = index.getFunction().apply(value);
            if (indexKey instanceof Collection) {
                ((Collection) indexKey)
                        .forEach(eachIndexKey -> index.getMap().put(eachIndexKey, key));
            } else {
                index.getMap().put(indexKey, key);
            }
        });
    }

    public void delete(K key) {
        V value = map.remove(key);

        if (value != null) {
            indices.forEach((function, indexMap) -> {
                indexMap.getMap().removeValue(key);
            });
        }
    }

    public V get(String key) {
        return map.get(key);
    }

    public Collection<V> query(String indexName, Object value) {
        Index<Object, K> indexMap = indices.get(indexName).getMap();

        return indexMap.get(value)
                .stream()
                .map(map::get)
                .collect(Collectors.toList());
    }

    private static class Index<K, V> {
        private final String name;
        private final Function<V, Object> function;
        private final Index<Object, K> map;

        public Index(String name, Function<V, Object> function) {
            this.function = function;
            this.name = name;
            this.map = new Index<>();
        }

        public Function<V, Object> getFunction() {
            return function;
        }

        public String getName() {
            return name;
        }

        public Index<Object, K> getMap() {
            return map;
        }
    }

    public static <K, V> Builder newBuilder() {
        return new Builder<>();
    }

    public static class Builder<K, V> {
        private  Function<V, K> keyFunction;
        private List<Index> indices = new ArrayList<>();

        public Builder<K, V> keyFunction(Function<V, K> keyFunction) {
            this.keyFunction = keyFunction;
            return this;
        }

        public Builder<K, V> addIndex(String name, Function<V, Object> function) {
            indices.add(new Index(name, function));
            return this;
        }

        public QueryableMap<K, V> build() {
            QueryableMap<K, V> map = new QueryableMap<>(keyFunction);

            indices.forEach(map::addIndex);

            return map;
        }
    }
}
