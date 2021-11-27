


./build/install/sometest/bin/hello-world-client 9000 9002 put 1 2 &
./build/install/sometest/bin/hello-world-client 9000 9002 put 1 3 &
./build/install/sometest/bin/hello-world-client 9000 9002 get 2 1 &
./build/install/sometest/bin/hello-world-client 9000 9002 get 1 1 &

sleep 10
./build/install/sometest/bin/hello-world-client 9000 9002 get 1 1


