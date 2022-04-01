package com.glodnet.chain;

import com.glodnet.chain.keys.IKeyService;
import com.glodnet.chain.keys.impl.SM2KeyServiceImpl;
import com.glodnet.chain.util.Mnemonic;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Yelong
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SM2KeyServiceTest {
    private static IKeyService keyService = new SM2KeyServiceImpl(null);
    private static final String HRP = "gnc";
    private static final Map<KEYS, Object> paramMap = new HashMap<>();
    private enum KEYS {
        ADDRESS,
        MNEMONIC,
        FILEPATH
    }


    @Test
    public void test1AddKey() throws Exception {
        Mnemonic mnemonic = keyService.addKey("test2", "12345678");
        assertNotNull(mnemonic.getAddress());
        assertNotNull(mnemonic.getMnemonic());
        assertTrue("Wrong HRP", mnemonic.getAddress().startsWith(HRP));
        assertEquals("Wrong Words Count", 24, mnemonic.getMnemonic().split(" ").length);
        paramMap.put(KEYS.ADDRESS, mnemonic.getAddress());
        paramMap.put(KEYS.MNEMONIC, mnemonic.getMnemonic());
    }

    @Test
    public void test2RecoverKey() {
        String address = keyService.recoverKey("test1", "12345678", (String) paramMap.get(KEYS.MNEMONIC), true, 0, "");
        assertNotNull(address);
        assertTrue("Wrong HRP", address.startsWith(HRP));
        assertEquals("Wrong Address", paramMap.get(KEYS.ADDRESS), address);
    }

    @Test
    public void test3ShowAddress() {
        String address = keyService.showAddress("test1");
        paramMap.get(KEYS.ADDRESS);
        assertNotNull(address);
        assertEquals("Wrong Address", paramMap.get(KEYS.ADDRESS), address);
    }

    @Test
    public void test4DeleteKey()  {
        keyService.deleteKey("test1", "12345678");
    }

    @Test
    public void test5ExportKey() throws Exception {
        String fileName = keyService.exportKeystore("test2", "12345678", "12345678", new File("/tmp/"));
        assertNotNull(fileName);
        paramMap.put(KEYS.FILEPATH, "/tmp/" + fileName);

    }

    @Test
    public void test6ImportKey() throws Exception {
        String address = keyService.importFromKeystore("test1", "12345678", "12345678",(String) paramMap.get(KEYS.FILEPATH));
        assertNotNull(address);
        assertEquals("Wrong Address", paramMap.get(KEYS.ADDRESS), address);
    }

}