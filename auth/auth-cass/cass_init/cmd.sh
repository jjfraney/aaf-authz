#!/bin/bash 
#
# Engage normal Cass Init, then check for data installation
#
if [ ! -e /aaf_cmd ]; then
  ln -s /opt/app/aaf/cass_init/cmd.sh /aaf_cmd
  chmod u+x /aaf_cmd
fi

function install_cql {
    # Now, make sure data exists
    if [ "$(/usr/bin/cqlsh -e 'describe keyspaces' | grep authz)" = "" ]; then
      for CNT in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15; do
         if [ -z "$(grep 'listening for CQL clients' /var/log/cassandra/system.log)" ]; then
            echo "Waiting for Cassandra to start... Sleep 10"
            sleep 10
         else
            break
         fi
      done
      echo "Initializing Cassandra DB" 
      if [ "`/usr/bin/cqlsh -e 'describe keyspaces' | grep authz`" == "" ]; then
        echo "Docker Installed Basic Cassandra on aaf_cass.  Executing the following "
        echo "NOTE: This creator provided is only a Single Instance. For more complex Cassandra, create independently"
        echo ""
        echo " cd /opt/app/aaf/cass_init"
        cd /opt/app/aaf/cass_init
        echo " cqlsh -f keyspace.cql"
        /usr/bin/cqlsh -f keyspace.cql
        echo " cqlsh -f init.cql"
        /usr/bin/cqlsh -f init.cql
        echo ""
        echo "The following will give you a temporary identity with which to start working, or emergency"
        echo " cqlsh -f temp_identity.cql"
      fi
    fi
}

case "$1" in
  start)
    # Startup like normal
    echo "Cassandra Startup"
    /usr/local/bin/docker-entrypoint.sh 
  ;;
  onap)
	install_cql

	# Change date expiring dat files to more recent
	ID_FILE=/opt/app/aaf/cass_init/sample.identities.dat	
    	if [ -e $ID_FILE ]; then
  	    DATE=$(date "+%Y-%m-%d %H:%M:%S.000+0000" -d "+6 months")
  	    echo $DATE
            CRED="/opt/app/aaf/cass_init/dats/cred.dat"
            # Enter for People
            echo "Default Passwords for Apps"
            for ID in $(grep '|a|' $ID_FILE | sed -e "s/|.*//"); do
               if [ "$ID" = "aaf" ]; then
                  DOMAIN="aaf.osaaf.org";
               else
                  DOMAIN="$ID.onap.org";
               fi
               unset FIRST
               for D in ${DOMAIN//./ }; do
                  if [ -z "$FIRST" ]; then
                    NS="$D"
                    FIRST="N"
                  else
                    NS="$D.$NS"
                  fi
               done
               echo "$ID@$DOMAIN|2|${DATE}|0xd993c5617486296f1b99d04de31633332b8ba1a550038e23860f9dbf0b2fcf95|Initial ID|$NS|53344|" >> $CRED
            done
  	    
	    # Enter for People
            for ID in $(grep '|e|' $ID_FILE | sed -e "s/|.*//"); do
               echo "$ID@people.osaaf.org|2|${DATE}|0xd993c5617486296f1b99d04de31633332b8ba1a550038e23860f9dbf0b2fcf95|Initial ID|org.osaaf.people|53344|" >> $CRED
            done

	    # Change UserRole
            mv dats/user_role.dat tmp
            sed "s/\(^.*|\)\(.*|\)\(.*|\)\(.*\)/\1${DATE}|\3\4/" tmp > dats/user_role.dat

	    # Remove ID File, which is marker for initializing Creds
            rm $ID_FILE
        fi
	bash push.sh
  ;;
esac

