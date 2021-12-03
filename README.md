# DS Project  -  distributed KV

You need bash, gradle, protoc, protobuf.

### build
```
./gradlew installDist
```
### launch the servers
```
sh start.sh
```

### write some values to servers
```
./build/install/sometest/bin/key-val-client localhost 9000 9002 ops.txt
```


### kill servers at the end
```
ps aux | grep -ie java | awk '{print $1}'  | xargs kill -9
```
