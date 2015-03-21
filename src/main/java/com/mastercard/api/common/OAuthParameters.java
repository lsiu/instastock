package com.mastercard.api.common;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class OAuthParameters {

    protected Map<String, SortedSet<String>> baseParameters;
    
    /**
     * Creates a new {@link OAuthParameters} object.  Initializes parameters
     * containers.
     */
    public OAuthParameters() {
        baseParameters = new TreeMap<String, SortedSet<String>>();
    }
    
    /**
     * Return the parameters used to calculate the OAuth signature.
     *
     * @return a map of key/value pairs to use in the signature base string
     */
    public Map<String, SortedSet<String>> getBaseParameters() {
    	return baseParameters;
    }
    
    /**
     * Adds a parameter to be used when generating the OAuth signature.
     *
     * @param key   The key used to reference this parameter.  This key will also be
     *              used to reference the value in the request url and in the http
     *              authorization header.
     * @param value the value of the parameter
     */
    public void addParameter(String key, String value) {
        put(key, value);
    }

    /**
     * Removes a parameter from the OAuth signature.
     *
     * @param key The key used to reference this parameter.
     * @param value The value that will be removed
     */
    public void removeBaseParameter(String key, String value) {
        remove(key, value);
    }

    /**
     * Removes all parameter from the OAuth signature.
     *
     * @param key The key used to reference this parameter.
     */
    public void removeBaseParameter(String key) {
        remove(key, null);
    }
    
    /**
     * Retrieves the value with the given key from the input map.  A null value
     * is returned as an empty string.
     *
     * @param key    the key whose value to retrieve
     * @return the value associated with the given key
     */
    public String get(String key){
        SortedSet<String> s = this.baseParameters.get(key);
        return s.first() == null ? "" : s.first();
    }

    /**
     * Adds the key/value pair to the input map.
     *
     * @param key    the key to add to the map
     * @param value  the value to add to the map
     */
    protected void put(String key, String value){
        SortedSet<String> temp;
        if (this.baseParameters.containsKey(key)) {
            temp = new TreeSet<String>(this.baseParameters.get(key));
            temp.add(value);

            this.baseParameters.put(key, temp);

        } else {
            temp = new TreeSet<String>();
            temp.add(value);
            this.baseParameters.put(key, temp);
        }
    }
    
    protected void putAll(Map<String, SortedSet<String>> params){
    	this.baseParameters.putAll(params);
    }

    /**
     * Removes a key/value pair from the input map's sortedSet.
     *
     * @param key    the key to remove to value from
     * @param value  the value to remove from the set
     */

    protected void remove(String key, String value){
        SortedSet<String> temp;
        if (this.baseParameters.containsKey(key) && value != null) {
            temp = new TreeSet<String>(this.baseParameters.get(key));
            this.baseParameters.remove(key);
            if (temp.contains(value)) {
                temp.remove(value);
                this.baseParameters.put(key, temp);
            }
        } else if (this.baseParameters.containsKey(key) && value == null) {
        	this.baseParameters.remove(key);
        }
    }

}
