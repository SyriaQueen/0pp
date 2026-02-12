#!/bin/sh

DIR="."
OUT="files_dump.txt"

# إفراغ الملف إذا كان موجود
: > "$OUT"

find "$DIR" -type f | while read -r file; do
  echo "===== PATH =====" >> "$OUT"
  echo "$file" >> "$OUT"
  echo "===== CONTENT =====" >> "$OUT"
  cat "$file" >> "$OUT"
  echo "\n\n" >> "$OUT"
done
