#! /bin/sh
# parameter: directorul 
for i in resources/languages/*;
  # do echo $i/rules;
  # do bin/convert-rules.clj $i/rules;
  # do bin/convert-corpus.clj $i/corpus;
  # do rm $i/corpus/*.clj;
  do rm $i/rules/*.clj;
done
