## Millionaires Problem

Please see the Millionaires-Problem powerpoint file for instructions on how to use this repository.

# How to run

Start by navigating to a terminal window located on this project's root directory.

1. Setup Docker <br />
    `docker run --name millionaires-problem -p 8080:8080 -it -d -v :/sdk -w /sdk ubuntu bash` <br />
    `docker exec -ti millionaires-problem apt update`<br />
    `docker exec -ti millionaires-problem apt install -y openjdk-11-jdk`<br />
2. From your project root, run the following to build the enclave JAR files. <br />
    `cd ./host; gradle clean build; cd ../`
3. Now copy those files to your Docker images <br />
    `docker cp host/build/libs/host.jar millionaires-problem:/tmp/host.jar`<br />
4. Start the Host & Enclave
    `docker exec -ti millionaires-problem java -jar /tmp/host.jar`
