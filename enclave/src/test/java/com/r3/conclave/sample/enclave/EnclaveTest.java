package com.r3.conclave.sample.enclave;

import com.r3.conclave.host.EnclaveLoadException;
import com.r3.conclave.testing.MockHost;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the enclave fully in-memory in a mock environment.
 */
public class EnclaveTest {
    @Test
    void reverseNumber() throws EnclaveLoadException {
        MockHost<MyEnclave> mockHost = MockHost.loadMock(MyEnclave.class);
        mockHost.start(null, null);
        MyEnclave reverseEnclave = mockHost.getEnclave();

//        assertNull(reverseEnclave.previousResult);
//
//        byte[] response = mockHost.callEnclave("1234".getBytes());
//        assertNotNull(response);
//        assertEquals("4321", new String(response));
//        assertEquals("4321", new String(reverseEnclave.previousResult));
    }
}
