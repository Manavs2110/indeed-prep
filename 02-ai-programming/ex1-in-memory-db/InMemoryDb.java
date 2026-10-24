import java.util.*;

public class InMemoryDb {

    // ---- Level 1 ----
    public void set(String key, String field, String value) { throw new UnsupportedOperationException(); }
    public String get(String key, String field) { throw new UnsupportedOperationException(); }
    public boolean delete(String key, String field) { throw new UnsupportedOperationException(); }

    // ---- Level 2 ----
    public List<String> scan(String key) { throw new UnsupportedOperationException(); }
    public List<String> scanByPrefix(String key, String prefix) { throw new UnsupportedOperationException(); }

    // ---- Level 3 ----
    public void setAt(String key, String field, String value, long ts) { throw new UnsupportedOperationException(); }
    public void setAtWithTtl(String key, String field, String value, long ts, long ttl) { throw new UnsupportedOperationException(); }
    public String getAt(String key, String field, long ts) { throw new UnsupportedOperationException(); }
    public boolean deleteAt(String key, String field, long ts) { throw new UnsupportedOperationException(); }
    public List<String> scanAt(String key, long ts) { throw new UnsupportedOperationException(); }
    public List<String> scanByPrefixAt(String key, String prefix, long ts) { throw new UnsupportedOperationException(); }

    // ---- Level 4 ----
    public int backup(long ts) { throw new UnsupportedOperationException(); }
    public void restore(long ts, long tsToRestore) { throw new UnsupportedOperationException(); }
}
