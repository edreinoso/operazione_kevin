
ps aux | grep -ie java | awk '{print $1}'  | xargs kill -9

./build/install/sometest/bin/hello-world-server 9000 9000 9002  &
./build/install/sometest/bin/hello-world-server 9001 9000 9002  &
./build/install/sometest/bin/hello-world-server 9002 9000 9002  &
