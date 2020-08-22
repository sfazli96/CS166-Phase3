#! /bin/bash
createdb -h /tmp/$LOGNAME/sockets $LOGNAME"_DB"

echo "Database Name: " $LOGNAME"_DB"

sleep 1

cp *.txt /tmp/$LOGNAME/test/data

sleep 1

psql -h /tmp/$LOGNAME/sockets $LOGNAME"_DB" < chapter5.sql
