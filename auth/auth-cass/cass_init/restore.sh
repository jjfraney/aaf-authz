# echo -n "Password:"
# read  -s PWD
# echo
echo `date`
ENV=DOCKER

CQLSH="/usr/bin/cqlsh -k authz"

cd dats
if [ "$*" = "" ]; then
  DATA=""
  for Tdat in `ls *.dat`; do
    if [ -s "${Tdat}" ]; then
      DATA="$DATA ${Tdat%.dat}"
    fi
  done
else
  DATA="$*"
fi
cd -

echo "You are about to REPLACE the data in the $ENV DB for the following tables:"
echo "$DATA"
echo -n 'If you are VERY sure, type "YES": '
read YES

if [ ! "$YES" = "YES" ]; then
  echo 'Exiting ...'
  exit
fi

UPLOAD=""
for T in $DATA; do
  if [ -s "dats/${T}.dat" ]; then
    echo $T
    case "$T" in
      # 2.1.14 still has NULL problems for COPY.  Fixed in 2.1.15+
      "approval"|"artifact"|"cred"|"ns"|"x509"|"role")
        $CQLSH -e  "truncate $T"
        UPLOAD="$UPLOAD dats/"$T
        ;;
      *)
        $CQLSH -e  "truncate $T; COPY authz.${T} FROM 'dats/${T}.dat' WITH DELIMITER='|'"
        ;;
    esac
  fi
done

if [ ! "$UPLOAD" = "" ]; then
  java -DCASS_ENV=$ENV -jar aaf-auth-batch-*-full.jar Upload $UPLOAD
fi
echo `date`