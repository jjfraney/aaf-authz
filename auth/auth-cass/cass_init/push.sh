#!/bin/bash
#
# Push data from Cassandra ".dat" files
# These are obtained from "gzipped" files, or pre-placed (i.e. initialization) 
#   in the "dats" directory
#
DIR=/opt/app/aaf/cass_init
cd $DIR
if [ ! -e dats ]; then
  if [ -e dat.gz ]; then
     tar -xvf dat.gz
  else 
     echo "No Data to push for Cassandra"
     exit
  fi
fi
cd dats
for T in $(ls *.dat); do
  if [ -s $T ]; then
    cqlsh -e "use authz; COPY ${T%.dat} FROM '$T' WITH DELIMITER='|';"
  fi
done
cd $DIR
#rm -Rf dats
