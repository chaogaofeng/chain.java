package com.glodnet.chain.keys.impl;

import com.glodnet.chain.util.Key;
import com.glodnet.chain.exception.KeyException;
import com.glodnet.chain.keys.IKeyDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * Default Key DAO Implementation, only for testing purpose, not recommended
 *
 * @author Yelong
 */
public class DefaultKeyDAOImpl implements IKeyDAO {

    private static Map<String, Key> KEY_MAP;

    public DefaultKeyDAOImpl() {
        KEY_MAP = new HashMap<>();
    }

    @Override
    public void write(String name, Key key) throws KeyException {
        KEY_MAP.put(name, key);
    }

    @Override
    public Key read(String name) {
        return KEY_MAP.get(name);
    }

    @Override
    public void delete(String name) throws KeyException {
        KEY_MAP.remove(name);
    }
}
