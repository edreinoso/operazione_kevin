# DS Project  -  distributed KV

You need bash, gradle, protoc, protobuf.

### build
```
./gradlew installDist
```
### launch the servers
WARNING: run.sh first removes all java processes for convenience - the previously running servers - be sure it is ok for you :)
```
sh run.sh
```

### write a value (hardcoded request code) to server listening on port 9000
```
./build/install/sometest/bin/hello-world-client 9000
```


### kill servers at the end
```
ps aux | grep -ie java | awk '{print $1}'  | xargs kill -9
```
