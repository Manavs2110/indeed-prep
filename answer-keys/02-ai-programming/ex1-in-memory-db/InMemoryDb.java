import java.util.*;

/**
 * Reference solution.
 * Storage: key -> (field -> Entry). TreeMap per record gives sorted scans for free.
 * get/set/delete: O(log f). scan: O(f). backup: O(total fields). restore: O(fields in backup).
 */
public class InMemoryDb {

    private static final class Entry {
        final String value;
        final Long expiresAt; // null = never expires (in backups: remaining TTL)

        Entry(String value, Long expiresAt) { this.value = value; this.expiresAt = expiresAt; }

        boolean aliveAt(long ts) { return expiresAt == null || ts < expiresAt; }
    }

    private Map<String, TreeMap<String, Entry>> data = new HashMap<>();
    private final TreeMap<Long, Map<String, TreeMap<String, Entry>>> backups = new TreeMap<>();

    // ---- Level 1 ----
    public void set(String key, String field, String value) { setAt(key, field, value, 0); }
    public String get(String key, String field) { return getAt(key, field, 0); }
    public boolean delete(String key, String field) { return deleteAt(key, field, 0); }

    // ---- Level 2 ----
    public List<String> scan(String key) { return scanAt(key, 0); }
    public List<String> scanByPrefix(String key, String prefix) { return scanByPrefixAt(key, prefix, 0); }

    // ---- Level 3 ----
    public void setAt(String key, String field, String value, long ts) {
        data.computeIfAbsent(key, k -> new TreeMap<>()).put(field, new Entry(value, null));
    }

    public void setAtWithTtl(String key, String field, String value, long ts, long ttl) {
        data.computeIfAbsent(key, k -> new TreeMap<>()).put(field, new Entry(value, ts + ttl));
    }

    public String getAt(String key, String field, long ts) {
        Entry e = live(key, field, ts);
        return e == null ? null : e.value;
    }

    public boolean deleteAt(String key, String field, long ts) {
        if (live(key, field, ts) == null) return false;
        TreeMap<String, Entry> record = data.get(key);
        record.remove(field);
        if (record.isEmpty()) data.remove(key);
        return true;
    }

    public List<String> scanAt(String key, long ts) { return scanByPrefixAt(key, "", ts); }

    public List<String> scanByPrefixAt(String key, String prefix, long ts) {
        List<String> out = new ArrayList<>();
        TreeMap<String, Entry> record = data.get(key);
        if (record == null) return out;
        // tailMap starting at prefix, stop at first non-matching field
        for (Map.Entry<String, Entry> me : record.tailMap(prefix, true).entrySet()) {
            if (!me.getKey().startsWith(prefix)) break;
            if (me.getValue().aliveAt(ts)) out.add(me.getKey() + "(" + me.getValue().value + ")");
        }
        return out;
    }

    // ---- Level 4 ----
    public int backup(long ts) {
        Map<String, TreeMap<String, Entry>> snapshot = new HashMap<>();
        for (Map.Entry<String, TreeMap<String, Entry>> rec : data.entrySet()) {
            TreeMap<String, Entry> copy = new TreeMap<>();
            for (Map.Entry<String, Entry> f : rec.getValue().entrySet()) {
                Entry e = f.getValue();
                if (!e.aliveAt(ts)) continue;
                copy.put(f.getKey(), new Entry(e.value, e.expiresAt == null ? null : e.expiresAt - ts));
            }
            if (!copy.isEmpty()) snapshot.put(rec.getKey(), copy);
        }
        backups.put(ts, snapshot);
        return snapshot.size();
    }

    public void restore(long ts, long tsToRestore) {
        Map.Entry<Long, Map<String, TreeMap<String, Entry>>> b = backups.floorEntry(tsToRestore);
        if (b == null) return;
        Map<String, TreeMap<String, Entry>> restored = new HashMap<>();
        for (Map.Entry<String, TreeMap<String, Entry>> rec : b.getValue().entrySet()) {
            TreeMap<String, Entry> copy = new TreeMap<>();
            for (Map.Entry<String, Entry> f : rec.getValue().entrySet()) {
                Entry e = f.getValue();
                copy.put(f.getKey(), new Entry(e.value, e.expiresAt == null ? null : ts + e.expiresAt));
            }
            restored.put(rec.getKey(), copy);
        }
        data = restored; // fresh copies, so the stored backup is never mutated
    }

    private Entry live(String key, String field, long ts) {
        TreeMap<String, Entry> record = data.get(key);
        if (record == null) return null;
        Entry e = record.get(field);
        return e != null && e.aliveAt(ts) ? e : null;
    }
}
