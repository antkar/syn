#!/bin/bash

D="$(dirname $0)"

java -cp "$D/target/sample-script-1.2.jar" org.antkar.syn.sample.script.ide.IDEMain $*

