../build/install/sometest/bin/key-val-client localhost 9100 9102 ops.txt > output_file1.txt &

../build/install/sometest/bin/key-val-client localhost 9100 9102 ops2.txt > output_file2.txt

# sleep time for all the operations to execute
sleep 5

# reading results from file one
client1=1
while read line; do
    result1=$line
    client1=$((n+1))
done < output_file1.txt

# reading results from file two
client2=1
while read line; do
    result2=$line
    client2=$((n+1))
done < output_file2.txt

echo

# comparing results from file
if [ $client1 == $client2 ]
then
    echo "Woho! System is consistent"
else
    echo "Oh no! System is not consistent"
fi
