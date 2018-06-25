# usage: missing-translation-keys.sh nl_nl

if [ $# -ne 1 ]; then
    echo "missing language to compare with"
    exit
fi

filename=src/main/resources/assets/refinedstorage/lang/$1.lang

if [ ! -f $filename ]; then
    echo "$filename not found"
    exit
fi

BASE_LANG="en_us"

cat src/main/resources/assets/refinedstorage/lang/$BASE_LANG.lang | cut -d "=" -f 1 > a.lang
cat $filename | cut -d "=" -f 1 > b.lang

diff -s -y a.lang b.lang

rm a.lang b.lang
