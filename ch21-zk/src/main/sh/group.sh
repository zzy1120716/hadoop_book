java CreateGroup localhost zoo

java ListGroup localhost zoo

java JoinGroup localhost zoo duck &
duck_pid=&!
java JoinGroup localhost zoo cow &
cow_pid=&!
java JoinGroup localhost zoo goat &
goat_pid=&!

sleep 5

java ListGroup localhost zoo

kill $goat_pid

sleep 5
sleep 5 # be sure goat process has died

java ListGroup localhost zoo

kill $duck_pid
kill $cow_pid

java DeleteGroup localhost zoo
java ListGroup localhost zoo
