# uranus

A tool helps on locating witness class for Apache SkyWalking plugin.

## package

```bash

mvn clean package appassembler:assemble

```

## release

```bash

tar -zcf  uranus-1.0.tar.gz target/uranus

```

## usage

Execute the command to see the usage.

### Print all version information of the specified artifact.

#### examples

```bash

./artifact-versions -gid org.mongodb -aid mongo-java-driver -s

```

### Print whether a certain class exists in a certain version of the package.

#### examples

```bash

./artifact-exists-class -aid mongo-java-driver -c  com.mongodb.client.MongoClientImpl -gid org.mongodb -v 3.7.0

```

### Print all classes of the package.

#### examples

```bash

./all-class -aid mongo-java-driver -gid org.mongodb -v 3.8.0 -s

```

### Print old classes of the package.

Multiple artifacts are separated by commas, groupId, artifactId, and version are separated by colons.

#### examples

```bash

./old-class -c org.mongodb:mongo-java-driver:3.7.0  -n org.mongodb:mongo-java-driver:3.8.0 -s

```

### Print new classes of the package.

Multiple artifacts are separated by commas, groupId, artifactId, and version are separated by colons.

#### examples

```bash

./new-class -c org.mongodb:mongo-java-driver:3.7.0  -n org.mongodb:mongo-java-driver:3.8.0 -s

```

### Print not in old classes and not in new classes of the package.

Multiple artifacts are separated by commas, groupId, artifactId, and version are separated by colons.

#### examples

```bash

./nold-nnew-class -o org.mongodb:mongo-java-driver:3.6.0 -c org.mongodb:mongo-java-driver:3.7.0  -n org.mongodb:mongo-java-driver:3.8.0 -s

```

### Print target class info in artifacts.

Multiple artifacts are separated by commas, groupId, artifactId are separated by colons.

#### examples

```bash

./target-class-info-in-artifacts -c com.mongodb.client.MongoClientImpl -gid org.mongodb  -aid mongo-java-driver -a org.mongodb:mongodb-driver-sync

```