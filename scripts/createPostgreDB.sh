#! /bin/bash
createdb -h /tmp/$LOGNAME/sockets $LOGNAME"_DB"

echo "Database Name: " $LOGNAME"_DB" 