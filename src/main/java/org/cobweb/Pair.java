package org.cobweb;

public class Pair<K, V> {
    private K key;
    private V value;

    /**
     * Creates a new pair.
     *
     * @param key   The key for this pair.
     * @param value The value to use for this pair.
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key for this pair.
     *
     * @return The key for this pair.
     */
    public K getKey() {
        return key;
    }

    /**
     * Gets the value for this pair.
     *
     * @return The value for this pair.
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets the key for this pair.
     *
     * @param key The new key for this pair.
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * Sets the value for this pair.
     *
     * @param value The new value for this pair.
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Returns a string representation of this pair.
     *
     * @return A string representation of this pair.
     */
    @Override
    public String toString() {
        return "org.cobweb.Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    /**
     * Checks the equality of this pair with another object.
     *
     * @param o The object to be compared.
     * @return true if the specified object is equal to this pair, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
        return value != null ? value.equals(pair.value) : pair.value == null;
    }

    /**
     * Returns the hash code value for this pair.
     *
     * @return The hash code value for this pair.
     */
    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
