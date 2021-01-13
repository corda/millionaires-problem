package com.r3.conclave.sample.client;

import com.r3.conclave.client.EnclaveConstraint;
import com.r3.conclave.client.InvalidEnclaveException;
import com.r3.conclave.common.EnclaveInstanceInfo;
import com.r3.conclave.mail.Curve25519KeyPairGenerator;
import com.r3.conclave.mail.MutableMail;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.KeyPair;

/*
TODO
 */

public class DemoScript {

    static private int hostPort = 8080;

    public static void main(String[] args) throws IOException {
        int sequenceNumber = 0;

        // Get the remote attestation from the Enclave
        System.out.println("Retrieving Remote Attestation from Enclave");
        EnclaveInstanceInfo receivedRA = getRa(
                "http://localhost:" + hostPort + "/ra",
                "S:4924CA3A9C8241A3C0AA1A24A407AA86401D2B79FA9FF84932DA798A942166D4 PROD:1 SEC:INSECURE");

        // Generate key pairs for Bob & Alice
        KeyPair bobKey = new Curve25519KeyPairGenerator().generateKeyPair();
        KeyPair aliceKey = new Curve25519KeyPairGenerator().generateKeyPair();

        // Send data to the host/enclave
        submitData(convertSubmitDataToBytes(100, "Bob"), receivedRA, bobKey, sequenceNumber++);
        submitData(convertSubmitDataToBytes(1000, "Alice"), receivedRA, aliceKey, sequenceNumber++);
    }

    // Takes an amount and name and converts it to a byte array
    private static byte[] convertSubmitDataToBytes(int amount, String name) {
        ByteBuffer buffer = ByteBuffer.allocate(4 + name.getBytes().length);
        buffer.putInt(amount); // 4 bytes for amount (int)
        buffer.put(name.getBytes()); // name.length # of bytes for name (string)
        return buffer.array();
    }

    // A wrapper method to sendMail that specifically sends a `SUBMIT` request
    private static void submitData(byte[] bytes, EnclaveInstanceInfo receivedRA, KeyPair myKey, int sequenceNumber) throws IOException {
        sendMail(bytes, receivedRA, "http://localhost:" + hostPort + "/sendData", myKey, "SUBMIT", sequenceNumber);
    }

    // A method that sends a mail object to the host/enclave
    private static void sendMail(byte[] byteArray, EnclaveInstanceInfo receivedRA, String postEndpoint, KeyPair myKey, String topic, int sequenceNumber) throws IOException {
        MutableMail mail = receivedRA.createMail(byteArray);
        mail.setSequenceNumber(sequenceNumber);
        mail.setPrivateKey(myKey.getPrivate());
        mail.setTopic(topic);

        // Encrypt the mail
        byte[] encryptedMail = mail.encrypt();

        // Create a POST request to send the encrypted byte[] to Host server
        URL url = new URL(postEndpoint);
        HttpURLConnection postConn = (HttpURLConnection) url.openConnection();
        postConn.setRequestMethod("POST");
        postConn.setRequestProperty("Content-Type", "image/jpeg");
        postConn.setDoOutput(true);

        try(OutputStream os = postConn.getOutputStream()) {
            os.write(encryptedMail, 0, encryptedMail.length);
            os.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            postConn.getResponseCode();
            postConn.disconnect();
        }
    }

    // Retrieves the remote attestation for the Enclave
    private static EnclaveInstanceInfo getRa(String raEndpoint, String attestationConstraint) throws IOException {
        EnclaveInstanceInfo attestation = null;

        URL url = new URL(raEndpoint);
        HttpURLConnection getConn = (HttpURLConnection)url.openConnection();
        getConn.setRequestMethod("GET");

        try{
            byte[] buf = new byte[getConn.getInputStream().available()];
            getConn.getInputStream().read(buf);
            attestation = EnclaveInstanceInfo.deserialize(buf);
            EnclaveConstraint.parse(attestationConstraint).check(attestation);

        }catch(InvalidEnclaveException e){
            e.printStackTrace();
        }finally {
            getConn.disconnect();
        }
        return attestation;
    }

}
