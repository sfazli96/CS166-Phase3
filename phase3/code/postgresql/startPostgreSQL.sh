#! /bin/bash
folder=/tmp/$LOGNAME

echo folder

#Clear Folder
rm -rf $folder

#Initialize folders
mkdir $folder
cd $folder
mkdir -p test/data
mkdir sockets

#Initialize the Database
export PGDATA=/tmp/$LOGNAME/test/data

sleep 1

#Initialize DB
initdb

sleep 1

#Starting Database
export PGPORT=5432
pg_ctl -o "-c unix_socket_directories=/tmp/$LOGNAME/sockets" -D $PGDATA -l /tmp/$LOGNAME/logfile start

sleep 1
#Checking of Database is running
pg_ctl status
