package com.r3.conclave.sample.host;

import com.r3.conclave.host.AttestationParameters;
import com.r3.conclave.host.EnclaveHost;
import com.r3.conclave.host.EnclaveLoadException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostController {

    EnclaveHost enclave;
    long mailID = 0;

    public HostController() throws EnclaveLoadException {

        try {
            EnclaveHost.checkPlatformSupportsEnclaves(true);
            System.out.println("This platform supports enclaves in simulation, debug and release mode.");
        } catch (EnclaveLoadException e) {
            System.out.println("This platform does not support hardware enclaves: " + e.getMessage());
        }
        enclave = EnclaveHost.load("com.r3.conclave.sample.enclave.MyEnclave");

        enclave.start(new AttestationParameters.DCAP(), new EnclaveHost.MailCallbacks() {
            @Override
            public void postMail(byte[] encryptedBytes, String routingHint) {
                receiveMailFromEnclave(encryptedBytes, routingHint);
            }
        });
    }

    // When the host receives mail from a client, it will deliver that mail to the Enclave
    @PostMapping(path = "/sendData")
    public void deliverMailToEnclave(@RequestBody byte[] bytes) {
        enclave.deliverMail(mailID++, bytes, "");
    }

    // This method is called when the enclave sends mail back to the host
    private void receiveMailFromEnclave(byte[] encryptedBytes, String routingHint) {
        // We don't need the enclave to pass anything back to the host so this is unused
    }

    // A GET endpoint used to check that the server is running.
    @GetMapping(path="/status")
    public String status() {
        return "Up and running";
    }

    // A GET endpoint used to retrieve the remote attestation.
    @GetMapping(path="/ra")
    public byte[] get_ra() {
        return enclave.getEnclaveInstanceInfo().serialize();
    }
}
