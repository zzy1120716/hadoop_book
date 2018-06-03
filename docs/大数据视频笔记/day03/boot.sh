#!/bin/bash

SERVERS="mini1 mini2 mini3"
PASSWORD=hadoop
BASE_SERVER=172.16.78.81

auto_ssh_copy_id() {
    expect -c "set timeout -1;
        spawn ssh-copy-id $1;
        expect {
            *(yes/no)*  {send -- yes\r;exp_continue;}
            *assword:*  {send -- $2\r;exp_continue;}
            eof         {exit 0;}
        }
    ";
}

ssh_copy_id_to_all() {
    for SERVER in $SERVERS
    do
        auto_ssh_copy_id $SERVER $PASSWORD
    done
}

ssh_copy_id_to_all

for SERVER in $SERVERS
do
    scp install.sh root@$SERVER:/root
    ssh root@$SERVER /root/install.sh
done